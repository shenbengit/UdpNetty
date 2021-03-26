package com.shencoder.udpnetty;

import com.shencoder.udpnetty.bean.MessageBean;
import com.shencoder.udpnetty.callback.DiscardMessageCallback;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ShenBen
 * @date 2021/01/07 13:58
 * @email 714081644@qq.com
 */
class UdpClient implements Runnable {

    public static final String TAG = "UdpClient->";

    private ExecutorService executorService;

    private Channel mChannel;
    private final Lock mLock = new ReentrantLock();
    private final List<MessageBean> msgList = new ArrayList<>(20);
    /**
     * 流水号
     */
    private int mSerialNumber = 0;

    private DiscardMessageCallback mDiscardMessageCallback;

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = null;
        //消息重发任务
        if (UdpManager.getInstance().getUdpConfig().isResendEnable()) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1, runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("scheduled-resend-" + thread.getId());
                return thread;
            });
            //启用重发功能
            scheduledExecutorService.scheduleAtFixedRate(this::needResend, 1000L, UdpManager.getInstance().getUdpConfig().getResendInterval(), TimeUnit.MILLISECONDS);
        }

        //Netty
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            mChannel = new Bootstrap()
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
                            pipeline.addLast(UdpClientHandler.class.getSimpleName(), new UdpClientHandler(UdpClient.this));
                        }
                    })
                    .bind(0)
                    .sync()
                    .channel();
            LogUtil.i(TAG + "started");
            mChannel.closeFuture().await();
        } catch (InterruptedException ignore) {
            if (mChannel != null) {
                mChannel.close();
            }
        } finally {
            workGroup.shutdownGracefully();
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdownNow();
            }
            mLock.lock();
            try {
                msgList.clear();
            } finally {
                mLock.unlock();
            }
            LogUtil.i(TAG + "closed");
        }
    }

    void setDiscardMessageCallback(DiscardMessageCallback callback) {
        mDiscardMessageCallback = callback;
    }

    void start() {
        execute(this);
    }

    void stop() {
        shutdownNow();
    }

    /**
     * 发送消息
     *
     * @param msg      发送的消息
     * @param isResend 是否是重新发送,false:首次直接发送，true:重发
     */
    public void sendMessage(MessageBean msg, boolean isResend) {
        if (mChannel != null) {
            final int number;
            if (!isResend) {
                ++mSerialNumber;
                number = mSerialNumber;
                mLock.lock();
                try {
                    msgList.add(msg);
                } finally {
                    mLock.unlock();
                }
            } else {
                number = msg.getSerialNumber();
            }
            msg.setResendTimes(msg.getResendTimes() + 1);
            msg.setSendTimeMillis(System.currentTimeMillis());
            msg.setSerialNumber(number);
            byte[] msgArray = msg.getMsg().getBytes(Charset.forName("gb2312"));
            byte[] byteArray = new byte[Constant.HEADER_LENGTH + msgArray.length];
            byte[] lengthArray = AppUtil.intToByteArray(byteArray.length);
            byte[] idArray = AppUtil.intToByteArray(1);
            byte[] numberArray = AppUtil.intToByteArray(number);
            System.arraycopy(lengthArray, 0, byteArray, 0, lengthArray.length);
            System.arraycopy(idArray, 0, byteArray, 4, idArray.length);
            System.arraycopy(numberArray, 0, byteArray, 8, numberArray.length);
            System.arraycopy(msgArray, 0, byteArray, 12, msgArray.length);
            mChannel.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer(byteArray),
                            new InetSocketAddress(msg.getIp(), msg.getPort())
                    )
            )
                    .addListener(future -> {
                        if (future != null) {
                            LogUtil.i(TAG + "send to ip : " + msg.getIp() + ":" + msg.getPort() + ", isSuccess: " + future.isSuccess() + ", serial number: " + number);
                        }
                    });
        }
    }

    /**
     * 删除消息列表中的消息
     *
     * @param numId 需要删除的消息的id
     */
    void removeMsg(int numId) {
        mLock.lock();
        try {
            Iterator<MessageBean> iterator = msgList.iterator();
            while (iterator.hasNext()) {
                MessageBean bean = iterator.next();
                if (bean.getSerialNumber() == numId) {
                    iterator.remove();
                }
            }
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 遍历列表，判断是否存在需要重新发送的信息
     */
    private void needResend() {
        UdpConfig udpConfig = UdpManager.getInstance().getUdpConfig();
        int resendLimit = udpConfig.getResendLimit();
        long resendInterval = udpConfig.getResendInterval();
        mLock.lock();
        try {
            Iterator<MessageBean> iterator = msgList.iterator();
            while (iterator.hasNext()) {
                MessageBean bean = iterator.next();
                if (bean.getResendTimes() >= resendLimit) {
                    iterator.remove();
                    if (mDiscardMessageCallback != null) {
                        mDiscardMessageCallback.onDiscardMsg(bean);
                    }
                    LogUtil.e(TAG + "send to ip : " + bean.getIp() + ", msg's serial number :" + bean.getSerialNumber() + ", more than " + resendLimit + ", removed.");
                    continue;
                }
                //3秒后重新发送
                if ((System.currentTimeMillis() - bean.getSendTimeMillis()) >= resendInterval) {
                    sendMessage(bean, true);
                }
            }
        } finally {
            mLock.unlock();
        }
    }

    private void execute(Runnable runnable) {
        if (executorService == null || executorService.isShutdown()) {
            executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(), runnable1 -> {
                Thread thread = new Thread(runnable1);
                thread.setName("udp-client-" + thread.getId());
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
