package com.shencoder.udpnetty;

/**
 * @author ShenBen
 * @date 2019/12/4 17:21
 * @email 714081644@qq.com
 */
class AppUtil {

    static int byteArrayToInt(byte[] byteArray) {
        if (byteArray.length != 4) {
            return 0;
        }
        return byteArray[3] & 0xFF |
                (byteArray[2] & 0xFF) << 8 |
                (byteArray[1] & 0xFF) << 16 |
                (byteArray[0] & 0xFF) << 24;
    }

    static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[4 - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    /**
     * 分割包数据
     */
    static void splitDatagramPacket(byte[] bytes, byte[] lengthArray, byte[] idArray, byte[] numArray) {
        System.arraycopy(bytes, 0, lengthArray, 0, lengthArray.length);
        System.arraycopy(bytes, 4, idArray, 0, idArray.length);
        System.arraycopy(bytes, 8, numArray, 0, numArray.length);
    }

    /**
     * 是否是非法的端口
     *
     * @param port
     * @return
     */
    static boolean isIllegalPort(int port) {
        return port < 0 || port > 65535;
    }
}
