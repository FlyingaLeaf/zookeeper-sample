package com.flyingrain.zookeeper.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wally on 9/22/17.
 */
public class LockWatcher implements Watcher {

    private CountDownLatch countDownLatch;

    private ZooKeeper zooKeeper;

    private String monitorPath;

    public LockWatcher(CountDownLatch countDownLatch, ZooKeeper zooKeeper, String monitorPath) {
        this.countDownLatch = countDownLatch;
        this.zooKeeper = zooKeeper;
        this.monitorPath = monitorPath;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getType()==Event.EventType.NodeDeleted){
            countDownLatch.countDown();
        }else if(event.getType()== Event.EventType.None){
            switch(event.getState()){
                case SyncConnected:
                    break;
                case Expired:
                    System.out.println("all over!");
                    break;
            }
        }
    }
}
