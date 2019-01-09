package org.server.chapter2.nioserver;

/**
 * Created by zhengtengfei on 2018/12/29.
 */
public class QueueIntFlip {

    public int[] elements = null;

    private boolean flipped = false;
    private int capacity = 0;
    private int writePos = 0;
    private int readPos = 0;

    public QueueIntFlip(int capacity) {
        this.capacity = capacity;
        this.elements = new int[capacity];
    }

    public int take(){
        if (!flipped){
            if (readPos < writePos){
                return elements[readPos++];
            }else{
                return -1;
            }
        } else{
            if (readPos == capacity){
                readPos = 0;
                flipped = false;

                if (readPos < writePos){
                    return elements[readPos];
                }else{
                    return -1;
                }
            } else{
                return elements[readPos];
            }
        }
    }

    public boolean put(int element) {
        if(!flipped){
            if(writePos == capacity){
                writePos = 0;
                flipped = true;

                if(writePos < readPos){
                    elements[writePos++] = element;
                    return true;
                } else {
                    return false;
                }
            } else {
                elements[writePos++] = element;
                return true;
            }
        } else {
            if(writePos < readPos ){
                elements[writePos++] = element;
                return true;
            } else {
                return false;
            }
        }

    }
}
