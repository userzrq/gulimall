package com.atguigu.locktest;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 4种创建线程的方法
 * 1)继承Thread
 * 2)实现Runnable
 * 无返回值
 * <p>
 * 3)实现Callable
 * 4)实现Future
 * 有返回值
 */
@Slf4j
public class ThreadMainTest {
    /**
     * 异步任务的启动不应该由Thread.start()启动
     * 应该用线程池进行控制
     *
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        List<Future<Integer>> futures = new ArrayList<>();
        // 相当于把参数传到ThreadPoolExecutor的构造器中
        // return new ThreadPoolExecutor(nThreads, nThreads,
        //                                0L, TimeUnit.MILLISECONDS,
        //                                new LinkedBlockingQueue<Runnable>());
        // 提交一个任务
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Future<Integer> submit = threadPool.submit(() -> {
                Thread.sleep(2000);
                System.out.println("提交的任务执行...result" + finalI + " " + Thread.currentThread().getId() + Thread.currentThread().getName());
                return finalI;
            });
            futures.add(submit);
        }

        // 第一个任务已完成，future已结束
        Integer integer = futures.get(0).get();
        System.out.println(integer);

    }


    public void test(String[] args) throws Exception {
//        System.out.println("outside thread:" + Thread.currentThread().getName());
//        new Thread(new HelloThread()).start();
//        System.out.println("outside thread done" + Thread.currentThread().getName());

        FutureTask<Object> task = new FutureTask<>(() -> {
            Thread.sleep(3000);
            System.out.println("Future Task Thread" + Thread.currentThread().getName());
            return 10;
        });

        Thread thread = new Thread(task);
        thread.start();

        //在线等结果，等不到不结束，所以Thread必须先执行才可以
        Object o = task.get();
        System.out.println("result -> " + o);
    }
}

class HelloThread implements Runnable {

    @Override
    public void run() {
        int i = 1 + 1;
        // 不允许有返回值
        // return i;
        System.out.println("current thread:" + Thread.currentThread().getName());
    }
}
