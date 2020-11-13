package com.atguigu.gulimall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 库存服务中的消息队列配置
 *
 * @author 10017
 */
@Configuration
@EnableRabbit
public class RabbitConfig {

//    @Bean("skuStockDeadQueue")
//    public Queue skuStockDeadQueue() {
//        return new Queue("skuStockDeadQueue", true, false, false, null);
//    }


    //-------------------交换机⬇ 队列以及他们之间的绑定关系⬇-------------------
    /**
     * 工作于订单创建的交换机
     *
     * @return
     */
    @Bean("skuStockCreateExchange")
    public Exchange skuStockCreateExchange() {
        return new DirectExchange("skuStockCreateExchange", true, false, null);
    }


    /**
     * 用于和死信路由交互的 队列,存储还未到过期时间的消息
     * 需要加上一些特殊的参数指定死信路由和路由键
     */
    @Bean("deadskuStockStorageQueue")
    public Queue deadskuStockStorageQueue() {
        Map<String, Object> properties = new HashMap<>();
        // 信死了以后发给哪个交换机（死信路由），而不是丢弃
        properties.put("x-dead-letter-exchange", "skuStockDeadExchange");
        // 信死了以什么路由键发出去，消息发布者需要指定的路由键
        properties.put("x-dead-letter-routing-key", "dead.skuStock");
        properties.put("x-message-ttl", 1000 * 60 * 40);
        return new Queue("deadskuStockStorageQueue", true, false, false, properties);
    }


    @Bean("deadskuStockRoutingBinding")
    public Binding deadskuStockRoutingBinding() {
        /**
         * "create.skuStock"
         */
        return new Binding("deadskuStockStorageQueue", Binding.DestinationType.QUEUE, "skuStockCreateExchange", "create.skuStock", null);
    }

    //-------------------以上⬆的交换机/路由绑定/队列 为订单创建后进入的队列，队列有设置的ttl时间，以及死信生成后被发送到的交换机和使用的路由键-------------------
    //-------------------订单创建的信息能保存到与死信路由交互的队列中-------------------

    /**
     * 订单到达过期时间x-message-ttl后需要被转发到的交换机，死信路由（死信交换机）
     */
    @Bean("skuStockDeadExchange")
    public Exchange skuStockDeadExchange() {
        return new DirectExchange("skuStockDeadExchange", true, false, null);
    }

    /**
     * 最后被监听的队列，最后被消费的队列
     *
     * @return
     */
    @Bean("closeSkuStockQueue")
    public Queue deadskuStockQueue() {
        return new Queue("closeSkuStockQueue", true, false, false, null);
    }

    @Bean("deadBinding")
    public Binding deadBinding() {
        return new Binding("closeSkuStockQueue", Binding.DestinationType.QUEUE, "skuStockDeadExchange", "dead.skuStock", null);
    }


    /**
     * 设置消息队列全局使用的转化器（转JSON格式）
     *
     * @return
     */
    @Bean
    public MessageConverter setConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
