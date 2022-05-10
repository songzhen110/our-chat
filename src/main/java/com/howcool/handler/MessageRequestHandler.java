package com.howcool.handler;

import com.howcool.protocol.request.MessageRequestPacket;
import com.howcool.protocol.response.MessageResponsePacket;
import com.howcool.session.Session;
import com.howcool.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
@Slf4j
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageRequestPacket messageRequestPacket) throws Exception {
        receiveMessage(channelHandlerContext,messageRequestPacket);
    }

    private void receiveMessage(ChannelHandlerContext channelHandlerContext, MessageRequestPacket messageRequestPacket){
        log.info("收到客户端消息: " + messageRequestPacket.toString());

        String toUserId = messageRequestPacket.getToUserId();
        String message = messageRequestPacket.getMessage();

        // 1.拿到消息发送方的会话信息
        Session session = SessionUtil.getSession(channelHandlerContext.channel());

        // 2.通过消息发送方的会话信息构造要发送的消息
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        messageResponsePacket.setFromUserId(session.getUserId());
        messageResponsePacket.setFromUserName(session.getUserName());
        messageResponsePacket.setMessage(message);

        // 3.拿到消息接收方的 channel
        Channel toUserChannel = SessionUtil.getChannel(toUserId);

        // 将消息发送给消息接收方
        if (toUserChannel != null && SessionUtil.hasLogin(toUserChannel)) {
            // 消息接收方在线，就转发出去
            toUserChannel.writeAndFlush(messageResponsePacket);
        } else {
            // 消息接收方不在线，就告诉消息发送方发送失败
            //channelHandlerContext.channel().writeAndFlush(messageResponsePacket);
            log.error("[" + toUserId + "] 不在线，发送失败!");
        }
    }
}
