package com.howcool.handler;

import com.howcool.protocol.Packet;
import com.howcool.protocol.PacketCodeC;
import com.howcool.protocol.request.LoginRequestPacket;
import com.howcool.protocol.request.MessageRequestPacket;
import com.howcool.protocol.response.LoginResponsePacket;
import com.howcool.protocol.response.MessageResponsePacket;
import com.howcool.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Date;

@Slf4j
public class FirstServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        log.info("handlerAdded(ChannelHandlerContext ctx)");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("channelRegistered(ChannelHandlerContext ctx)");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("channelActive(ChannelHandlerContext ctx)");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 服务器收取数据
        ByteBuf byteBuf = (ByteBuf) msg;

        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);

        // 判断是否是登录请求数据包
        if (packet instanceof LoginRequestPacket) {
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            loginResponsePacket.setVersion(packet.getVersion());
            // 登录校验
            if (valid(loginRequestPacket)) {
                LoginUtil.markAsLogin(ctx.channel());
                // 校验成功
                loginResponsePacket.setSuccess(true);
            } else {
                // 校验失败
                loginResponsePacket.setSuccess(false);
                loginResponsePacket.setReason("账号密码校验失败");
            }

            ByteBuf resByteBuf = PacketCodeC.INSTANCE.encodeOld(loginResponsePacket);
            ctx.channel().writeAndFlush(resByteBuf);
        } else if (packet instanceof MessageRequestPacket) {
            // 处理消息
            MessageRequestPacket messageRequestPacket = ((MessageRequestPacket) packet);
            log.info(": 收到客户端消息: " + messageRequestPacket.getMessage());

            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
            ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encodeOld(messageResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }

        log.info("channelRead(ChannelHandlerContext ctx)");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        log.info("channelReadComplete(ChannelHandlerContext ctx)");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        if (loginRequestPacket.hashCode() % 2 == 0) {
            return false;
        }
        return true;
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1. 获取二进制抽象 ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();

        // 2. 准备数据，指定字符串的字符集为 utf-8
        byte[] bytes = ("你好，欢迎 客户端["+ ctx.channel().remoteAddress() +"]").getBytes(Charset.forName("utf-8"));

        // 3. 填充数据到 ByteBuf
        buffer.writeBytes(bytes);

        return buffer;
    }
}
