package org.server.chapter12.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.server.chapter12.Header;
import org.server.chapter12.NettyMessage;
import org.server.chapter12.constant.MessageType;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhengtengfei on 2019/1/25.
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>();
    private String[] whiteList = {"127.0.0.1","10.100.99.142"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_REQ.value()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            if (nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte) -1);
            }else{
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (String whiteIP: whiteList){
                    if (whiteIP.equals(whiteIP)){
                        isOk = true;
                        break;
                    }
                }
                loginResp = isOk ? buildResponse((byte) 0): buildResponse((byte) -1);
                if (isOk){
                    nodeCheck.put(nodeIndex,true);
                }
                ctx.writeAndFlush(loginResp);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte b) {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        nettyMessage.setHeader(header);
        nettyMessage.setBody(b);
        return nettyMessage;
    }

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable throwable){
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(throwable);
    }
}
