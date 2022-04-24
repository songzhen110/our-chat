package com.howcool.handler;

import com.howcool.protocol.request.LoginRequestPacket;
import com.howcool.protocol.response.LoginResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestPacket loginRequestPacket) throws Exception {
        channelHandlerContext.channel().writeAndFlush(login(loginRequestPacket));
    }

    private LoginResponsePacket login(LoginRequestPacket loginRequestPacket){
        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        loginResponsePacket.setVersion(loginRequestPacket.getVersion());
        // 登录校验
        if (valid(loginRequestPacket)) {
            // 校验成功
            loginResponsePacket.setSuccess(true);
        } else {
            // 校验失败
            loginResponsePacket.setSuccess(false);
            loginResponsePacket.setReason("账号密码校验失败");
        }

        return loginResponsePacket;
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        if (!loginRequestPacket.getUsername().equals("zhangsan")) {
            return false;
        }
        Object
        return true;
    }

}
