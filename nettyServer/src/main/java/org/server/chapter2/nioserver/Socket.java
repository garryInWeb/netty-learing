package org.server.chapter2.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class Socket {
    public long socketId = 0;

    public SocketChannel socketChannel;

    public IMessageReader messageReader = null;
    public MessageWrite messageWrite = null;

    public boolean endOfStreamReached = false;

    public Socket(SocketChannel socketChannel){
        this.socketChannel = socketChannel;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while(bytesRead > 0){
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if (bytesRead == -1){
            this.endOfStreamReached = true;
        }
        return totalBytesRead;
    }

    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWrite = this.socketChannel.write(byteBuffer);
        int totalBtesRead = bytesWrite;

        while(bytesWrite > 0 && byteBuffer.hasRemaining()){
            bytesWrite = this.socketChannel.write(byteBuffer);
            totalBtesRead += bytesWrite;
        }

        return totalBtesRead;
    }
}
