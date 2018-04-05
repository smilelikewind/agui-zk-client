package com.agui.zk.client.lock;

import com.agui.zk.client.ZKClient;
import com.agui.zk.client.constants.ZKConstants;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import static com.agui.zk.client.constants.ZKConstants.LOCK_BASIC_PATH;

/**
 * Created by gui.a on 2018/4/2.
 *
 * @author xiaowei.li
 */
public class DistributionLock {

    private static ZKClient zkClient = ZKClient.getInstance();

    private static ThreadLocal<LockContext> lockContextLocal = new ThreadLocal<LockContext>(){
        @Override
        public void set(LockContext value){
            if (get() != null){
                throw new IllegalStateException("get is not null");
            }
            super.set(value);
        }
    };

    static {
        Stat stat = zkClient.getState(LOCK_BASIC_PATH,null);
        if (stat == null){
            zkClient.create(LOCK_BASIC_PATH,"on", CreateMode.PERSISTENT);
        }
    }

    public static boolean acquire(String lockKey){
        return acquire(lockKey,60 * 1000 * 60);
    }

    public static boolean acquire(String lockKey,int timeOut){
        String path = zkClient.create( getLockKey(lockKey),"1",CreateMode.EPHEMERAL_SEQUENTIAL);
        lockContextLocal.set(new LockContext().setLockKey(lockKey).setNodePath(path));
        ZKLockHandler zkLockHandler = new ZKLockHandler(path);
        if (zkLockHandler.isAcquired()){
            return true;
        }
        zkLockHandler.tryWait(timeOut);
        return zkLockHandler.isSuccess();
    }

    public static void release(){
        LockContext lockContext = lockContextLocal.get();
        lockContextLocal.remove();
        zkClient.delete(lockContext.getNodePath());
    }


    private static String getLockKey(String lockKey){
        return LOCK_BASIC_PATH + ZKConstants.ZK_PATH_SPERATOR+ lockKey + ZKConstants.ZK_PATH_SPERATOR + "lockNode";
    }

}
