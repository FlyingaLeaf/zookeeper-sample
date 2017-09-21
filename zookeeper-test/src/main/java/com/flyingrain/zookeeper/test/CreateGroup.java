package com.flyingrain.zookeeper.test;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wally on 9/21/17.
 */
public class CreateGroup implements Watcher {

    private static final int SESSION_TIMEOUT=5000;

    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts,SESSION_TIMEOUT,this);
        countDownLatch.await();
    }

    public void create(String groupName) throws KeeperException, InterruptedException {
        String path = "/"+ groupName;
        String createPath = zk.create(path,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        String createPath2 = zk.create(createPath+"/hello",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("create group :" + createPath);
        System.out.println("create group :" + createPath2);
        List<String> children = zk.getChildren(createPath,false);
        if(children.isEmpty()){
            System.out.println("no children!");
        }else{
            System.out.println(Arrays.toString(children.toArray()));
        }
    }

    public void close() throws InterruptedException {
        zk.close();
    }




    @Override
    public void process(WatchedEvent event) {
        if(event.getState()== Event.KeeperState.SyncConnected){
            System.out.println("get zookeeper callback!");
            countDownLatch.countDown();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String hosts = "127.0.0.1:2181";
        CreateGroup createGroup = new CreateGroup();
        createGroup.connect(hosts);
        createGroup.create("helloWorld");
        createGroup.close();
    }
}
