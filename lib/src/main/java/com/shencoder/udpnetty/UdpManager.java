package com.shencoder.udpnetty;

import com.shencoder.udpnetty.bean.MessageBean;
import com.shencoder.udpnetty.callback.DiscardMessageCallback;
import com.shencoder.udpnetty.callback.ReceiveMessageCallback;


/**
 * @author ShenBen
 * @date 2021/01/07 11:46
 * @email 714081644@qq.com
 */
public class UdpManager {
    /**
     * 配置参数
     */
    private UdpConfig mUdpConfig;
    /**
     * 消息发送端
     */
    private UdpClient mUdpClient;
    /**
     * 消息接收端
     */
    private UdpServer mUdpServer;
    /**
     * 未发送成功消息回调
     */
    private DiscardMessageCallback mDiscardMessageCallback;
    /**
     * 接收消息回调
     */
    private ReceiveMessageCallback mReceiveMessageCallback;

    private UdpManager() {
    }

    private static final class SingleHolder {
        private static final UdpManager INSTANCE = new UdpManager();
    }

    public static UdpManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void setDiscardMessageCallback(DiscardMessageCallback mDiscardMessageCallback) {
        this.mDiscardMessageCallback = mDiscardMessageCallback;
    }

    public void setReceiveMessageCallback(ReceiveMessageCallback mReceiveMessageCallback) {
        this.mReceiveMessageCallback = mReceiveMessageCallback;
    }

    public void setDebug(boolean debug) {
        LogUtil.DEBUG = debug;
    }

    public void init(UdpConfig config) {
        mUdpConfig = config;
    }

    public UdpConfig getUdpConfig() {
        if (mUdpConfig == null) {
            //如果没有配置
            mUdpConfig = new UdpConfig.Builder().build();
        }
        return mUdpConfig;
    }

    /**
     * 只开启发送端
     */
    public void startClient() {
        if (mUdpClient == null) {
            //只用初始化一次即可
            mUdpClient = new UdpClient();
            mUdpClient.setDiscardMessageCallback(msg -> {
                if (mDiscardMessageCallback != null) {
                    mDiscardMessageCallback.onDiscardMsg(msg);
                }
            });
            mUdpClient.start();
        }
    }

    public void sendMessage(String ip, int port, String msg) {
        if (!ip.matches(Constant.IP_REGEX)) {
            LogUtil.e("invalid ip address");
            return;
        }
        if (AppUtil.isIllegalPort(port)) {
            LogUtil.e("invalid port");
            return;
        }
        if (msg == null || msg.isEmpty()) {
            LogUtil.e("send msg is null");
            return;
        }
        if (mUdpClient == null) {
            LogUtil.e("you need to start UdpClient");
            return;
        }
        mUdpClient.sendMessage(new MessageBean(ip, port, msg), false);
    }


    /**
     * 只看起接收端
     *
     * @param inetPort 接收端监听的端口
     */
    public void startServer(int inetPort) {
        if (AppUtil.isIllegalPort(inetPort)) {
            LogUtil.e("监听端口错误");
            return;
        }

        if (mUdpServer != null && inetPort == mUdpServer.getInetPort()) {
            //说明正在运行，并且监听的端口一致，无需重新启动
            return;
        }
        stopServer();
        mUdpServer = new UdpServer(inetPort);
        mUdpServer.setReceiveMessageCallback((msg, sender) -> {
            if (mReceiveMessageCallback != null) {
                mReceiveMessageCallback.onReceiveMsg(msg, sender);
            }
        });
        mUdpServer.start();
    }

    /**
     * 关闭发送端
     */
    public void stopClient() {
        if (mUdpClient != null) {
            mUdpClient.stop();
            mUdpClient = null;
        }
    }

    /**
     * 关闭接收端
     */
    public void stopServer() {
        if (mUdpServer != null) {
            mUdpServer.stop();
            mUdpServer = null;
        }
    }

    /**
     * 开启发送端和接收端
     *
     * @param inetPort 接收端监听的端口
     */
    public void start(int inetPort) {
        startClient();
        startServer(inetPort);
    }

    /**
     * 关闭发送端和接收端
     */
    public void stop() {
        stopClient();
        stopServer();
    }

}
