package com.atguigu.gulimall.order.config;

import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitTemplate：用于收发消息
 * AmqpAdmin：管理RabbitMQ中exchange, queue, binding的创建、删除
 *
 * @EnableRabbit 开启rabbitMQ功能
 * <p>
 * <p>
 * 1.利用java API给RabbitMQ创建 exchange, queue, binding
 * 方式 1)、利用AmqpAdmin.declareXXX方法来创建(queue, binding, exchange...)，创建队列，交换机，及两者之间的关系（绑定）
 * amqpAdmin.declareQueue() .declareBinding() .declareExchange()
 * <p>
 * <p>
 * 方式 2)、直接通过@EnableRabbit 注解给容器中放 exchange, queue, binding
 * 新版本的坑：在往容器中创建组件前，必须先与rabbitMQ建立连接，才能成功进入 RabbitAdmin的initialize方法，对组件的属性进行初始化
 * <p>
 * <p>
 * 2.如何监听消息队列中的消息
 * @EnableRabbit
 * @RabbitListener(queues = "myqueue") 指定监听的队列,刺激创建连接（不然在mq中不会创建对象）
 * <p>
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
     * 通过@Bean注解,将组件放到容器中,SpringBoot会自动给RabbitMQ中创建这个交换机/队列/绑定关系
     * <p>
     * 1)、去RabbitMQ中查看有没有当前名字的交换机/队列/绑定关系，如果没有就创建
     *
     * @return
     */
    @Bean(RabbitMQConstant.order_exchange)
    public Exchange orderTopicExchange() {
        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        TopicExchange orderTopicExchange = new TopicExchange(RabbitMQConstant.order_exchange, true, false, null);
        return orderTopicExchange;
    }

    /**
     * 延时队列
     *
     * @return
     */
    @Bean("order-delay-queue")
    public Queue orderDelayQueue() {
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
         */
        Map<String, Object> properties = new HashMap<>();
        // 信死了还是交给order_enchange，但是路由键要发生变化
        properties.put("x-dead-letter-exchange", RabbitMQConstant.order_exchange);
        // 信死了以什么路由键发出去，消息发布者需要指定的路由键
        properties.put("x-dead-letter-routing-key", RabbitMQConstant.order_dead_event_routing_key);
        properties.put("x-message-ttl", RabbitMQConstant.order_timeout);
        return new Queue("order-delay-queue", true, false, false, properties);
    }


    /**
     * 被order_exchange重新通过死信路由键发出去后到达的队列，接收死单
     *
     * @return
     */
    @Bean(RabbitMQConstant.order_queuq_dead)
    public Queue deadQueue() {
        return new Queue(RabbitMQConstant.order_queuq_dead, true, false, false, null);
    }

    /**
     * 交换机与延时队列的路由绑定
     *
     * @return
     */
    @Bean(RabbitMQConstant.order_exchange + "_" + "order-delay-queue" + RabbitMQConstant.order_create_event_routing_key)
    public Binding orderCreateBinding() {
        return new Binding("order-delay-queue",
                Binding.DestinationType.QUEUE,
                RabbitMQConstant.order_exchange,
                RabbitMQConstant.order_create_event_routing_key,
                null
        );
    }

    /**
     * 交换机与死单队列的路由绑定
     *
     * @return
     */
    @Bean(RabbitMQConstant.order_exchange + "_" + "dead-queue" + RabbitMQConstant.order_dead_event_routing_key)
    public Binding orderDeadBinding() {
        return new Binding(RabbitMQConstant.order_queuq_dead,
                Binding.DestinationType.QUEUE,
                RabbitMQConstant.order_exchange,
                RabbitMQConstant.order_dead_event_routing_key,
                null
        );
    }


    /**
     * 设置消息队列全局使用的转化器
     *
     * @return
     */
    @Bean
    public MessageConverter setConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
