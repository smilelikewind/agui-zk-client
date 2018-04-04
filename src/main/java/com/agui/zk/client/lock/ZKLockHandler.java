package com.agui.zk.client.lock;

import com.agui.zk.client.ZKClient;
import com.agui.zk.client.constants.ZKConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;

/**
 * Created by gui.a on 2018/4/2.
 *
 * @author xiaowei.li
 */
public class ZKLockHandler implements Watcher {

    ZKClient zkClient = ZKClient.getInstance();

    private Object lock = new Object();

    private Boolean isSuccess = null;

    private String lockPath;

    private String waitPath;

    public ZKLockHandler(String lockPath) {
        this.lockPath = lockPath;
    }

    public void tryWait(long timeOut) {

        if (isSuccess != null && isSuccess || StringUtils.isBlank(waitPath)) {
            return;
        }

        zkClient.exists(waitPath, this);

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

        int location = 0;
        for (int index = 0;index < dataPaths.size();index ++){
            if (lockPath.equals(dataPaths.get(index))){
                location = index;
            }
        }

        if (location == 0){
            return true;
        }

        waitPath = dataPaths.get(location -1);
        return false;
    }

    public Boolean isSuccess() {
        return isSuccess != null && isSuccess;
    }

    public ZKLockHandler setSuccess(Boolean success) {
        isSuccess = success;
        return this;
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
