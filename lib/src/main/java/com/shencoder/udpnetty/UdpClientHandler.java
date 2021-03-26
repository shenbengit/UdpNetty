package com.shencoder.udpnetty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 *
 * @author ShenBen
 * @date 2021/01/07 13:59
 * @email 714081644@qq.com
 */
class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    public static final String TAG = "UdpClientHandler->";

    private UdpClient mUdpClient;

    public UdpClientHandler(UdpClient client) {
        mUdpClient = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        if (msg != null) {
            ByteBuf buf = msg.copy().content();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            byte[] lengthArray = new byte[4];
            byte[] idArray = new byte[4];
            byte[] numArray = new byte[4];

            AppUtil.splitDatagramPacket(bytes, lengthArray, idArray, numArray);

            int num = AppUtil.byteArrayToInt(numArray);
            LogUtil.i(TAG + "channelRead0 收到回复的消息流水号:" + num);
            if (mUdpClient != null) {
                mUdpClient.removeMsg(num);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx != null) {
            ctx.close();
        }
        LogUtil.e(TAG + "exceptionCaught:" + cause.getMessage());
    }
}
