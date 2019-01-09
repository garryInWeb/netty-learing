package org.server.chapter1.practice02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by zhengtengfei on 2018/12/28.
 */
public class NIOSocketClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("10.100.99.142",9899));

        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.put("ggggggg".getBytes());
        buf.flip();
        while(buf.hasRemaining()) {
            int bytesRead = socketChannel.write(buf);
        }
        buf.clear();
        int byteRead = socketChannel.read(buf);
        readChannel(socketChannel, buf, byteRead);
    }

    public static void readChannel(SocketChannel socketChannel, ByteBuffer buf, int byteRead) throws IOException {
        buf.flip();
        byte[] bytes = new byte[1024*1024];
        int index = 0;
        while (buf.hasRemaining()){
            bytes[index] = buf.get();
            index ++;
        }
        System.out.println(Arrays.toString(bytes));
        buf.clear();
    }
}
