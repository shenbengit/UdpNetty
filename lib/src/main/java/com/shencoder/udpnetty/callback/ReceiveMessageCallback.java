package com.shencoder.udpnetty.callback;

import java.net.InetSocketAddress;

/**
 * 接收到udp消息回调
 *
 * @author ShenBen
 * @date 2021/01/07 10:58
 * @email 714081644@qq.com
 */
public interface ReceiveMessageCallback {
    /**
     * 接收到的消息
     *
     * <p>运行在子线程</p>
     *
     * @param msg    接收到的内容
     * @param sender 发送端相关信息,{@link InetSocketAddress#getHostName()}是个耗时操作，建议使用{@link InetSocketAddress#getHostString()}
     */
    void onReceiveMsg(String msg, InetSocketAddress sender);
}
