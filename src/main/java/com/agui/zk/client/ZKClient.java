package com.agui.zk.client;

import com.agui.zk.client.common.Logger;
import com.agui.zk.client.constants.ZKConstants;
import com.agui.zk.client.monitor.ZKClientMonitor;
import com.agui.zk.client.operation.ConfigLoader;
import com.agui.zk.client.util.PathUtil;
import com.agui.zk.client.util.TimeUtil;
import com.agui.zk.client.util.ZKDecoder;
import com.agui.zk.client.util.ZKEncoder;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lingshou.util.logger.LoggerFactory;
import com.lingshou.util.logger.LoggerWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gui.a on 2018/3/14.
 *
 * @author xiaowei.li
 */
public class ZKClient implements Watcher {


    static LoggerWrapper INFO = LoggerFactory.getValue(Logger.ZK_INFO);

    /**
     * 类变量
     */
    private static ZKClient zk;

    /**
     * 仅允许关闭一次
     */
    private static AtomicBoolean IS_START = new AtomicBoolean(false);

    /**
     * zookeeper 实例变量
     */
    private ZooKeeper zookeeper;

    private CountDownLatch countDownLatch;


    public ZKClient() {
        if (IS_START.compareAndSet(false, true)) {
            try {
                countDownLatch = new CountDownLatch(1);
                this.zookeeper = new ZooKeeper(ZKConstants.zkServerAddress, ZKConstants.sessionTimeOut, this);
                this.countDownLatch.await();
            } catch (Exception e) {
                INFO.info("connect zk error,do restart", e);
                restart();
            }
        }
    }

    public boolean exists(String path, Watcher watcher) {
        try {
            Stat stat = zookeeper.exists(PathUtil.assemblePath(path), watcher);
            return stat != null;
        } catch (Exception e) {
            throw new RuntimeException("exists excepiton", e);
        }
    }

    public long getSessionId() {
        return zookeeper.getSessionId();
    }

    public Stat getState(String path, Watcher watcher) {
        try {
            Stat stat = zookeeper.exists(PathUtil.assemblePath(path), watcher);
            return stat;
        } catch (Exception e) {
            throw new RuntimeException("getState excepiton", e);
        }
    }


    public void delete(String path) {
        try {

            String nodePath = PathUtil.assemblePath(path);
            List<String> children = zookeeper.getChildren(nodePath, false);

            if (CollectionUtils.isNotEmpty(children)) {
                children.stream().forEach(item -> {
                    delete(path + ZKConstants.ZK_PATH_SPERATOR + item);
                });
            }
            zookeeper.delete(nodePath, -1);
        } catch (Exception e) {
            throw new RuntimeException("delete exception", e);
        }
    }


    public String getData(String path, Watcher watcher) {
        try {
            Stat stat = zookeeper.exists(PathUtil.assemblePath(path), false);

            if (stat == null) {
                return null;
            }
            return ZKDecoder.decode(zookeeper.getData(PathUtil.assemblePath(path), watcher, stat));
        } catch (Exception e) {
            throw new RuntimeException("getData exception", e);
        }
    }

    public String create(String path, String data,CreateMode createMode) {
        try {

            if (StringUtils.isBlank(path)){
                throw new IllegalArgumentException("argeument invalid");
            }

            if (createMode != CreateMode.EPHEMERAL || createMode != CreateMode.PERSISTENT){
                throw new UnsupportedOperationException("仅支持非顺序节点");
            }

            String targetPath = PathUtil.preHandler(path);
            String nodePath = PathUtil.assemblePath(targetPath);
            int lastIndex = targetPath.lastIndexOf(ZKConstants.ZK_PATH_SPERATOR);
            List<String > needAddNode = Lists.newArrayList();
            String parentPath = targetPath;

            while (lastIndex != -1 && zookeeper.exists(PathUtil.assemblePath(parentPath.substring(0,lastIndex)),null)== null){
                parentPath = parentPath.substring(0,lastIndex);
                needAddNode.add(parentPath);
                lastIndex = parentPath.lastIndexOf(ZKConstants.ZK_PATH_SPERATOR);
            }

            Collections.reverse(needAddNode);

            needAddNode.stream().forEach(item ->{
                try {
                    zookeeper.create(PathUtil.assemblePath(item),ZKEncoder.encode("-1"),defaultAcl(),createMode);
                } catch (Exception e) {
                    throw new IllegalStateException("create wrong",e);
                }
            });
            return zookeeper.create(nodePath, ZKEncoder.encode(data), defaultAcl(), createMode);
        } catch (Exception e) {
            throw new RuntimeException("exists excepiton", e);
        }
    }

    public boolean createEphemeral(String path, String data) {
        try {
            String createPath = zookeeper.create(PathUtil.assemblePath(path), ZKEncoder.encode(data), defaultAcl(), CreateMode.EPHEMERAL);
            return StringUtils.isNotBlank(createPath);
        } catch (Exception e) {
            throw new RuntimeException("exists excepiton", e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return zookeeper.getChildren(PathUtil.assemblePath(path), false);
        } catch (Exception e) {
            throw new RuntimeException("getChildren exception", e);
        }
    }

    private List<ACL> defaultAcl() {
        List<ACL> acls = new ArrayList<ACL>();
        Id id1 = new Id("world", "anyone");
        ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(acl1);
        return acls;
    }

    public boolean isActive() {
        return zookeeper != null && zookeeper.getState().isAlive();
    }

    public boolean isConnected() {
        return zookeeper != null && zookeeper.getState().isConnected();
    }

    private static void restart() {
        zk.close();
        TimeUtil.sleep(15);
        ZKClient.getInstance();
    }


    public void close() {
        if (IS_START.compareAndSet(false, true)) {
            zk = null;
            countDownLatch = null;
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                throw new RuntimeException("close zk error", e);
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
        } else if (event != null && event.getState() == Event.KeeperState.Expired) {
            // 链接失效后，需要清除watcher
            // 数据暂时不清理
            ConfigLoader.clearWathcer();
            restart();
        }
        INFO.info("[ZKClient] zk event is: " + JSON.toJSONString(event));
    }
}
