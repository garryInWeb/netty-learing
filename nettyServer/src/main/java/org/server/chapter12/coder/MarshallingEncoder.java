package org.server.chapter12.coder;


import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * Created by zhengtengfei on 2019/1/24.
 */
public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodeCFactory.buildMarshalling();
    }
    protected void encode(Object msg,ByteBuf buf) throws IOException {
        // 已写的容量
        int lengthPos = buf.writerIndex();
        buf.writeBytes(LENGTH_PLACEHOLDER);
        ChannelBufferByteOutput output = new ChannelBufferByteOutput(buf);
        // 对对象进行自定义加密编码
        marshaller.start(output);
        marshaller.writeObject(msg);
        marshaller.finish();
        buf.setInt(lengthPos,buf.writerIndex() - lengthPos - 4);
    }
}
