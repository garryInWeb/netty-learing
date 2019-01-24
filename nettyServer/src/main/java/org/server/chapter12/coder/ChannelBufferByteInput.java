package org.server.chapter12.coder;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

import java.io.IOException;

/**
 * Created by zhengtengfei on 2019/1/24.
 */
public class ChannelBufferByteInput implements ByteInput {

    private final ByteBuf buffer;

    public ChannelBufferByteInput(ByteBuf buf) {
        buffer = buf;
    }

    public int read() throws IOException {
        if (buffer.isReadable()){
            return buffer.readByte() & 0xff;
        }
        return -1;
    }

    public int read(byte[] bytes) throws IOException {
        return read(bytes,0,bytes.length);
    }

    public int read(byte[] bytes, int dstIndex, int length) throws IOException {
        int available = available();
        if (available == 0){
            return -1;
        }
        length = Math.min(available,length);
        buffer.readBytes(bytes,dstIndex,length);
        return length;
    }

    public int available() throws IOException {
        return buffer.readableBytes();
    }

    public long skip(long bytes) throws IOException {
        int readable = buffer.readableBytes();
        if (readable < bytes){
            bytes = readable;
        }
        buffer.readerIndex((int)(buffer.readerIndex() + bytes));
        return bytes;
    }

    public void close() throws IOException {

    }
}
