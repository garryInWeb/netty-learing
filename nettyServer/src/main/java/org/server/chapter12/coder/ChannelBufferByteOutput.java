package org.server.chapter12.coder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * Created by zhengtengfei on 2019/1/24.
 */
public class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf byteBuf;

    public ChannelBufferByteOutput(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public void write(int i) throws IOException {
        byteBuf.writeByte(i);
    }

    public void write(byte[] bytes) throws IOException {
        byteBuf.writeBytes(bytes);
    }

    public void write(byte[] bytes, int i, int i1) throws IOException {
        byteBuf.writeBytes(bytes,i,i1);
    }

    public void close() throws IOException {

    }

    public void flush() throws IOException {

    }

    ByteBuf getByteBuf(){
        return byteBuf;
    }
}
