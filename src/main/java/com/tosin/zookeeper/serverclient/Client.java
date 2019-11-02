package com.tosin.zookeeper.serverclient;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    ZooKeeper zkCli;
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        Client c = new Client();
        c.getConnect();
        c.businessLogic();
    }
    //业务逻辑 一直监听
    public void businessLogic() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }
    //获取服务器列表
    public void getServers(){
        List<String> list = null;
        try {
            List<String> serverNameList = new ArrayList<>();
//                    list = zkCli.getChildren("/servers", false);
            list = zkCli.getChildren("/servers", true);    //false: true
            for (String c:list) {
                byte[] data = zkCli.getData("/servers/" + c, true, null);
                serverNameList.add(new String(data));
            }
            System.out.println("ZooKeeper: "+serverNameList);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //初始化监听
    public void getConnect() throws IOException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("ZooKeeper path:"+watchedEvent.getPath()+"\ttype:"+watchedEvent.getType()+"\tstate:"+watchedEvent.getState()+"\twrapper:"+watchedEvent.getWrapper());
                getServers();
            }
        };
        zkCli = new ZooKeeper("192.168.1.150:2181,192.168.1.151:2181,192.168.1.152:2181", 3000, watcher);
    }
}
