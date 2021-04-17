package com.atguigu.gulimall.pms.transaction;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 异步多线程事务管理器
 */
public class TaskManager {

    private List<MyFutureTask> taskList = new LinkedList<>();

    private boolean isSuccess = false;

    public TaskManager() {
    }

    public void addTask(Task task) {
        taskList.add(new MyFutureTask(task));
    }

    public void done() {
        CompletableFuture[] completableFutures = taskList.stream().map(task -> CompletableFuture.runAsync(task).whenCompleteAsync(this::done1))
                .collect(Collectors.toList())
                .toArray(new CompletableFuture[taskList.size()]);
        CompletableFuture.allOf(completableFutures).join();
    }

    public void done1(Void v, Throwable t) {
        if (!isSuccess) {
            isSuccess = true;
            try {
                for (MyFutureTask futureTask : taskList) {
                    futureTask.get();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                for (MyFutureTask futureTask : taskList) {
                    if (!futureTask.isCancelled()) {
                        futureTask.cancel(true);
                        // 中断任务的执行并执行callback方法
                        Task task = (Task) futureTask.getCallable();
                        task.callback();
                    }
                }
            }
        }
    }
}
