package com.agui.zk.client;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
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
     * zookeeper 实例变量
     */
    private ZooKeeper zookeeper;

    /**
     * 仅允许关闭一次
     */
    private volatile AtomicBoolean hasStop = new AtomicBoolean(false);

    private CountDownLatch countDownLatch;


    public ZKClient() {
        try {
            countDownLatch = new CountDownLatch(1);
            this.zookeeper = new ZooKeeper(ZKConstants.zkServerAddress, ZKConstants.sessionTimeOut, this);
            this.countDownLatch.await();
        } catch (Exception e) {
            throw new RuntimeException("[ZKClient] create zk instance wrong", e);
        }
    }

    public boolean exists(String path,Watcher watcher){
        try {
            Stat stat = zookeeper.exists(generatePath(path),watcher);
            return stat != null;
        } catch (Exception e) {
            throw new RuntimeException("exists excepiton",e);
        }
    }

    public String getData(String path,Watcher watcher){
        try {
            Stat stat = zookeeper.exists(generatePath(path),false);

            if (stat == null){
                return null;
            }
            return byte2Str(zookeeper.getData(generatePath(path),watcher,stat));
        } catch (Exception e) {
            throw new RuntimeException("getData exception",e);
        }
    }

    public boolean create(String path,String data){
        try {
            String createPath = zookeeper.create(generatePath(path),str2Byte(data),defaultAcl(), CreateMode.PERSISTENT);
            return StringUtils.isNotBlank(createPath);
        } catch (Exception e) {
            throw new RuntimeException("exists excepiton",e);
        }
    }

    private List<ACL> defaultAcl(){
        List<ACL> acls = new ArrayList<ACL>();
        Id id1 = new Id("world","anyone");
        ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(acl1);
        return acls;
    }

    private byte[] str2Byte(String data){
        try {
            return data.getBytes(ZKConstants.defaultChraterSet);
        } catch (Exception e) {
            throw new RuntimeException("str2Byte error,data" + data,e);
        }
    }

    private String byte2Str(byte[] data){
        try {
            return new String(data,ZKConstants.defaultChraterSet);
        } catch (Exception e) {
            throw new RuntimeException("str2Byte error,data" + data,e);
        }
    }


    private String generatePath(String path){
        return ZKConstants.basePath + path;
    }

    public boolean isActive(){
        return zookeeper != null && zookeeper.getState().isAlive();
    }

    public boolean isConnected(){
        return zookeeper != null && zookeeper.getState().isConnected();
    }


    public void close(){
        if (hasStop.compareAndSet(false, true)) {
            zk = null;
            countDownLatch = null;
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                throw new RuntimeException("close zk error",e);
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
            ZKClientMonitor.startMonitor(zk);
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
        Thread.sleep(1000 * 1000);
        System.out.println("[main] success");
    }
}
