package com.agui.zk.client;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gui.a on 2018/3/14.
 *
 * @author xiaowei.li
 */
public class ZKClient implements Watcher {

    /**
     * 类变量
     */
    private static ZKClient zk;

    /**
     * 实例变量
     */
    private ZooKeeper zookeeper;
    private volatile AtomicBoolean IS_STOP = new AtomicBoolean(false);
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZKClient() {
        try {
            this.zookeeper = new ZooKeeper(ZKConstants.zkServerAddress, ZKConstants.sessionTimeOut, this);
            countDownLatch.await();
        } catch (Exception e) {
            throw new RuntimeException("[ZKClient] create zk instance wrong", e);
        }
    }

    private void shutDown(){
        if (IS_STOP.compareAndSet(false, true)) {
            try {
                zookeeper.close();
                zk = null;
            } catch (InterruptedException e) {
                throw new RuntimeException("close zk error",e);
            }
        }
    }

    private boolean isActive(){
        return zookeeper != null && zookeeper.getState().isAlive();
    }


    public static boolean isAlive(){
        return zk != null && zk.isActive();
    }

    public static void close(){
        if (zk != null){
            zk.shutDown();
        }
    }

    public static ZKClient getInstance() {

        if (zk != null) {
            return zk;
        }

        synchronized (ZKClient.class) {

            if (zk != null) {
                return zk;
            }
            zk = new ZKClient();
        }

        return zk;
    }

    @Override
    public void process(WatchedEvent event) {

        if (event != null && event.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("zk connected success");
            this.countDownLatch.countDown();
        }

        System.out.println("[ZKClient] zk event is: " + JSON.toJSONString(event));
    }

    public static void main(String[] args) throws Exception {
        ZKClient.getInstance();
        Thread.sleep(100 * 1000);
        System.out.println("[main] success");
    }
}
