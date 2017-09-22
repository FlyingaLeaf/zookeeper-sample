package com.flyingrain.zookeeper.test;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by wally on 9/21/17.
 */
public class ZookeeperLock implements Lock {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    private static final String ZKLOCKPATH = "/zklock";

    private static final String ZKCHILD = "/child";

    public ZookeeperLock() {
        ZookeeperConnector zookeeperConnector = new ZookeeperConnector();
        System.out.println("establish connection to zookeeper!");
        try {
            this.zooKeeper = zookeeperConnector.connect();
            if (zooKeeper.exists(ZKLOCKPATH, false) == null) {
                zooKeeper.create(ZKLOCKPATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lock() {
        try {
            String childNode = zooKeeper.create(ZKLOCKPATH + ZKCHILD, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            waitLock(childNode);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void waitLock(String childNode) {
        List<String> children = null;
        try {
            children = zooKeeper.getChildren(ZKLOCKPATH, false);

            String minChild = getMinChild(children);
            if (!childNode.equals(ZKLOCKPATH + "/" + minChild)) {
                System.out.println("i'm wait lock!" + Thread.currentThread().getName());
                CountDownLatch nodeStateCountDown = new CountDownLatch(1);
                Stat stat = zooKeeper.exists(ZKLOCKPATH + "/" + minChild, new LockWatcher(nodeStateCountDown, zooKeeper, minChild));
                if (stat == null) {
                    waitLock(childNode);
                } else {
                    nodeStateCountDown.await();
                    waitLock(childNode);
                }
            }else{
                System.out.println(Thread.currentThread().getName()+" say :my node is minimum!"+ minChild);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    private String getMinChild(List<String> children) {
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.stream().min(Comparator.comparingInt(this::getInt)).orElseGet(() -> "");
    }


    private Integer getInt(String child) {
        return Integer.parseInt(child.substring(child.length() - 10, child.length()));
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        try {
            if (zooKeeper != null)
                zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
