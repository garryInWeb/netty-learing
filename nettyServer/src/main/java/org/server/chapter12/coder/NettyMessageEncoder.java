package org.server.chapter12.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.server.chapter12.NettyMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengtengfei on 2019/1/16.
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        marshallingEncoder = new MarshallingEncoder();
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, List<Object> list) throws Exception {
        if (nettyMessage == null || nettyMessage.getHeader() == null)
            throw new Exception("The encode message is null");
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt((nettyMessage.getHeader().getCrcCode()));
        sendBuf.writeInt(nettyMessage.getHeader().getLength());
        sendBuf.writeLong(nettyMessage.getHeader().getSessionID());
        sendBuf.writeByte(nettyMessage.getHeader().getType());
        sendBuf.writeByte(nettyMessage.getHeader().getPriority());
        sendBuf.writeInt(nettyMessage.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String,Object> param : nettyMessage.getHeader().getAttachment().entrySet()){
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            value = param.getValue();
            marshallingEncoder.encode(value,sendBuf);
        }
        key = null;
        keyArray = null;
        value = null;
        if (nettyMessage.getBody() != null){
            marshallingEncoder.encode(nettyMessage.getBody(),sendBuf);
        }else{
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4,sendBuf.readableBytes());
    }
}
