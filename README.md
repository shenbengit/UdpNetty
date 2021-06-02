# UdpNetty
基于[Netty](https://github.com/netty/netty)封装UDP收发工具，支持设置消息重发、重发次数、重发间隔。
## 引入

### 将JitPack存储库添加到您的项目中(项目根目录下build.gradle文件)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
### 添加依赖
[![](https://jitpack.io/v/shenbengit/UdpNetty.svg)](https://jitpack.io/#shenbengit/UdpNetty)
```gradle
dependencies {
    implementation 'com.github.shenbengit:UdpNetty:Tag'
}
```
## 使用事例
代码示例
>  基本使用
```java
UdpManager manager = UdpManager.getInstance();
UdpConfig config = new UdpConfig.Builder()
        .setLoggable(true)//是否显示日志
        .setResendLimit(3)//重发次数
        .setResendEnable(true)//是否启用重发
        .setResendInterval(3000L)//重发间隔
        .build();
//初始化配置
manager.init(config);

//丢弃消息回调
manager.setDiscardMessageCallback(new DiscardMessageCallback() {
     @Override
     public void onDiscardMsg(MessageBean msg) {
                
     }
});

//接收消息回调，最好一开始设置，避免发送端异常接收不到
manager.setReceiveMessageCallback(new ReceiveMessageCallback() {
    @Override
    public void onReceiveMsg(String msg, InetSocketAddress sender) {
                
    }
    
    @Override
    public void onException(Exception e) {
    
    }
});

//仅启用发送消息功能
manager.startClient();

//仅开启接收消息功能
manager.startServer(8888);

//接收、发送消息都开启
manager.start(7777);

//关闭发送消息
manager.stopClient();

//关闭接收功能
manager.stopServer();

//关闭发送端和接收端
manager.stop();

```

>  若需要监听多个端口，请参考下面使用事例
```java
UdpServer udpServer = new UdpServer(7777, new ReceiveMessageCallback() {

    @Override
    public void onReceiveMsg(String msg, InetSocketAddress sender) {
                
    }
    
    @Override
    public void onException(Exception e) {
    
    }
});

//开启
udpServer.start();

//关闭
udpServer.stop();

```

# [License](https://github.com/shenbengit/UdpNetty/blob/master/LICENSE)
