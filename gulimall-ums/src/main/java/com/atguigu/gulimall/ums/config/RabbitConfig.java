package com.atguigu.gulimall.ums.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 库存服务中的消息队列配置
 *
 * @author 10017
 */
@Configuration
@EnableRabbit
public class RabbitConfig {


    @Bean
    public MessageConverter setConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
