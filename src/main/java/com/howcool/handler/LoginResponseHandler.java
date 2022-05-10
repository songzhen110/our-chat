package com.howcool.handler;

import com.howcool.protocol.request.LoginRequestPacket;
import com.howcool.protocol.response.LoginResponsePacket;
import com.howcool.session.Session;
import com.howcool.util.LoginUtil;
import com.howcool.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

@Slf4j
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       /*log.info("客户端开始登录");

        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUserName("张三");
        loginRequestPacket.setPassword("abc123");

        // 写数据 (会把输出对象传递到最后一个ChannelOutboundHandler即PacketEncoder)
        ctx.channel().writeAndFlush(loginRequestPacket);*/
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginResponsePacket loginResponsePacket) throws Exception {

        if (loginResponsePacket.isSuccess()) {
            //LoginUtil.markAsLogin(channelHandlerContext.channel());
            SessionUtil.bindSession(new Session(loginResponsePacket.getUserId(), loginResponsePacket.getUserName()), channelHandlerContext.channel());
            System.out.println("["+loginResponsePacket.getUserId()+"]"+"登陆成功, channel.id="+channelHandlerContext.channel().id());
        } else {
            System.out.println("客户端登录失败，原因：" + loginResponsePacket.getReason());
        }
    }
}
