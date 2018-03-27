package com.agui.zk.client.register;

import com.agui.zk.client.common.TimeUtil;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gui.a on 2018/3/27.
 *
 * @author xiaowei.li
 */
public class ServiceRegisterTest {

    public static void main(String[] args) {

        int threadNum  = 10;
        CountDownLatch downLatch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum;i++){
            new RegisterThread("threadName" + i,downLatch).start();
        }

        try {
            downLatch.await();
        } catch (InterruptedException e) {
        }
    }

    public static class RegisterThread extends Thread{

        private CountDownLatch countDownLatch;

        private String name;

        public RegisterThread(String name,CountDownLatch countDownLatch){
            this.name = name;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run(){

            boolean registerValue = false;

            while (!registerValue){

                registerValue = ServerRegister.isRegistered(new RegisterWatcher("ssdk", new ServerCloseCallBack() {
                    @Override
                    public void close() {
                        System.out.println("i am closed,my name is: " + name);
                    }
                }));

                if (registerValue){
                    System.out.println("i am registerValue,my name is: " + name);
                    break;
                }
                TimeUtil.sleep(1);
            }

            Random random = new Random();
            int randomValue = random.nextInt(10);
            TimeUtil.sleep(randomValue);
            countDownLatch.countDown();

        }
    }
}
