package com.shencoder.udpnettydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shencoder.udpnetty.UdpConfig;
import com.shencoder.udpnetty.UdpManager;
import com.shencoder.udpnetty.bean.MessageBean;
import com.shencoder.udpnetty.callback.DiscardMessageCallback;
import com.shencoder.udpnetty.callback.ReceiveMessageCallback;

import java.net.InetSocketAddress;


public class MainActivity extends AppCompatActivity {
    private final UdpManager manager = UdpManager.getInstance();

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
        manager.setDiscardMessageCallback(msg -> System.out.println("丢失消息ip :" + msg.getIp() + ",port:" + msg.getPort() + ",msg:" + msg.getMsg()));
        manager.setReceiveMessageCallback((msg, sender) -> System.out.println("接收到的消息:" + msg));
        manager.start(7777);

        findViewById(R.id.btnSend).setOnClickListener(v -> manager.sendMessage("192.168.2.2", 80, "123"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stop();
    }
}