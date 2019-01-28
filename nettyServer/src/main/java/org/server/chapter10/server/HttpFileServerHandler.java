package org.server.chapter10.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * Created by zhengtengfei on 2019/1/10.
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    private String uri;

    public HttpFileServerHandler(String url) {
        this.uri = url;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 请求消息解码结果判断
        if (!request.getDecoderResult().isSuccess()){
            sendError(ctx,BAD_REQUEST);
            return ;
        }
        if (request.getMethod() != HttpMethod.GET){
            sendError(ctx,METHOD_NOT_ALLOWED);
            return ;
        }
        final String uri = request.getUri();
        final String path = sanitizeUri(uri);
        if (path == null){
            sendError(ctx,FORBIDDEN);
            return;
        }
        File file = new File(path);
        if (file.isHidden() || !file.exists()){
            sendError(ctx,NOT_FOUND);
            return;
        }
        if (file.isDirectory()){
            if (uri.endsWith("/")){
                sendListing(ctx,file);
            }else{
                sendRedirect(ctx,uri + "/");
            }
            return;
        }
        if (!file.isFile()){
            sendError(ctx,FORBIDDEN);
            return;
        }
        // nio 提供的读文件方法
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file,"r");
        long fileLength = randomAccessFile.length();
        // 响应
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,OK);
        // netty提供的方法设置响应体长度
        setContentLength(response,fileLength);
        // 设置相应的媒体类型
        setContentTypeHeader(response,file);

        if (isKeepAlive(request)){
            response.headers().set(CONNECTION,KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture sendFileFuture;
        // ChunkedFile 直接将文件写入到发送缓冲区中
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                if (total < 0){
                    System.err.println("Transfer progress : " + progress);
                }else{
                    System.err.println("Transfer progress: " + progress + "/" + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                System.out.println("Transfer complete");
            }
        });
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!isKeepAlive(request)){
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void sendRedirect(ChannelHandlerContext ctx, String newUrl) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,FOUND);
        response.headers().set(LOCATION,newUrl);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendListing(ChannelHandlerContext ctx, File dir) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,OK);
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append(" 目录： ");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>连接：<a href=\"../\">..</a></li>\r\n");
        for (File file : dir.listFiles()){
            if (file.isHidden() || !file.canRead()){
                continue;
            }
            String name = file.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()){
                continue;
            }
            buf.append("<li>连接：<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    // 设置文件的mime type
    private void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,mimetypesFileTypeMap.getContentType(file.getPath()));
    }

    private String sanitizeUri(String uri) {
        try{
            uri = URLDecoder.decode(uri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!uri.startsWith(this.uri)){
            return null;
        }
        if (!uri.startsWith("/")){
            return null;
        }
        // 替换为系统分隔符
        uri = uri.replace('/',File.separatorChar);
        if (uri.contains(File.separator + '.')
                || uri.contains("." + File.separatorChar)
                || uri.startsWith(".")
                || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        //
        return System.getProperty("user.dir") + File.separator + uri;
    }

    private void sendError(ChannelHandlerContext channelHandlerContext, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,Unpooled.copiedBuffer("Failure: "+ status.toString()
        + "\r\n",CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE,"text/plain;charset=UTF-8");
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
