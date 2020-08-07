package com.atguigu.locktest;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureMainTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        FutureTest futureTest = new FutureTest();

        FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(futureTest);
        executorService.submit(integerFutureTask);
        executorService.shutdown();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("主线程开始工作");
            Integer integer = integerFutureTask.get();
            System.out.println("子线程获取的结果是" + integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务执行完毕");

    }
}


class FutureTest implements Callable {

    @Override
    public Object call() throws Exception {
        System.out.println("子线程开始工作");
        Thread.sleep(3000);
        int sum = 0;
        for (int i = 0; i <= 10; i++) {
            sum += i;
        }
        return sum;
    }
}
