package com.demo.netty.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HelloServer {

    /**
     * 服务端监听的端口地址
     */
    private static final int portNumber = 8080;

    public static void main(String[] args) throws InterruptedException {
        //用于处理服务器端接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //进行网络通信（读写）
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //辅助工具类，用于服务器通道的一系列配置
            ServerBootstrap b = new ServerBootstrap();
            //绑定两个线程组
            b.group(bossGroup, workerGroup);
            //指定NIO的模式
            b.channel(NioServerSocketChannel.class);
            //配置具体的数据处理方式
            b.childHandler(new HelloServerInitializer());

            /**
             * 对于ChannelOption.SO_BACKLOG的解释：
             * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，
             * 会发送带有SYN标志的包（第一次握手），服务器端接收到客户端发送的SYN时，
             * 向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，
             * 然后服务器接收到客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，
             * 连接完成，应用程序的accept会返回。也就是说accept从B队列中取出完成了三次握手的连接。
             * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，
             * 新的连接将会被TCP内核拒绝。所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，
             * 导致新的客户端无法连接。要注意的是，backlog对程序支持的连接数并无影响，
             * backlog影响的只是还没有被accept取出的连接
             */
//          b.option(ChannelOption.SO_BACKLOG, 128) //设置TCP缓冲区
//          b.option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
//          b.option(ChannelOption.SO_RCVBUF, 32 * 1024) //设置接受数据缓冲大小
//          b.childOption(ChannelOption.SO_KEEPALIVE, true); //保持连接

            // 服务器绑定端口监听
            ChannelFuture f = b.bind(portNumber).sync();
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

            // 可以简写为
//             b.bind(portNumber).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}