package com.atguigu.locktest.reentrant;

public class WhatReentrant {
    /**
     * 演示可重入锁是什么意思，可重入，就是可以重复获取相同的锁，synchronized和ReentrantLock都是可重入的
     * 可重入降低了编程复杂性
     * @param args
     */
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    System.out.println("第一次获取该锁,该锁是" + this);
                    int index = 1;

                    while (true) {
                        synchronized (this) {
                            // 在synchronized中再次调用synchronized，可重复获取相同的锁，这就是可重入锁
                            System.out.println("第" + (++index) + "次获取锁，这个锁是：" + this);
                        }
                        if (index == 10) {
                            break;
                        }
                    }
                }
            }
        }).run();
    }
}
