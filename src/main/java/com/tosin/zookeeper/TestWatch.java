package com.tosin.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

public class TestWatch {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
//        test1();
        test2();
    }

    static List<String> children = null;
    public static void test2() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zkCli = new ZooKeeper("bd-01-01:2181,bd-01-02:2181,bd-01-03:2181", 3000, new Watcher() {
            //监听回调
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("正在监听中...");
            }
        });

//        final List<String> children = null;
        //监听目录
        children = zkCli.getChildren("/", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("数据被修改了！！");
                System.out.println("监听路径："+watchedEvent.getPath()+"监听类型："+watchedEvent.getType());

                for (String c:children) {
                    System.out.println("inner:"+c);
                }
            }
        });

        for (String c:children) {
            System.out.println("out:"+c);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void test1() throws IOException, KeeperException, InterruptedException {
        String connectString = "bd-01-01:2181,bd-01-02:2181,bd-01-03:2181";
        int sessionTimeout = 3000;
        Watcher watcher = new Watcher() {
            //监听回调
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        };
        ZooKeeper zkCli = new ZooKeeper(connectString, sessionTimeout, watcher);

        byte[] data = zkCli.getData("/tosin", new Watcher() {
            //监听具体内容
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("数据被修改了！！");
                System.out.println("监听路径："+watchedEvent.getPath()+"\t监听类型："+watchedEvent.getType()+"\t监听状态"+watchedEvent.getState());
            }
        }, null);
        System.out.println(new String(data));
        Thread.sleep(Long.MAX_VALUE);
    }
}
