package com.atguigu.locktest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureMainTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> {
            return 800;
        }); //5s

        CompletableFuture<String> coupon = CompletableFuture.supplyAsync(() -> {
            return "满1000减120";
        }); //4s

        CompletableFuture<String> baseAttr = CompletableFuture.supplyAsync(() -> {
            return "黑色 128G";
        }); //8s

        CompletableFuture<Void> future = CompletableFuture.allOf(price, coupon, baseAttr);

        CompletableFuture<Object> antFuture = CompletableFuture.anyOf(price, coupon, baseAttr);

        // 阻塞住，在线等，如果能执行之后的逻辑，说明已经能拿到结果了
        future.get();

        // 分别拿取不同阻塞任务的结果
        Integer integer = price.get();
        String s = coupon.get();
        String s1 = baseAttr.get();
    }


    public void main222(String[] args) throws ExecutionException, InterruptedException {
        //        CompletableFuture<Integer> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
        //            System.out.println("int i = 10 / 0");
        //            int i = 10 / 0;
        //            return i;
        //        }).whenComplete((result, e) -> {
        //            System.out.println("exception: " + e.getMessage());
        //
        //            System.out.println("result: " + result);
        //        });

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return UUID.randomUUID().toString();
        }, executorService).thenApply((t) -> {
            System.out.println(t);
            return t.toUpperCase();
        }).thenApply((t) -> {
            System.out.println(t);
            return t.replace("-", "");
        });


        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            return 1;
        });

        // 合并两个异步任务的返回值
        // 不必分别等待两个任务分别完成，只需要等待大的合并任务的返回值
        CompletableFuture<String> stringCompletableFuture = future.thenCombine(future2, (t, u) -> {
            System.out.println("第一个异步任务的返回值" + t);
            System.out.println("第二个异步任务的返回值" + u);

            return t + " ----> " + u;
        });

        String s = future.get();
        System.out.println("异步任务1的返回值" + s);

        String s1 = stringCompletableFuture.get();
        System.out.println("大的合并任务的返回值" + s1);


        //打印的是最后的结果，前面打印的是上一步的结果
        //String s = future.get();
        System.out.println(s);

        // 不能连在thenApply后连用，因为thenApply有返回值
        // thenAccept和thenRun不会干预异步任务的结果
        future.thenAccept((t) -> {
            System.out.println("将结果保存到数据库中...");
        });

        future.thenRun(() -> {
            System.out.println("日志记录干完了");
        });

        executorService.shutdown();


        System.out.println("--------------------");
    }
}
