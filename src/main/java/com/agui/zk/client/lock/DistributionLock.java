package com.agui.zk.client.lock;

import com.agui.zk.client.ZKClient;
import com.agui.zk.client.constants.ZKConstants;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public boolean acquire(){
        return this.acquire(60 * 1000 * 60);
    }

    public boolean acquire(int timeOut){
        String path = zkClient.create(ZKConstants.LOCK_PATH_KEY,"1",CreateMode.PERSISTENT_SEQUENTIAL);
        ZKLockHandler zkLockHandler = new ZKLockHandler(path);
        if (zkLockHandler.isAcquired()){
            return true;
        }
        zkLockHandler.tryWait(timeOut);
        return zkLockHandler.isSuccess();
    }


    public void release(){

    }

}
