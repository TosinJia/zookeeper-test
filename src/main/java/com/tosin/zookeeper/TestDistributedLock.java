package com.tosin.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 4.4 案例实战 分布式秒杀
 */
public class TestDistributedLock {
    public static void main(String[] args) {
        // 定义客户端重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);//每次等待时间 最大重试次数
        // 定义ZK的一个客户端
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("bd-01-01:2181")
                .retryPolicy(retryPolicy)
                .build();
        // ZK生成锁 ---> ZK目录
        client.start();
        // 启动10个线程去访问共享资源
        InterProcessMutex interProcessMutex = new InterProcessMutex(client, "/mylock");

        for(int i = 0; i<10 ;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 请求得到锁
                        interProcessMutex.acquire();
                        // 访问共享资源
                        printCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // 释放锁
                        try {
                            interProcessMutex.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }

    // 定义共享资源
    private static int count = 100;

    private static void printCount(){
        System.out.println(Thread.currentThread().getName() +"当前值：" + count);
        count--;

        try {
            // 休眠2秒
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
