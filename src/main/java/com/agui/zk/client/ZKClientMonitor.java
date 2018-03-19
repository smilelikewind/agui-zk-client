package com.agui.zk.client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class ZKClientMonitor extends Thread {

    private static AtomicBoolean isStart = new AtomicBoolean(false);

    public ZKClientMonitor(String threadName) {
        super(threadName);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!ZKClient.isOnline() && !ZKClient.isAlive()){
                    System.out.println("[monitor] close current zk");
                    ZKClient.close();
                    ZKClient.getInstance();
                    System.out.println("[monitor] create a new current zk");
                } else if (ZKClient.isOnline()){
                    System.out.println("[monitor] zk client ok");
                } else if (ZKClient.isAlive()){
                    System.out.println("[monitor] zk client is connecting");
                }
                Thread.sleep(ZKConstants.zkMonitorExecuteIntervalTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startMonitor() {
        if (isStart.compareAndSet(false, true)) {
            new ZKClientMonitor("zk client monitor").start();
        }
    }
}
