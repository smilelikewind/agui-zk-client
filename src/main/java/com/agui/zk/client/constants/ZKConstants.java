package com.agui.zk.client.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gui.a on 2018/3/14.
 *
 * @author xiaowei.li
 */
public class ZKConstants {
    /**
     * zk 连接的Ip和端口
     */
    public static String zkServerAddress = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    /**
     * 每次sessionTimeout时间
     */
    public static int sessionTimeOut = 2000;
    public static int zkMonitorExecuteIntervalTime = 1000;
    public static String APPLICATION_BASE_PATH = "/data/zkclient/";
    public static String ZK_PATH_SPERATOR = "/";

    public static String defaultChraterSet = "utf-8";


    public static String LOCK_BASIC_PATH = "distributionLock";

}
