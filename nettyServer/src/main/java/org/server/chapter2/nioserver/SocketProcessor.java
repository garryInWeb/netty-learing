package org.server.chapter2.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class SocketProcessor implements Runnable{

    private Queue<Socket> inboundSocketQueue = null;

    private MessageBuffer readMessageBuffer = null;
    private MessageBuffer writeMessageBuffer = null;

    private IMessageReaderFactory messageReaderFactory = null;

    private Queue<Message> outboundMessageQueue = new LinkedList<>();

    private Map<Long,Socket> socketMap = new HashMap<>();

    private ByteBuffer readByteBuffer = ByteBuffer.allocate(1024*1024);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocate(1024*1024);
    private Selector readSelector = null;
    private Selector writeSelector = null;

    private IMessageProcessor messageProcessor = null;
    private WriteProxy writeProxy = null;

    private long nextSocketId = 16 * 1024; //start incoming socket ids from 16K - reserve bottom ids for pre-defined sockets (servers).

    private Set<Socket> emptyToNonEmptySockets = new HashSet<>();
    private Set<Socket> nonEmptyToEmptySockets = new HashSet<>();


    public SocketProcessor(Queue<Socket> inboundSocketQueue, MessageBuffer readMessageBuffer, MessageBuffer writeMessageBuffer, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) throws IOException {
        this.inboundSocketQueue = inboundSocketQueue;
        this.readMessageBuffer = readMessageBuffer;
        this.writeMessageBuffer = writeMessageBuffer;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;

        this.writeProxy = new WriteProxy(writeMessageBuffer,this.outboundMessageQueue);
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();

    }

    @Override
    public void run() {
        while(true){
            try{
                executeCycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeCycle() throws IOException {
        takeNewSocket();
        readFromSockets();
        writeToSockets();
    }

    private void writeToSockets() throws IOException {
        takeNewOutboundMessages();
        cancelEmptySockets();
        registerNonEmptySockets();

        int writeReady = this.writeSelector.selectNow();
        if (writeReady > 0){
            Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key =  iterator.next();
                Socket socket = (Socket) key.attachment();

                socket.messageWrite.write(socket,this.writeByteBuffer);

                if (socket.messageWrite.isEmpty()){
                    this.nonEmptyToEmptySockets.add(socket);
                }

                iterator.remove();
            }

            selectionKeys.clear();
        }
    }

    private void  registerNonEmptySockets() throws ClosedChannelException {
        for (Socket socket : emptyToNonEmptySockets){
            socket.socketChannel.register(this.writeSelector,SelectionKey.OP_WRITE,socket);
        }
        emptyToNonEmptySockets.clear();
    }

    private void cancelEmptySockets() {
        for (Socket socket : nonEmptyToEmptySockets){
            SelectionKey key = socket.socketChannel.keyFor(this.writeSelector);
            key.cancel();
        }
        nonEmptyToEmptySockets.clear();
    }

    private void takeNewOutboundMessages() {
        Message outMessage = this.outboundMessageQueue.poll();

        while (outMessage != null){
            Socket socket = this.socketMap.get(outMessage.socketId);

            if (socket != null){
                MessageWrite messageWrite = socket.messageWrite;
                if (messageWrite.isEmpty()){
                    messageWrite.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    emptyToNonEmptySockets.add(socket);
                }
            }
            outMessage = this.outboundMessageQueue.poll();
        }
    }

    private void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0){
            Set<SelectionKey> selectionKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();

                readFromSocket(key);

                keyIterator.remove();
            }
            selectionKeys.clear();
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        Socket socket = (Socket) key.attachment();
        socket.messageReader.read(socket,this.readByteBuffer);

        List<Message> fullMessage = socket.messageReader.getMessage();
        if (fullMessage.size() > 0){
            for(Message message : fullMessage){
                message.socketId = socket.socketId;
                // 自定义的实现取处理消息体
                this.messageProcessor.processor(message,writeProxy);
            }
            fullMessage.clear();
        }

        if (socket.endOfStreamReached){
            System.out.println("Socket closed:" +socket.socketId);
            this.socketMap.remove(socket.socketId);
            key.attach(null);
            key.cancel();
            key.channel().close();
        }

    }

    /**
     * 从队列中获取全部的socket，放到map里面去，并且把socket的channel注册到selector中
     * @throws IOException
     */
    private void takeNewSocket() throws IOException {
        Socket socket = this.inboundSocketQueue.poll();

        while(socket != null){
            socket.socketId = nextSocketId++;
            socket.socketChannel.configureBlocking(false);

            socket.messageReader = this.messageReaderFactory.createMessageReader();
            socket.messageReader.init(this.readMessageBuffer);

            socket.messageWrite = new MessageWrite();

            this.socketMap.put(socket.socketId,socket);

            SelectionKey key = socket.socketChannel.register(this.readSelector,SelectionKey.OP_READ);

            key.attach(socket);

            socket = inboundSocketQueue.poll();
        }
    }
}
