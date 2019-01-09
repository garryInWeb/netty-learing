package org.server.chapter2.nioserver;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class Server {

    private SocketAccepter socketAccepter = null;
    private SocketProcessor socketProcessor = null;

    private final int tcpPort;
    private final IMessageReaderFactory messageReaderFactory;
    private final IMessageProcessor messageProcessor;

    public Server(int tcpPort, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor){
        this.tcpPort = tcpPort;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException {
        Queue socketQueue = new ArrayBlockingQueue(1024);

        this.socketAccepter = new SocketAccepter(tcpPort,socketQueue);

        MessageBuffer readMessageBuffer = new MessageBuffer();
        MessageBuffer writeMessageBuffer = new MessageBuffer();

        this.socketProcessor = new SocketProcessor(socketQueue,readMessageBuffer,writeMessageBuffer,messageReaderFactory,messageProcessor);

        Thread accepterThread = new Thread(this.socketAccepter);
        Thread processorThread = new Thread(this.socketProcessor);

        accepterThread.start();
        processorThread.start();

    }
}
