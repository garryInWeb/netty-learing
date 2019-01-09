package org.server.chapter2.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class SocketAccepter implements Runnable{

    private int tcpPort = 0;
    private ServerSocketChannel serverSocketChannel = null;
    private Queue socketQueue = null;

    public SocketAccepter(int tcpPort, Queue socketQueue) {
        this.tcpPort = tcpPort;
        this.socketQueue = socketQueue;
    }

    @Override
    public void run() {
        try{
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while(true){
            try{
                SocketChannel socketChannel = this.serverSocketChannel.accept();

                System.out.println("accept " + socketChannel);

                this.socketQueue.add(new Socket(socketChannel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
