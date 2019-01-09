package org.server.chapter1.practice02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by zhengtengfei on 2018/12/28.
 */
public class NIOSocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(9899));

        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
            int byteRead = socketChannel.read(byteBuffer);

            NIOSocketClient.readChannel(socketChannel, byteBuffer, byteRead);

            byteBuffer.clear();
            byteBuffer.flip();

            String accept = "Sever accept";
            byteBuffer.put(accept.getBytes());
            socketChannel.write(byteBuffer);
        }

    }
}
