package org.server.chapter12.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.server.chapter12.coder.NettyMessageDecoder;
import org.server.chapter12.coder.NettyMessageEncoder;
import org.server.chapter12.constant.NettyConstant;
import org.server.chapter12.handler.HeartBeatReqHandler;
import org.server.chapter12.handler.LoginAuthReqHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();
    public void connect(int port,String host){
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new NettyMessageDecoder(1024*1024,4,4))
                                    .addLast("MessageEncoder",new NettyMessageEncoder())
                                    .addLast("readTimeoutHandler",new ReadTimeoutHandler(50))
                                    .addLast("LoginAuthHandler",new LoginAuthReqHandler())
                                    .addLast("HeartBeatHandler",new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(
                    new InetSocketAddress(host,port),
                    new InetSocketAddress(NettyConstant.LOCALIP,
                            NettyConstant.LOCAL_PORT)
            ).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executor.execute(() -> {
                try{
                    TimeUnit.SECONDS.sleep(5);
                    connect(NettyConstant.PORT,NettyConstant.REMOTEIP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public static void main(String[] args) {
        new NettyClient().connect(NettyConstant.PORT,NettyConstant.REMOTEIP);
    }
}
