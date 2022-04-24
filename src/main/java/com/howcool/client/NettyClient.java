package com.howcool.client;

import com.howcool.codec.PacketDecoder;
import com.howcool.codec.PacketEncoder;
import com.howcool.codec.Spliter;
import com.howcool.handler.LoginResponseHandler;
import com.howcool.handler.MessageResponseHandler;
import com.howcool.protocol.PacketCodeC;
import com.howcool.protocol.request.MessageRequestPacket;
import com.howcool.util.LoginUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private final static int MAX_RETRY = 5;
    public static void main(String[] args) {
        EventLoopGroup workGroup = new NioEventLoopGroup(2);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class).attr(AttributeKey.newInstance("clientName"),"nettyClient")
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new Spliter());
                        channel.pipeline().addLast(new PacketDecoder());
                        channel.pipeline().addLast(new LoginResponseHandler());
                        channel.pipeline().addLast(new MessageResponseHandler());
                        channel.pipeline().addLast(new PacketEncoder());
                    }
                });

        connect(bootstrap,"127.0.0.1",1000, MAX_RETRY);

    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
                Channel channel = ((ChannelFuture) future).channel();
                // 连接成功之后，启动控制台线程
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }

        });
    }

    private static void startConsoleThread(Channel channel) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (LoginUtil.hasLogin(channel)) {
                    System.out.println("输入消息发送至服务端: ");
                    Scanner sc = new Scanner(System.in);
                    String line = sc.nextLine();

                    MessageRequestPacket packet = new MessageRequestPacket();
                    packet.setMessage(line);
                    //ByteBuf byteBuf = PacketCodeC.INSTANCE.encodeOld(packet);
                    channel.writeAndFlush(packet);
                }
            }
        }).start();
 }
}
