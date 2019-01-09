package org.server.chapter2.nioserver;


/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class MessageBuffer {

    public static int KB = 1024;
    public static int MB = 1024 * 1024;

    private static final int CAPACITY_SMALL  =   4  * KB;
    private static final int CAPACITY_MEDIUM = 128  * KB;
    private static final int CAPACITY_LARGE  = 1024 * KB;


    QueueIntFlip smallMessageBufferFreeBlocks  = new QueueIntFlip(1024); // 1024 free sections
    QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(128);  // 128  free sections
    QueueIntFlip largeMessageBufferFreeBlocks = new QueueIntFlip(16);  // 128  free sections

    byte[]  smallMessageBuffer  = new byte[1024 *   4 * KB];   //1024 x   4KB messages =  4MB.
    byte[]  mediumMessageBuffer = new byte[128  * 128 * KB];   // 128 x 128KB messages = 16MB.
    byte[]  largeMessageBuffer = new byte[16  * 1 * MB];   // 128 x 128KB messages = 16MB.



    private Message message;

    public MessageBuffer() {
        //add all free sections to all free section queues.
        for(int i=0; i<smallMessageBuffer.length; i+= CAPACITY_SMALL){
            this.smallMessageBufferFreeBlocks.put(i);
        }
        for(int i=0; i<mediumMessageBuffer.length; i+= CAPACITY_MEDIUM){
            this.mediumMessageBufferFreeBlocks.put(i);
        }
        for(int i=0; i<largeMessageBuffer.length; i+= CAPACITY_LARGE){
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }


    public Message getMessage() {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if (nextFreeSmallBlock == -1)return null;

        Message message = new Message(this);

        message.shareArray = this.smallMessageBuffer;
        message.capacity = CAPACITY_SMALL;
        message.offset = nextFreeSmallBlock;
        message.length = 0;


        return message;
    }

    /**
     * 扩展消息体容量
     * @param message
     * @return
     */
    public boolean expanMessage(Message message) {
        if (message.capacity == CAPACITY_SMALL){
            return moveMessage(message,this.smallMessageBufferFreeBlocks,this.mediumMessageBufferFreeBlocks,this.mediumMessageBuffer,CAPACITY_MEDIUM);
        }
        return true;
    }

    private boolean moveMessage(Message message, QueueIntFlip srcBlockQueue, QueueIntFlip destBlockQueue, byte[] dest, int capacityMedium) {
        int nextFreeBlock = destBlockQueue.take();
        if (nextFreeBlock == -1) return false;

        System.arraycopy(message.shareArray,message.offset,dest,nextFreeBlock,message.length);
        return false;
    }
}
