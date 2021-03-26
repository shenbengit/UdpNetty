package com.shencoder.udpnettydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shencoder.udpnetty.UdpConfig;
import com.shencoder.udpnetty.UdpManager;
import com.shencoder.udpnetty.UdpServer;
import com.shencoder.udpnetty.callback.ReceiveMessageCallback;

import java.net.InetSocketAddress;


public class MainActivity extends AppCompatActivity {
    private final UdpManager manager = UdpManager.getInstance();
//    private final UdpServer udpServer = new UdpServer(7777, new ReceiveMessageCallback() {
//        @Override
//        public void onReceiveMsg(String msg, InetSocketAddress sender) {
//
//        }
//
//        @Override
//        public void onException(Exception e) {
//
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UdpConfig config = new UdpConfig.Builder()
                .setLoggable(true)
                .setResendLimit(3)
                .setResendEnable(true)
                .setResendInterval(3000L)
                .build();
        manager.init(config);
        manager.setDiscardMessageCallback(msg -> System.out.println("丢弃消息ip :" + msg.getIp() + ",port:" + msg.getPort() + ",msg:" + msg.getMsg()));
        manager.setReceiveMessageCallback(new ReceiveMessageCallback() {
            @Override
            public void onReceiveMsg(String msg, InetSocketAddress sender) {
                System.out.println("接收到的消息:" + msg);
            }

            @Override
            public void onException(Exception e) {
                System.out.println("异常:" + e.getMessage());
            }
        });
//        manager.startClient();
//        manager.startServer(8888);
        manager.start(7777);

//        udpServer.start();
        findViewById(R.id.btnSend).setOnClickListener(v -> manager.sendMessage("192.168.2.2", 80, "123"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        udpServer.stop();
//        manager.stopClient();
//        manager.stopServer();
        manager.stop();
    }
}