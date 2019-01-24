package org.server.chapter12.coder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * Created by zhengtengfei on 2019/1/24.
 */
public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        this.unmarshaller = MarshallingCodeCFactory.buildUnMarshalling();
    }

    public Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(),objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);
        unmarshaller.start(input);
        Object obj = unmarshaller.readObject();
        in.readerIndex();
        return obj;

    }
}
