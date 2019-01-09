package org.server.part2.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by zhengtengfei on 2019/1/5.
 */
// 对网络事件进行读写操作
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    public void channelRead(ChannelHandlerContext ctx,Object msg) throws UnsupportedEncodingException {
        // 类似于java.nio.ByteBuffer,通过readablesBytes方法可以获取缓冲区可读的字节数
        String body = (String) msg;
        // 根据字节数创建byte数组，通过ByteBuf的readBytes 方法将缓冲区中的字节数组复制到新建的byte数组中
        System.out.println("The time server receiver order: " + body + " ; the counter is :" + ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(
                System.currentTimeMillis()).toString() : "BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        // 异步发送应答消息
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将消息发送队列中的消息写入到SocketChannel中发送给对方。
        // netty的write方法不直接将消息写入socketChannel中，调用write方法值是把待发送的消息放到发送缓冲数组中，
        // 再通过调用flush方法，将发送缓冲区中的消息全部写道socket channel中
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        ctx.close();
    }
}
