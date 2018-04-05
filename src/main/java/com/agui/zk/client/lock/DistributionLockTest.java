package com.agui.zk.client.lock;

import com.agui.zk.client.ZKClient;
import com.agui.zk.client.constants.ZKConstants;
import com.agui.zk.client.util.TimeUtil;
import org.apache.zookeeper.CreateMode;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gui.a on 2018/4/4.
 *
 * @author xiaowei.li
 */
public class DistributionLockTest {


    public static class LockTest extends Thread {
        private String threadName;

        private CountDownLatch latch;

        public LockTest(String threadName,CountDownLatch latch){
            this.threadName = threadName;
            this.latch = latch;
        }

        @Override
        public void run() {
            while (true){
                if (DistributionLock.acquire("lockKey",1000)){
                    System.out.println("get the lock, name is: " + threadName);
                    break;
                }
                int sleepTime = new Random().nextInt(5);
                TimeUtil.sleep(sleepTime);
            }

            TimeUtil.sleep(3);
            DistributionLock.release("lockKey");
            latch.countDown();
        }
    }

    public static void main(String[] args) {
//        int threadNum = 20;
//        CountDownLatch latch = new CountDownLatch(20);
//        for (int num = 0;num < threadNum;num ++){
//            new LockTest("[myName]"+num,latch).start();
//        }
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        DistributionLock.acquire("lockKey",1);

        ZKClient.getInstance().delete("/monitor");
    }


}
