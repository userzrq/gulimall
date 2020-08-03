package com.atguigu.locktest.controller.service;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    /**
     * +1
     */
    public void incr() {
        // 1、尝试获取锁
        String token = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        // 2、获取到锁的执行业务逻辑
        if (lock) {

            redisTemplate.expire("lock", 3, TimeUnit.SECONDS);
            // 读和写没有分离，没有等写完就读了,读取的值存在偏差，可能读到旧值
            String num = redisTemplate.opsForValue().get("num");    // 读操作
            Integer i = Integer.parseInt(num);
            i++;

            // 1.2、把新加的值放进去
            redisTemplate.opsForValue().set("num", i.toString());    // 写操作

            // 原子性操作，可防并发
            //redisTemplate.opsForValue().increment("num");

            // 2、删锁前检查是不是自己的锁

            // 解锁脚本
            String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            //"if redis.call('get','lock')==token then return redis.call('del','lock') else return 0 end"
            Object o = redisTemplate.execute(new DefaultRedisScript<>(), Arrays.asList("lock"), token);
            System.out.println("删锁" + o.toString());

//            if (token.equals(redisTemplate.opsForValue().get("lock"))) {
//                // 判断值和删锁不是原子化操作，还是可能会误删别人的锁
//                redisTemplate.delete("lock");
//            }

        } else {
            try {
                // 没获取到锁的睡上一秒，等待重试
                System.out.println("没有获取到锁，等待重试");
                Thread.sleep(1000);
                incr();
            } catch (InterruptedException e) {

            }
        }
    }

    public synchronized void incrByLock() {

        String num = redisTemplate.opsForValue().get("num");
        Integer i = Integer.parseInt(num);
        i++;

        redisTemplate.opsForValue().set("num", i.toString());
    }


    public void incr2() throws InterruptedException {
        RLock lock = redissonClient.getLock("lock"); //只要锁的名字一样，就是同一把锁

        // lock.lock(10, TimeUnit.SECONDS);
        // 改为尝试加锁，及时获取不到锁也不会长时间等待
        // boolean b = lock.tryLock();
        // 尝试加锁，最多等待100秒，等待超过100秒就不要这个锁了，加锁后10秒放锁
        boolean b = lock.tryLock(100, 10, TimeUnit.SECONDS);

        if (b) {
            System.out.println("redission加锁.....");
            // 主要功能逻辑
            String num = redisTemplate.opsForValue().get("num");    // 读操作
            Integer i = Integer.parseInt(num);
            i++;
            redisTemplate.opsForValue().set("num", i.toString());    // 写操作
        }


        lock.unlock();
        System.out.println("redission释放锁.....");
    }

    public String read() throws InterruptedException {
        RReadWriteLock data = redissonClient.getReadWriteLock("data");
        RLock rLock = data.readLock();

        rLock.lock();
        String hello = redisTemplate.opsForValue().get("hello");
        rLock.unlock();
        return hello;
    }

    public String write() throws InterruptedException {

        RReadWriteLock data = redissonClient.getReadWriteLock("data");
        RLock rLock = data.writeLock();

        rLock.lock();
        // 让线程睡3s，那么有可能读到旧数据
        Thread.sleep(3000L);
        redisTemplate.opsForValue().set("hello", UUID.randomUUID().toString());

        rLock.unlock();

        return "ok";
    }

    /**
     * 闭锁的整体流程
     * 在redis中先放入总数量
     * 用Redisson操作一个闭锁获取该数量，并使闭锁线程处于等待状态
     * 闭锁线程等到redis中的数量减到0时，才会关锁
     *
     * @return
     * @throws InterruptedException
     */
    public String lockdoor() throws InterruptedException {

        // 获取一个闭锁
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("0508");
        countDownLatch.await();

        return "the door has closed";
    }

    public void gobackhome() {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("0508");
        countDownLatch.countDown();
    }
}
