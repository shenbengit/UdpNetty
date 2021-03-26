package com.shencoder.udpnetty.bean;

/**
 * @author ShenBen
 * @date 2021/01/07 10:52
 * @email 714081644@qq.com
 */
public class MessageBean {
    /**
     * 发送到目标的ip地址
     */
    private String ip;
    /**
     * 发送到目标的端口号
     */
    private int port;
    /**
     * 发送的消息
     */
    private String msg;

    /**
     * 消息发送的次数
     * <p>内部使用</p>
     */
    private int resendTimes = 0;

    /**
     * 消息发送的时间
     * <p>内部使用</p>
     */
    private long sendTimeMillis = System.currentTimeMillis();

    /**
     * 流水号
     * <p>内部使用</p>
     */
    private int serialNumber = 0;

    public MessageBean(String ip, int port, String msg) {
        this.ip = ip;
        this.port = port;
        this.msg = msg;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResendTimes() {
        return resendTimes;
    }

    public void setResendTimes(int resendTimes) {
        this.resendTimes = resendTimes;
    }

    public long getSendTimeMillis() {
        return sendTimeMillis;
    }

    public void setSendTimeMillis(long sendTimeMillis) {
        this.sendTimeMillis = sendTimeMillis;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", msg='" + msg + '\'' +
                ", resendTimes=" + resendTimes +
                ", sendTimeMillis=" + sendTimeMillis +
                ", serialNumber=" + serialNumber +
                '}';
    }
}
