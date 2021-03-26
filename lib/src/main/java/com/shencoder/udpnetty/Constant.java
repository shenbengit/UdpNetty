package com.shencoder.udpnetty;

/**
 * @author ShenBen
 * @date 2021/01/07 13:39
 * @email 714081644@qq.com
 */
class Constant {
    /**
     * udp消息 消息头的长度
     */
    static final int HEADER_LENGTH = 12;
    /**
     * 是否启用重发机制
     */
    static final boolean DEFAULT_RESEND_ENABLE = true;
    /**
     * 重发次数限制
     */
    static final int DEFAULT_RESEND_LIMIT = 3;
    /**
     * 两秒重发一次
     */
    static final long DEFAULT_RESEND_INTERVAL = 1000L;

    static final String IP_REGEX = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
}
