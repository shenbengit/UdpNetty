package com.shencoder.udpnetty;


/**
 * @author ShenBen
 * @date 2021/01/07 11:49
 * @email 714081644@qq.com
 */
public class UdpConfig {
    /**
     * 消息发送不成功，是否启用重发
     */
    private final boolean resendEnable;
    /**
     * 消息重发次数
     */
    private final int resendLimit;
    /**
     * 消息重发之间的间隔时间,单位：毫秒
     */
    private final long resendInterval;

    private UdpConfig(Builder builder) {
        this.resendEnable = builder.resendEnable;
        this.resendLimit = builder.resendLimit;
        this.resendInterval = builder.resendInterval;
        LogUtil.DEBUG = builder.isLoggable;
    }


    public boolean isResendEnable() {
        return resendEnable;
    }

    public int getResendLimit() {
        return resendLimit;
    }

    public long getResendInterval() {
        return resendInterval;
    }

    public static final class Builder {
        /**
         * 是否启用重发
         */
        private boolean resendEnable = Constant.DEFAULT_RESEND_ENABLE;
        /**
         * 重发次数
         */
        private int resendLimit = Constant.DEFAULT_RESEND_LIMIT;
        /**
         * 重发之间的间隔时间,单位：毫秒
         */
        private long resendInterval = Constant.DEFAULT_RESEND_INTERVAL;
        /**
         * 是否打印日志
         */
        private boolean isLoggable = false;

        public Builder setResendEnable(boolean resendEnable) {
            this.resendEnable = resendEnable;
            return this;
        }

        public Builder setResendLimit(int resendLimit) {
            if (resendLimit <= 0) {
                resendLimit = Constant.DEFAULT_RESEND_LIMIT;
            }
            this.resendLimit = resendLimit;
            return this;
        }

        public Builder setResendInterval(long resendInterval) {
            if (resendInterval < Constant.DEFAULT_RESEND_INTERVAL) {
                resendInterval = Constant.DEFAULT_RESEND_INTERVAL;
            }
            this.resendInterval = resendInterval;
            return this;
        }

        public Builder setLoggable(boolean loggable) {
            isLoggable = loggable;
            return this;
        }

        public UdpConfig build() {
            return new UdpConfig(this);
        }
    }
}
