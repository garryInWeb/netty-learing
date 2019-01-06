package org.zhengtengfei.part1.support;

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
    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        byte[] req = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    /**
     * �ͻ��˺ͷ����tcp��·�����ɹ���netty��nio�̻߳���ô˷�����
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        // ��������Ϣ���͵������
        ctx.writeAndFlush(firstMessage);
    }

    /**
     * ������˷���Ӧ����Ϣʱ�����������
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req,"UTF-8");
        System.out.println("Now is : "+body);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        log.warn("Unexpected exception from downstream :" + cause.getMessage());
        ctx.close();
    }
}
