package org.server.chapter12.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.server.chapter12.Header;
import org.server.chapter12.NettyMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengtengfei on 2019/1/16.
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder{

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx,in);
        if (frame == null){
            return null;
        }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());

        int size = in.readInt();
        if (size > 0){
            Map<String,Object> attch = new HashMap<String,Object>(size);
            int keySize = 0;
            byte[] keyArray;
            String key;
            for (int i = 0; i < size; i++){
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray,"UTF-8");
                attch.put(key,marshallingDecoder.decode(in));
            }
            header.setAttachment(attch);
        }

        if (in.readableBytes() > 4){
            message.setBody(marshallingDecoder.decode(in));
        }
        message.setHeader(header);
        return message;
    }
}
