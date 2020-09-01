package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitTemplate：用于收发消息
 * AmqpAdmin：管理RabbitMQ中exchange, queue, binding的创建、删除
 *
 * @EnableRabbit 开启rabbitMQ功能
 * <p>
 * <p>
 * 1.利用java API给RabbitMQ创建 exchange, queue, binding
 * 方式 1)、利用AmqpAdmin.declareXXX方法来创建(queue, binding, exchange...)
 * amqpAdmin.declareQueue() .declareBinding() .declareExchange()
 * <p>
 * <p>
 * 方式 2)、直接通过@EnableRabbit 注解给容器中放 exchange, queue, binding
 * 新版本的坑：在往容器中创建组件前，必须先与rabbitMQ建立连接，才能成功进入 RabbitAdmin的initialize方法，对组件的属性进行初始化
 * <p>
 * <p>
 * 2.如何监听消息队列中的消息
 * @EnableRabbit
 * @RabbitListener(queues = "myqueue") 指定监听的队列
 * <p>
 * 1)发消息要将消息序列化后发出去
 * 2)收消息要将消息反序列化进来
 * 3)希望给消息队列发的对象要实现序列化接口
 * <p>
 * 4)且消息都是JSON，不同语言平台都能处理
 */
@EnableRabbit
@Configuration
public class GulimallRabbitConfig {

    /**
     * SpringBoot会自动给RabbitMQ中创建这个交换机/队列/绑定关系
     * <p>
     * 1)、去RabbitMQ中查看有没有当前名字的交换机/队列/绑定关系，如果没有就创建
     *
     * @return
     */
    @Bean("my-guli-fanout-exchange")
    public Exchange myExchange() {
        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        FanoutExchange fanoutExchange = new FanoutExchange("my-guli-fanout-exchange", true, false, null);
        return fanoutExchange;
    }

    @Bean("myqueue")
    public Queue myQueue() {
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
         */
        return new Queue("myqueue", true, false, false, null);
    }

    @Bean
    public Binding myBinding() {
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
         */
        return new Binding("myqueue",
                Binding.DestinationType.QUEUE,
                "my-guli-fanout-exchange",
                "hello",
                null
        );
    }

    @Bean
    public MessageConverter setConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
