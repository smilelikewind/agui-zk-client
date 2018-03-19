package com.agui.zk.client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class ZKClientMonitorThread extends Thread{

    private static AtomicBoolean isStart = new AtomicBoolean(false);

    public ZKClientMonitorThread(String threadName){
        super(threadName);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!ZKClient.isAlive()) {
                    System.out.println("[monitor] close current zk");
                    ZKClient.close();
                    ZKClient.getInstance();
                    System.out.println("[monitor] create a new current zk");
                } else {
                    System.out.println("[monitor] zk client ok!");
                }
                Thread.sleep(ZKConstants.zkMonitorExecuteIntervalTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startMonitor(){
        if (isStart.compareAndSet(false,true)){
            new ZKClientMonitorThread("zk client monitor").start();
        }
    }
}
