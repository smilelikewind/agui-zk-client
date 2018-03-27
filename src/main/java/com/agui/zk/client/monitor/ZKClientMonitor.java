package com.agui.zk.client.monitor;

import com.agui.zk.client.ZKClient;
import com.agui.zk.client.common.TimeUtil;
import com.agui.zk.client.constants.ZKConstants;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class ZKClientMonitor extends Thread {

    private static AtomicBoolean isStart = new AtomicBoolean(false);

    private ZKClient zkClient;

    public ZKClientMonitor(String threadName,ZKClient zkClient) {
        super(threadName);
        this.zkClient = zkClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!zkClient.isActive() && !zkClient.isConnected()){
                    System.out.println("[monitor] close current zk");
                    /**
                     * 把当前的zkClient关闭掉
                     */
                    zkClient.close();
                    /**
                     * 睡5秒
                     */
                    TimeUtil.sleep(5);
                    /**
                     * 创建一个新的Client实例，并创建的实例赋值给this.client
                     */
                    zkClient = ZKClient.getInstance();
                    System.out.println("[monitor] create a new current zk");
                } else if (zkClient.isConnected()){
                    System.out.println("[monitor] zk client ok");
                } else if (zkClient.isActive()){
                    System.out.println("[monitor] zk client is connecting");
                }
                Thread.sleep(ZKConstants.zkMonitorExecuteIntervalTime);
            } catch (Exception e) {
                TimeUtil.sleep(15);
            }
        }
    }

    public static void startMonitor(ZKClient zkClient) {
        if (isStart.compareAndSet(false, true)) {
            new ZKClientMonitor("zk client monitor",zkClient).start();
        }
    }
}
