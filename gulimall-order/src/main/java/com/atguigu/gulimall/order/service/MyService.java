package com.atguigu.gulimall.order.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MyService {

    /**
     * 消息队列监听器
     * @RabbitListener 接收到消息并消费掉，Queue队列中为空
     *
     * @param content
     */
   @RabbitListener(queues = "myqueue")
    public void listenMyQueue(Map<String, Object> content) {
        System.out.println("收到消息队列里面的内容是" + content);

    }
}
