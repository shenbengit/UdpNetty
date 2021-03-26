package com.shencoder.udpnetty;

/**
 * @author ShenBen
 * @date 2021/01/07 14:04
 * @email 714081644@qq.com
 */
class LogUtil {

    static boolean DEBUG = false;

    static void i(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }

    static void e(String msg) {
        if (DEBUG) {
            System.err.println(msg);
        }
    }


}
