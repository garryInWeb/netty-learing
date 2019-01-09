package org.server.chapter7.server.support;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.server.chapter7.server.codec.MsgPackEncoder;
import org.server.chapter7.server.codec.MsgpackDecoder;

/**
 * Created by zhengtengfei on 2019/1/9.
 */
public class EchoServer {
    public void bind(int port){
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workergroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossgroup,workergroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("frameEncoder",new LengthFieldPrepender(2))
                                    .addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2))
                                    .addLast("decoder",new MsgpackDecoder())
                                    .addLast("encoder",new MsgPackEncoder())
                                    .addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossgroup.shutdownGracefully();
            workergroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new EchoServer().bind(9998);
    }
}
