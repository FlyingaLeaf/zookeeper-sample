package com.flyingrain.zookeeper;

import com.flyingrain.zookeeper.test.ZookeeperLock;
import org.junit.Test;

/**
 * Created by wally on 9/22/17.
 */
public class ZookeeperLockTest {


    @Test
    public void testLock() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                final ZookeeperLock zookeeperLock = new ZookeeperLock();
                System.out.println("thread " + Thread.currentThread().getName() + "started!");
                zookeeperLock.lock();
                System.out.println("thread " + Thread.currentThread().getName() + " say :i'm get lock!");
                zookeeperLock.unlock();
                System.out.println("i'm unlock!");
            }).start();
        }
        Thread.sleep(Integer.MAX_VALUE);
    }
}
