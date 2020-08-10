package com.atguigu.locktest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
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
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        List<Future<Integer>> futures = new ArrayList<>();
        // 相当于把参数传到ThreadPoolExecutor的构造器中
        // return new ThreadPoolExecutor(nThreads, nThreads,
        //                                0L, TimeUnit.MILLISECONDS,
        //                                new LinkedBlockingQueue<Runnable>());

        // 在for循环中提交任务，而任务的执行不会在for循环中同步，而是提交到线程池后由线程池执行
        // 因此，for循环可能还没走完，但执行的结果已经被拿到
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Future<Integer> submit = executorService.submit(() -> {
                Thread.sleep(2000);
                System.out.println("提交的任务执行...result" + finalI + " " + Thread.currentThread().getId() + Thread.currentThread().getName());
                return finalI;
            });
            futures.add(submit);
        }

        // 第一个任务已完成，future已结束
        // 在线等结果，等不到就会一直阻塞
        Integer integer = futures.get(0).get();
        System.out.println(integer);
    }

    @Test
    public void test() throws Exception {
        //        System.out.println("outside thread:" + Thread.currentThread().getName());
        //        new Thread(new HelloThread()).start();
        //        System.out.println("outside thread done" + Thread.currentThread().getName());

        FutureTask<Object> task = new FutureTask<>(() -> {
            Thread.sleep(3000);
            System.out.println("Future Task Thread" + Thread.currentThread().getName());
            return 10;
        });

        FutureTask<Integer> task1 = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return new Random().nextInt();
            }
        });

        new Thread(task1).start();

        Thread thread = new Thread(task);
        thread.start();

        //在线等结果，等不到不结束，所以Thread必须先执行才可以
        Object o = task.get();
        Integer integer = task1.get();
        System.out.println("Integer -> " + integer);
        System.out.println("result -> " + o);
    }


    @Test
    public void test1() throws Exception {

        ExecutorService executorService = Executors.newCachedThreadPool();
        Test1 test1 = new Test1();
        Future submit = executorService.submit(test1);
        executorService.shutdown();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("主线程在执行任务");

        try {
            System.out.println("task运行结果"+submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务执行完毕");

    }

    @Test
    public void test2() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Test1 test1 = new Test1();
        FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(test1);
        executorService.submit(integerFutureTask, Integer.class);
        executorService.shutdown();


        //        Test1 test1 = new Test1();
        //        FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(test1);
        //
        //        Thread thread = new Thread(integerFutureTask);
        //        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("主线程在执行任务");

        try {
            System.out.println("task运行结果"+integerFutureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务执行完毕");

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

class Test1 implements Callable{

    @Override
    public Object call() throws Exception {
        System.out.println("子线程在进行计算");
        Thread.sleep(3000);
        int sum = 0;
        for(int i = 0;i <= 10 ;i++)
            sum += i;

        return sum;
    }
}
