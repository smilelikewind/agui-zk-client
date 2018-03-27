package com.agui.zk.client;

import com.lingshou.util.logger.INFO;
import com.lingshou.util.logger.LoggerFactory;
import com.lingshou.util.monitor.ApplicationHeartBeatMonitor;

/**
 * Created by gui.a on 2018/3/26.
 *
 * @author xiaowei.li
 */
public class Logger {

    @INFO
    public static String ZK_INFO = "zkInfo";

    @INFO
    public static String ZK_ERROR = "zkError";

    static {
        LoggerFactory.init(Logger.class, "zk-client");
        ApplicationHeartBeatMonitor.start();
    }
}
