package com.flyingrain.zookeeper.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wally on 9/21/17.
 */
public class ZookeeperConnector {

    private static final int SESSIONTIMEOUT = 5000;

    private static final String ZKHOST = "127.0.0.1:2181";

    private static ZooKeeper zooKeeper;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper connect() {
        return zooKeeper;
    }

    static class Connector implements Watcher {

        static {
            try {
                zooKeeper= new ZooKeeper(ZKHOST,SESSIONTIMEOUT,new Connector());
                countDownLatch.await();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                System.out.println("get zookeeper callback!");
                countDownLatch.countDown();
            }
        }
    }

}
