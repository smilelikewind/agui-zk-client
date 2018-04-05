package com.agui.zk.client.util;

import com.agui.zk.client.constants.ZKConstants;

/**
 * Created by gui.a on 2018/4/5.
 *
 * @author xiaowei.li
 */
public class PathUtil {
    public static String assemblePath(String path){
        String tmpPath = preHandler(path);
        return (ZKConstants.APPLICATION_BASE_PATH + tmpPath).replace("//","/");
    }

    public static String preHandler(String origin){
        origin = origin.startsWith("/") ?  origin.substring(1,origin.length()) : origin;
        return origin.endsWith("/") ? origin.substring(0,origin.length()-1) :origin;
    }
}
