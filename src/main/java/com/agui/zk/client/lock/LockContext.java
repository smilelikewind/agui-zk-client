package com.agui.zk.client.lock;

/**
 * Created by gui.a on 2018/4/6.
 *
 * @author xiaowei.li
 */
public class LockContext {
    private String nodePath;
    private String lockKey;

    public String getNodePath() {
        return nodePath;
    }

    public LockContext setNodePath(String nodePath) {
        this.nodePath = nodePath;
        return this;
    }

    public String getLockKey() {
        return lockKey;
    }

    public LockContext setLockKey(String lockKey) {
        this.lockKey = lockKey;
        return this;
    }
}
