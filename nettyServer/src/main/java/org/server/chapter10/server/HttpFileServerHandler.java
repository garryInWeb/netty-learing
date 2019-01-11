package org.server.chapter10.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * Created by zhengtengfei on 2019/1/10.
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public HttpFileServerHandler(String url) {

    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()){
            sendError(channelHandlerContext,BAD_REQUEST);
            return ;
        }
        if (request.method() != HttpMethod.GET){
            sendError(channelHandlerContext,METHOD_NOT_ALLOWED);
            return ;
        }
        final String uri = request.uri();
        final  String path = sanitizeUri(uri);
        if (path == null){
            sendError(channelHandlerContext,FORBIDDEN);
            return;
        }
        File file = new File(path);
        if (file.isHidden() || !file.exists()){
            sendError(channelHandlerContext,NOT_FOUND);
            return;
        }
        if (!file.isFile()){
            sendError(channelHandlerContext,FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file,"r");
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,OK);
        setContentLength(response,fileLength);
        setContentTypeHeader(response,file);

        if (isKeepAlive(request)){
            response.headers().set(CONNECTION,KEEP_ALIVE);
        }
        channelHandlerContext.write(response);
        ChannelFuture sendFileFuture;
        sendFileFuture = channelHandlerContext.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),channelHandlerContext.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {

            }

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {

            }
        });
    }

    private void setContentTypeHeader(HttpResponse response, File file) {

    }
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private String sanitizeUri(String uri) {
        try{
            uri = URLDecoder.decode(uri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        uri = uri.replace('/',File.separatorChar);
        if (uri.contains(File.separator + '.')
                || uri.contains("." + File.separatorChar)
                || uri.startsWith(".")
                || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
    }

    private void sendError(ChannelHandlerContext channelHandlerContext, HttpResponseStatus methodNotAllowed) {

    }
}
