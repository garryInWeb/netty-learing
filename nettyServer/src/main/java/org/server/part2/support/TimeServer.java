package org.server.part2.support;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by zhengtengfei on 2019/1/5.
 */
public class TimeServer {
    public void bind(int port){
        // NioEventLoopGroup 是个线程组，包含了一组NIO线程
        // 用于服务端接受客户端的连接
        EventLoopGroup boxxGroup = new NioEventLoopGroup();
        // 用户进行socketchannel的网络读写
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            // 启动NIO服务端的辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boxxGroup,workGroup)
                    // 对应JDK NIO中的 serverSocketChannel
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    // I/O 事件的处理类
                    .childHandler(new ChildChannelHandler());
            // 监听端口，同步阻塞方法等待绑定操作完成
            // channelFuture类似于java.util.concurrent.Future，用于异步操作的通知回调
            ChannelFuture f = bootstrap.bind(port).sync();
            // 阻塞等待服务端链路关闭后才退出main函数
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boxxGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) {
        int port = 9999;
        if (args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        new TimeServer().bind(port);
    }
}
