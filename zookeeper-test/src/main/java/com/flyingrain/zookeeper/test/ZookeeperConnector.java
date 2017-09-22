package com.flyingrain.zookeeper.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wally on 9/21/17.
 */
public class ZookeeperConnector implements Watcher{

    private static final int SESSIONTIMEOUT = 5000;

    private static final String ZKHOST = "127.0.0.1:2181";

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public  ZooKeeper connect() throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(ZKHOST,SESSIONTIMEOUT,this);
        countDownLatch.await();
        return zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("get zookeeper callback!");
            countDownLatch.countDown();
        }
    }
}
