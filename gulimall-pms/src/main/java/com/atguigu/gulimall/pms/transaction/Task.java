package com.atguigu.gulimall.pms.transaction;

import java.util.concurrent.Callable;

public interface Task<T> extends Callable<T> {

    /**
     * 接口定义任务回滚方法
     */
    void callback();

}
