package com.howcool.handler;

import com.howcool.protocol.request.MessageRequestPacket;
import com.howcool.protocol.response.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageRequestPacket messageRequestPacket) throws Exception {
        channelHandlerContext.channel().writeAndFlush(receiveMessage(messageRequestPacket));
    }

    private MessageResponsePacket receiveMessage(MessageRequestPacket messageRequestPacket){
        System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());

        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
        return messageResponsePacket;
    }
}
