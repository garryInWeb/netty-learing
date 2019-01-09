package org.server.chapter7.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.server.chapter7.server.codec.MsgPackEncoder;
import org.server.chapter7.server.codec.MsgpackDecoder;


/**
 * Created by zhengtengfei on 2019/1/9.
 */
public class EchoClient {
    public void connect(String host, int port, final int sendNumber) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    // 在ByteBuf 之前增加2个字节的消息长度
                                    .addLast("frameEncoder",new LengthFieldPrepender(2))
                                    //
                                    .addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2))

                                    .addLast("msgpack decoder",new MsgpackDecoder())
                                    .addLast("msgpack encoder",new MsgPackEncoder())
                                    .addLast(new EchoClientHandler(sendNumber));
                        }
                    });

            ChannelFuture future = bootstrap.connect(host,port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoClient().connect("0.0.0.0",9998,8);
    }
}
