package org.server.chapter2.nioserver.http;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhengtengfei on 2019/1/3.
 */
public class HttpUtil {

    private static final byte[] CONTENT_LENGTH = new byte[]{'C','o','n','t','e','n','t','-','L','e','n','g','t','h'};


    public static int parseHttpRequest(byte[] src,int startIndex,int endIndex,HttpHeaders httpHeaders){

        // parse HTTP request line
        int endOfFirstLine = findNextLineBreak(src,startIndex,endIndex);
        // test
        if (endOfFirstLine == -1) return -1;

        // parse HTTP header line
        int presEndOfHeader = endOfFirstLine + 1;
        int endOfHeader = findNextLineBreak(src,presEndOfHeader,endIndex);

        while (endOfHeader != -1 && endOfHeader != presEndOfHeader + 1){
            if (matches(src,presEndOfHeader,CONTENT_LENGTH)){
                try {
                    findContentLength(src, presEndOfHeader, endIndex, httpHeaders);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            presEndOfHeader = endOfHeader + 1;
            endOfHeader = findNextLineBreak(src,presEndOfHeader,endIndex);
        }

        if (endOfHeader == -1){
            return -1;
        }

        int bodyStartIndex = endOfHeader + 1;
        int bodyEndIndex = bodyStartIndex + httpHeaders.contentLength;

        if (bodyEndIndex <= endIndex){
            httpHeaders.bodyStartIndex = bodyStartIndex;
            httpHeaders.bodyEndIndex = bodyEndIndex;
            return bodyEndIndex;
        }

        return -1;
    }

    private static void findContentLength(byte[] src, int startIndex, int endIndex, HttpHeaders httpHeaders) throws UnsupportedEncodingException {
        int indexOfColon = findNext(src,startIndex,endIndex,(byte)':');

        int index = indexOfColon + 1;
        while (src[index] == ' '){
            index ++;
        }

        int valueStartIndex = index;
        int valueEndIndex = index;
        boolean endOfValueFound = false;

        while(index < endIndex && !endOfValueFound){
            switch (src[index]){
                case '0' : ;
                case '1' : ;
                case '2' : ;
                case '3' : ;
                case '4' : ;
                case '5' : ;
                case '6' : ;
                case '7' : ;
                case '8' : ;
                case '9' : { index++;  break; }

                default: {
                    endOfValueFound = true;
                    valueEndIndex = index;
                }
            }
        }

        httpHeaders.contentLength = Integer.parseInt(new String(src,valueStartIndex,valueEndIndex - valueStartIndex,"UTF-8"));
    }

    private static int findNext(byte[] src, int startIndex, int endIndex, byte b) {
        for (int i = startIndex; i < endIndex; i++){
            if (src[i] == b) return i;
        }
        return -1;
    }

    private static boolean matches(byte[] src, int offset, byte[] value) {
        for (int i = offset,n = 0; n < value.length; i++,n++){
            if (src[i] != value[n]){
                return false;
            }
        }
        return true;
    }

    private static int findNextLineBreak(byte[] src, int startIndex, int endIndex) {
        for (int index = startIndex; index < endIndex; index++){
            if (src[index] == '\n'){
                if (src[index-1] == '\r'){
                    return index;
                }
            }
        }
        return -1;
    }
}
