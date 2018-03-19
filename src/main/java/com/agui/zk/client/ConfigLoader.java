package com.agui.zk.client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class ConfigLoader {

    private static ZKClient zkClient= ZKClient.getInstance();

    private static ConcurrentHashMap<String,String> contentCache = new ConcurrentHashMap<String, String>();

    public static String getValue(String key,String defaultValue){
        return null;
    }


}
