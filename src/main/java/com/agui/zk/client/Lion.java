package com.agui.zk.client;

import com.agui.zk.client.common.TimeUtil;
import com.agui.zk.client.operation.ConfigLoader;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class Lion {


    public static String getStrValue(String key, String defaultValue) {
        return ConfigLoader.getValue(key, defaultValue);
    }

    public static void main(String[] args) {
        while (true){
            try {
                System.out.println(getStrValue("xiaowei-lq", "520"));
                TimeUtil.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
