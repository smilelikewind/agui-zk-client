package com.agui.zk.client.util;

/**
 * Created by gui.a on 2018/3/26.
 *
 * @author xiaowei.li
 */
public class TimeUtil {
    public static void sleep(int seconds){
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
