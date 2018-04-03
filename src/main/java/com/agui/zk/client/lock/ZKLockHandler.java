package com.agui.zk.client.lock;

import com.agui.zk.client.ZKClient;
import com.agui.zk.client.constants.ZKConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by gui.a on 2018/4/2.
 *
 * @author xiaowei.li
 */
public class LockHelper implements Watcher {

    ZKClient zkClient = ZKClient.getInstance();

    private Object lock = new Object();

    private Boolean isSuccess = null;

    private String lockPath;

    public LockHelper(String lockPath) {
        this.lockPath = lockPath;
    }

    public void tryWait(String path, long timeOut) {

        if (isSuccess != null && isSuccess) {
            return;
        }

        zkClient.exists(path, this);

        synchronized (lock) {

            try {
                lock.wait(timeOut);
            } catch (Exception e) {
                throw new RuntimeException("tryWait exception", e);
            }

            // 超时处理
            if (isSuccess != null) {
                isSuccess = false;
            }
        }
    }

    public boolean isAcquired() {
        List<String> dataPaths = zkClient.getChildren(ZKConstants.LOCK_BASIC_PATH);

        if (CollectionUtils.isEmpty(dataPaths)) {
            return false;
        }

        Collections.sort(dataPaths);

        return lockPath.equals(dataPaths.get(0));
    }


    @Override
    public void process(WatchedEvent event) {
        if (event == null || event.getType() == null) {
            return;
        }

        Event.EventType type = event.getType();

        if (type == Event.EventType.NodeDeleted) {

            if (isAcquired()) {
                isSuccess = true;
            } else {
                isSuccess = false;
            }
            synchronized (lock) {
                lock.notify();
            }
        }

    }
}
