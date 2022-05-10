package com.howcool.handler;

import com.howcool.protocol.Packet;
import com.howcool.protocol.PacketCodeC;
import com.howcool.protocol.request.LoginRequestPacket;
import com.howcool.protocol.response.LoginResponsePacket;
import com.howcool.protocol.response.MessageResponsePacket;
import com.howcool.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    /***
     * 在 handlerAdded channelRegistered 之后 触发
     * @param ctx ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端开始登录");

        //ctx.channel().writeAndFlush(getByteBuf(ctx));

        // 创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUserName("张三");
        loginRequestPacket.setPassword("abc123");

        // 编码
        ByteBuf buffer = PacketCodeC.INSTANCE.encodeOld(loginRequestPacket);

        // 写数据
        ctx.channel().writeAndFlush(buffer);
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1. 获取 ByteBuf
        ByteBuf buffer = ctx.alloc().directBuffer();

        // 2. 准备数据，指定字符串的字符集为 utf-8
        byte[] bytes = "你好，闪电侠!".getBytes(Charset.forName("utf-8"));

        // 3. 填充数据到 ByteBuf
        buffer.writeBytes(bytes);

        return buffer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);

        if (packet instanceof LoginResponsePacket) {
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;

            if (loginResponsePacket.isSuccess()) {
                log.info("客户端登录成功");
                LoginUtil.markAsLogin(ctx.channel());
            } else {
                log.info( "客户端登录失败，原因：" + loginResponsePacket.getReason());
            }
        } else if (packet instanceof MessageResponsePacket) {
            MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
            log.info( "收到服务端的消息: " + messageResponsePacket.getMessage());
        }
    }
}
