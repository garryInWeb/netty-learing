package org.server.chapter12.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.server.chapter12.Header;
import org.server.chapter12.NettyMessage;
import org.server.chapter12.constant.MessageType;

public class LoginAuthReqHandler extends ChannelHandlerAdapter {
    // 链接成功是要发送登录的请求包
    public void channelActive(ChannelHandlerContext ctx){
        ctx.writeAndFlush(buildLoginReq());
    }
    // 响应
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            byte loginResult = (byte) message.getBody();
            if (loginResult != 0){
                // 失败
                ctx.close();
            }else{
                System.out.println("Login is ok : " + message);
                // inbound 用这个
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
