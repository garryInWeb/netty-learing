package org.server.chapter10.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by zhengtengfei on 2019/1/10.
 */
public class HttpFileServer {


    public void run(final int port,final String url) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    // 消息解码器
                                    .addLast("http-decoder",new HttpRequestDecoder())
                                    // 将多个消息转换为单一的FullHttpRequest，原因是HTTP解码器在每个HTTP消息中会生成多个消息对象
                                    .addLast("http-aggregator",new HttpObjectAggregator(65536))
                                    // 响应编码
                                    .addLast("http-encoder",new HttpResponseEncoder())
                                    // 支持异步发送大的码流，不占用过多的内存，防止JAVA内存溢出错误
                                    .addLast("http-chunked",new ChunkedWriteHandler())
                                    .addLast("fileServerChunked",new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new HttpFileServer().run(9998,"/nettyServer/src/main/java/org/server/");
    }
}
