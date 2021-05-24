package com.shencoder.udpnetty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.Charset;

/**
 * @author ShenBen
 * @date 2020/09/24 11:08
 * @email 714081644@qq.com
 */
class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String TAG = "UdpServerHandler->";

    private final UdpServer mUdpServer;

    public UdpServerHandler(UdpServer server) {
        mUdpServer = server;
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

            //回复udp命令
            byte[] replyBodyArray = "".getBytes(Charset.forName("gb2312"));
            byte[] replyArray = new byte[replyBodyArray.length + 12];
            byte[] replyIdArray = AppUtil.intToByteArray(-0x7fffffff);
            byte[] replyLengthArray = AppUtil.intToByteArray(replyArray.length);
            System.arraycopy(replyLengthArray, 0, replyArray, 0, replyLengthArray.length);
            System.arraycopy(replyIdArray, 0, replyArray, 4, replyIdArray.length);
            System.arraycopy(numArray, 0, replyArray, 8, numArray.length);
            System.arraycopy(replyBodyArray, 0, replyArray, 12, replyBodyArray.length);
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(replyArray), msg.sender()));

            //解析发送的消息
            int length = AppUtil.byteArrayToInt(lengthArray);
            if (length <= Constant.HEADER_LENGTH) {
                return;
            }
            String content = new String(bytes, Constant.HEADER_LENGTH, length - Constant.HEADER_LENGTH, Charset.forName("gb2312"));
            LogUtil.i(TAG + "channelRead0 -> msg:" + content);
            if (mUdpServer != null) {
                mUdpServer.receiveMessage(content, msg.sender());
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
