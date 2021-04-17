package com.atguigu.gulimall.pms.transaction;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 任务提交对象,将Callable对象进行封装
 *
 * @param <T>
 */
public class MyFutureTask<T> extends FutureTask<T> {

    public Callable<T> callable;

    public MyFutureTask(Callable<T> callable) {
        super(callable);
        this.callable = callable;
    }

    public MyFutureTask(Runnable runnable, T result) {
        super(runnable, result);
    }

    public Callable<T> getCallable() {
        return callable;
    }
}
