package com.shencoder.udpnetty.callback;


import com.shencoder.udpnetty.bean.MessageBean;

/**
 * 未发送成功的消息回调
 *
 * @author ShenBen
 * @date 2021/01/07 10:56
 * @email 714081644@qq.com
 */
public interface DiscardMessageCallback {
    /**
     * 丢弃的消息
     *
     * <p>运行在子线程</p>
     *
     * @param msg 丢弃的消息，具体的内容{@link MessageBean#getMsg()}
     */
    void onDiscardMsg(MessageBean msg);
}
