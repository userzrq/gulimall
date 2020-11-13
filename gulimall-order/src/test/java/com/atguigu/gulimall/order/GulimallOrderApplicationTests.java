package com.atguigu.gulimall.order;


import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 1.发送测试消息
     * 2.消息发送时要将消息序列化发送出去
     * 3.接受消息的时候要将消息反序列化进来
     * 4.但是为了跨平台传输消息，最好使用Json作为消息传输载体
     */
    @Test
    public void sendMsg() {
        for (int i = 1; i <= 100; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", UUID.randomUUID().toString());
            map.put("age", i);
            // rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            // convertAndSend 转化并发送
            rabbitTemplate.convertAndSend("my-guli-fanout-exchange", "any-routingKey-for-fanout,fanout不限制路由键", map);
            System.out.println("消息发送完成");
        }
    }


    /**
     * 1.创建队列
     */
    @Test
    void contextLoads() {

        /**
         * String name
         * boolean durable 是否持久化
         * boolean exclusive 排他，只能被指定的连接连上（只有一个人能连接它）
         * boolean autoDelete 自动删除
         * @Nullable Map<String, Object> arguments
         */

        Queue queue = new Queue("hello-queue", true, false, false, null);

        // 创建一个队列
        String declareQueue = amqpAdmin.declareQueue(queue);
        System.out.println("队列创建完成" + declareQueue);

    }

    /**
     * 2.创建交换机
     */
    @Test
    public void testExchange() {

        /**
         * String name
         * boolean durable
         * boolean autoDelete
         * Map<String, Object> arguments
         */

        DirectExchange directExchange = new DirectExchange("user-zrq-exchange", true, false, null);
        amqpAdmin.declareExchange(directExchange);
        System.out.println("交换机创建完成...");
    }

    /**
     * 创建绑定关系
     */
    @Test
    public void testBinding() {

        /**
         * String destination   【目的地】
         * DestinationType destinationType  【目的地类型】
         * String exchange  【交换机】
         * String routingKey    【路由键,路由规则】
         * @Nullable Map<String, Object> arguments
         *
         * 将【交换机】绑定到【目的地】，目的地的类型是【destinationType】，使用【路由键】进行绑定
         */

        Binding binding = new Binding(
                "hello-queue",
                Binding.DestinationType.QUEUE,
                "user-zrq-exchange",
                "hello.world",
                null
        );

        amqpAdmin.declareBinding(binding);
        System.out.println("交换机与队列的绑定关系创建完成...");
    }

}
