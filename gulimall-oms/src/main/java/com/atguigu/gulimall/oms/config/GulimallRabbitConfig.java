package com.atguigu.gulimall.oms.config;


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
     * 设置消息队列全局使用的转化器
     * @return
     */
    @Bean
    public MessageConverter setConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
