package com.tosin.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ZKClient {
    private static String connectString = "bd-01-01:2181,bd-01-02:2181,bd-01-03:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zooKeeper;

    /**
     * 4.3.2 创建ZooKeeper客户端
     */
    @Before
    public void init() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                // 收到事件通知后回调函数（用户的业务逻辑）
                System.out.println(watchedEvent.getType() + "--" + watchedEvent.getPath());

                try {
                    // 再次启动监听
                    zooKeeper.getChildren("/", true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 4.3.3 创建子节点
     */
    @Test
    public void create() throws KeeperException, InterruptedException {
        // 数据的增删改查
        // 参数1：要创建的节点的路径； 参数2：节点数据 ； 参数3：节点权限 ；参数4：节点的类型
        zooKeeper.create("/idea", "hello idea".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 4.3.4 获取子节点并监听
     */
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> childrenList = zooKeeper.getChildren("/", true);

        for (String children : childrenList) {
            System.out.println(children);
        }

        // 延时阻塞
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 4.3.5 判断znode是否存在
     */
    @Test
    public void exists() throws KeeperException, InterruptedException {
        Stat exists = zooKeeper.exists("/idea", false);
        System.out.println(exists==null?"not exist":"exist");
    }
}
