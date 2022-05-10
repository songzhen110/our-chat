package com.howcool.handler;

import com.howcool.util.LoginUtil;
import com.howcool.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!SessionUtil.hasLogin(ctx.channel())) {
            log.info("用户未登陆，关闭此连接");
            ctx.channel().close();
        } else {
            // 已经登陆的下次则不需要再进入此 ChannelHandler
            ctx.pipeline().remove(this);
            // 已经登陆的继续传递执行(也可写成super.channelRead(ctx,msg);)
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (SessionUtil.hasLogin(ctx.channel())) {
            log.info("当前连接登录验证完毕，无需再次验证, AuthHandler 被移除");
        } else {
            log.info("无登录验证，强制关闭连接!");
        }
        super.handlerRemoved(ctx);
    }
}
