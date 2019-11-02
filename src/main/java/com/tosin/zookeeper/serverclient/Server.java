package com.tosin.zookeeper.serverclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.ArrayList;

/**
 需求：分布式系统中，主节点有多台，可以进行动态上下线，当任何一台服务器发生动态的上下线，任何一台客户端都能感知到
 思路：
 1. 创建客户端、服务端
 2. 启动服务端，注册
 3. 启动客户端，监听
 4. 当服务端发生动态上下线，客户端能感知到
 *
 * */
public class Server {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //连接zookeeper
        Server s = new Server();
        s.getConnect();
        //注册节点信息
        String serverInfo = s.register(args[0]);
        //业务逻辑处理
        s.businessLogic(serverInfo);
    }

    ZooKeeper zkCli;
    public void getConnect() throws IOException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("Server-ZooKeeper path:"+watchedEvent.getPath()+ "\ttype:"+watchedEvent.getType()+"\tstate:"+watchedEvent.getState()+"\twrapper:"+watchedEvent.getWrapper());
            }
        };
        zkCli = new ZooKeeper("192.168.1.150:2181,192.168.1.151:2181,192.168.1.152:2181", 3000, watcher);
    }
    public String register(String serverInfo) throws KeeperException, InterruptedException {
        //应答方式
        ArrayList<ACL> aclList = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        String s = zkCli.create("/servers/server", serverInfo.getBytes(), aclList, CreateMode.EPHEMERAL_SEQUENTIAL);
        return s;
    }
    public void businessLogic(String serverInfo) throws InterruptedException {
        System.out.println("服务器："+serverInfo+"上线了！！！");
        Thread.sleep(5000);
    }
}
