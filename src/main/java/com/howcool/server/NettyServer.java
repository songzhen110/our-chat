package com.howcool.server;

import com.howcool.codec.PacketDecoder;
import com.howcool.codec.PacketEncoder;
import com.howcool.codec.Spliter;
import com.howcool.handler.LoginRequestHandler;
import com.howcool.handler.MessageRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(2);

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class).attr(AttributeKey.newInstance("serverName"),"nettyServer")
                .option(ChannelOption.SO_BACKLOG,1024)
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                        System.out.println("🙂️OUR-CHAT，服务器启动中...");
                    }
                })
                .childAttr(AttributeKey.newInstance("clientKey"),"clientValue")
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new Spliter());
                        channel.pipeline().addLast(new PacketDecoder());
                        channel.pipeline().addLast(new LoginRequestHandler());
                        channel.pipeline().addLast(new MessageRequestHandler());
                        channel.pipeline().addLast(new PacketEncoder());
                    }
                });

       bind(serverBootstrap, 1000);
    }

    private static void bind(ServerBootstrap serverBootstrap, int inetPort){
        serverBootstrap.bind(inetPort).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("🙂️OUR-CHAT，端口[" + inetPort + "]绑定成功!");
                } else {
                    System.err.println("😭OUR-CHAT，端口[" + inetPort + "]绑定失败!");
                    bind(serverBootstrap, inetPort + 1);
                }
            }
        });

    }
}
