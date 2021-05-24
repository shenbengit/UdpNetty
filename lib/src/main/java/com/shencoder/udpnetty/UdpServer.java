package com.shencoder.udpnetty;

import com.shencoder.udpnetty.callback.ReceiveMessageCallback;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ShenBen
 * @date 2021/01/07 14:08
 * @email 714081644@qq.com
 */
public class UdpServer implements Runnable {
    private static final String TAG = "UdpServer->";
    /**
     * 监听的端口号
     */
    private final int inetPort;
    private final ReceiveMessageCallback mReceiveMessageCallback;
    private ExecutorService executorService;

    public UdpServer(int inetPort, ReceiveMessageCallback callback) {
        this.inetPort = inetPort;
        this.mReceiveMessageCallback = callback;
    }

    public int getInetPort() {
        return inetPort;
    }

    @Override
    public void run() {
        //Netty
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Channel channel = null;
        try {
            channel = new Bootstrap()
                    .group(workGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))//解决netty udp接收、发送超过2048字节包
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(UdpServerHandler.class.getSimpleName(), new UdpServerHandler(UdpServer.this));
                        }
                    })
                    .bind(inetPort)
                    .sync()
                    .channel();
            LogUtil.i(TAG + "started");
            channel.closeFuture().await();
        } catch (Exception e) {
            if (mReceiveMessageCallback != null) {
                mReceiveMessageCallback.onException(e);
            }
            if (channel != null) {
                channel.close();
            }
        } finally {
            workGroup.shutdownGracefully();
            LogUtil.i(TAG + "closed");
        }
    }

    public void start() {
        execute(this);
    }

    public void stop() {
        shutdownNow();
    }

    void receiveMessage(String msg, InetSocketAddress sender) {
        if (mReceiveMessageCallback != null) {
            mReceiveMessageCallback.onReceiveMsg(msg, sender);
        }
    }

    private void execute(Runnable runnable) {
        if (executorService == null || executorService.isShutdown()) {
            executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(), runnable1 -> {
                Thread thread = new Thread(runnable1);
                thread.setName("udp-server-" + thread.getId());
                return thread;
            });
        }
        executorService.execute(runnable);
    }

    private void shutdownNow() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
