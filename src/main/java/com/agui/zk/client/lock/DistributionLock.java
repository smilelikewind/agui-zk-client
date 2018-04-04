package com.agui.zk.client.lock;

import com.agui.zk.client.ZKClient;
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


    static {
        Stat stat = zkClient.getState(LOCK_BASIC_PATH,null);
        if (stat == null){
            zkClient.create(LOCK_BASIC_PATH,"DistributionLock", CreateMode.PERSISTENT);
        }
    }

    public static boolean acquire(String lockKey){
        return acquire(lockKey,60 * 1000 * 60);
    }

    public static boolean acquire(String lockKey,int timeOut){
        String path = zkClient.create( LOCK_BASIC_PATH + lockKey,"1",CreateMode.EPHEMERAL_SEQUENTIAL);
        ZKLockHandler zkLockHandler = new ZKLockHandler(path);
        if (zkLockHandler.isAcquired()){
            return true;
        }
        zkLockHandler.tryWait(timeOut);
        return zkLockHandler.isSuccess();
    }

    public static void release(String lockKey){
        zkClient.delete(lockKey);
    }

}
