package org.zhengtengfei.part2.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhengtengfei on 2019/1/5.
 */
@Slf4j
public class TimeClientHandler extends ChannelHandlerAdapter {

    private int counter;

    private byte[] req;

    public TimeClientHandler() {
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    /**
     * 客户端和服务端tcp链路建立成功后，netty的nio线程会调用此方法。
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        // 将请求消息发送到服务端
        ByteBuf message = null;
        for (int i = 0; i < 100; i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    /**
     * 当服务端返回应答消息时调用这个方法
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws UnsupportedEncodingException {
        String body = (String) msg;
        System.out.println("Now is : "+body + " ; the counter is : " + ++ counter);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        log.warn("Unexpected exception from downstream :" + cause.getMessage());
        ctx.close();
    }
}
