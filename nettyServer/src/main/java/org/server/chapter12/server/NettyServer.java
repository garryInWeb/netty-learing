package org.server.chapter12.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.server.chapter12.coder.NettyMessageDecoder;
import org.server.chapter12.coder.NettyMessageEncoder;
import org.server.chapter12.constant.NettyConstant;
import org.server.chapter12.handler.HeartBeatRespHandler;
import org.server.chapter12.handler.LoginAuthRespHandler;

/**
 * Created by zhengtengfei on 2019/1/28.
 */
public class NettyServer {

    private static final Log LOG = LogFactory.getLog(NettyServer.class);


    public void bind() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new NettyMessageDecoder(1024*1024,4,4))
                                .addLast(new NettyMessageEncoder())
                                .addLast("readTimeoutHandler",new ReadTimeoutHandler(50))
                                .addLast(new LoginAuthRespHandler())
                                .addLast("HeartBeatHandler",new HeartBeatRespHandler());
                    }
                });
        bootstrap.bind(NettyConstant.REMOTEIP,NettyConstant.PORT).sync();
        LOG.info("Netty server start ok : "
                + (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().bind();
    }
}
