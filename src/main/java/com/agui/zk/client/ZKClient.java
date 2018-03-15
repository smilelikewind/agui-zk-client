package com.agui.zk.client;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by gui.a on 2018/3/14.
 *
 * @author xiaowei.li
 */
public class ZKClient implements Watcher{

    private static ZKClient zk;

    private ZooKeeper zookeeper;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private Thread monitorThread;
    public ZKClient(){

        try {
            this.zookeeper = new  ZooKeeper(ZKConstants.zkServerAddress,ZKConstants.sessionTimeOut,this);
            countDownLatch.await();
            monitorThread = new Thread(new ZKClientMonitorThread(),"zkClientMonitorThread");
            monitorThread.start();
        } catch (Exception e) {
            throw new RuntimeException("[ZKClient] create zk instance wrong",e);
        }
    }

    public void shutDown()throws Exception{
        monitorThread.interrupt();
        zookeeper.close();
    }

    public static void close(){
        try {
            zk.shutDown();
        } catch (Exception e) {
            throw new RuntimeException("[ZKClient]  close zk instance wrong",e);
        }
    }

    public class ZKClientMonitorThread implements Runnable{

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {

                    System.out.println("[monitor] check zk client state");

                    if (zookeeper != null && !zookeeper.getState().isAlive()) {
                        System.out.println("[monitor] close current zk");
                        zookeeper.close();
                        zk = new ZKClient();
                        System.out.println("[monitor] create a new current zk");
                    }
                    Thread.sleep(ZKConstants.zkMonitorExecuteIntervalTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

        if (event != null && event.getState() == Event.KeeperState.SyncConnected){
            System.out.println("zk connected success");
            this.countDownLatch.countDown();
        }

        System.out.println("[ZKClient] zk event is: " + JSON.toJSONString(event));
    }

    public static void main(String[] args) throws Exception{
        ZKClient.getInstance();
        Thread.sleep(10 * 1000);
        ZKClient.close();
        System.out.println("[main] success");
    }
}
