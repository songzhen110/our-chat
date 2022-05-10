package com.howcool.handler;

import com.howcool.protocol.request.LoginRequestPacket;
import com.howcool.protocol.response.LoginResponsePacket;
import com.howcool.session.Session;
import com.howcool.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    private static AtomicLong connectCount = new AtomicLong(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("connectCount = {},channelHashcode = {}", connectCount.incrementAndGet(), ctx.channel().hashCode());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestPacket loginRequestPacket) throws Exception {
        channelHandlerContext.channel().writeAndFlush(login(channelHandlerContext,loginRequestPacket));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindSession(ctx.channel());
        super.channelInactive(ctx);
    }

    private LoginResponsePacket login(ChannelHandlerContext channelHandlerContext, LoginRequestPacket loginRequestPacket){
        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        loginResponsePacket.setVersion(loginRequestPacket.getVersion());
        // 登录校验
        if (valid(loginRequestPacket)) {
            System.out.println("[" + loginRequestPacket.getUserId() + "]\t登陆成功");
            // 校验成功
            loginResponsePacket.setUserId(loginRequestPacket.getUserId());
            loginResponsePacket.setUserName(loginRequestPacket.getUserName());
            loginResponsePacket.setSuccess(true);
            SessionUtil.bindSession(new Session(loginRequestPacket.getUserId(), loginRequestPacket.getUserName()), channelHandlerContext.channel());
        } else {
            // 校验失败
            loginResponsePacket.setSuccess(false);
            loginResponsePacket.setReason("账号密码校验失败");
        }

        return loginResponsePacket;
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        //TODO 登陆逻辑
        return true;
    }

}
