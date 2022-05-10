package com.howcool.server;

import com.howcool.codec.PacketDecoder;
import com.howcool.codec.PacketEncoder;
import com.howcool.codec.Spliter;
import com.howcool.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    public static void main(String[] args) {

        // 创建两个线程组，一个用于接收连接，一个用于处理读写数据
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(2);

        // 服务引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workGroup)
                // 指定IO模型 可以是 NioServerSocketChannel(NIO非阻塞) 或 OioServerSocketChannel(BIO阻塞)
                .channel(NioServerSocketChannel.class)
                // 用于设置服务端接收连接的方式为队列，以及队列的大小 未连接队列(SYN) 和 已连接队列(ACCEPT)
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 用于设置服务端Handler 实现监控连接是否有效
                .option(ChannelOption.SO_KEEPALIVE,true)
                // 用于设置服务端handler 禁用nagle算法，保证高实时性
                .option(ChannelOption.TCP_NODELAY, true)
                // 用于设置服务端childHandler 实现监控连接是否有效
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 用于设置服务端childHandler 禁用nagle算法，保证高实时性
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 用于指定NioServerSocketChannel在服务端启动过程中的一些逻辑，通常情况下呢，我们用不着这个方法
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                        log.info("serverBootstrap initChannel handler execute success");
                    }
                })
                // 用于指定处理新连接数据的读写处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        //channel.pipeline().addLast(new FirstServerHandler());
                        channel.pipeline().addLast(new Spliter());
                        channel.pipeline().addLast(new PacketDecoder());
                        channel.pipeline().addLast(new LoginRequestHandler());
                        channel.pipeline().addLast(new AuthHandler());
                        channel.pipeline().addLast(new MessageRequestHandler());
                        channel.pipeline().addLast(new MyOutboundHandler());
                        channel.pipeline().addLast(new PacketEncoder());
                    }
                });
        // 设置自定义属性
        attr(serverBootstrap);

        // 设置服务器监听端口
        bind(serverBootstrap, 1000);
    }

    private static void attr(ServerBootstrap serverBootstrap){
        serverBootstrap.attr(AttributeKey.newInstance("serverKey"),"serverValue");
        serverBootstrap.childAttr(AttributeKey.newInstance("childKey"),"childValue");
    }

    private static void bind(ServerBootstrap serverBootstrap, int inetPort){
        serverBootstrap.bind(inetPort).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    log.info("OUR-CHAT，端口[" + inetPort + "]绑定成功!");
                } else {
                    log.info("OUR-CHAT，端口[" + inetPort + "]绑定失败!");
                    bind(serverBootstrap, inetPort + 1);
                }
            }
        });

    }
}
