package com.atguigu.gulimall.order.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 消息监听（消费）组件
 */
@Service
public class MyService {

    /**
     * 消息队列监听器 （是否可以等价于消息的消费者）
     *
     * @param content
     * @RabbitListener 接收到消息并消费掉，Queue队列中为空
     */
    @RabbitListener(queues = "myqueue")
    public void listenMyQueue1(Map<String, Object> content) {
        System.out.println("listenMyQueue1方法---收到消息队列里面的内容是" + content);

    }

    /**
     * 1.多人监听同一个同一个队列，谁收到消息 -> 只有一个人能收到同一条消息（随机消费），避免消息的重复消费
     *
     * 2.service监听消息队列里面的内容，方法的参数能写哪些
     *      1. Map<String, Object>  content 明确的自定义对象，将队列的内容自动转为这个对象
     *      2. Message (org.springframework.amqp.core.Message)  Message封装了当前消息对象的详细信息，消息头消息体等等
     *      3. Channel (com.rabbitmq.client.Channel)
     *
     * 3.将自动ack模式切换为手动ack
     *      1.没有确认收到消息，还是会收到新的消息，而非历史消息
     *      2.所有消息都是Unacked状态
     *
     * 4.如果消费者（监听器）没有确认消息
     *      1.如果一直在线，此消息不会再发给别人
     *      2.如果掉线，未被确认的消息又会变成ready状态，又可以发给别人
     */
    @RabbitListener(queues = "myqueue")
    public void listenMyQueue2(Map<String, Object> content, Message message, Channel channel) throws IOException {
        System.out.println("listenMyQueue2方法---收到消息队列里面的内容是" + content);

        System.out.println("message封装了消息的详细信息" + message.getMessageProperties().getMessageId());

        System.out.println("channel 当前消息使用的通道" + channel);

        /**
         *  listenMyQueue2方法---收到消息队列里面的内容是{age=18, username=zhangsan}
         *  message封装了消息的详细信息null
         *  channel 当前消息使用的通道Cached Rabbit Channel: AMQChannel(amqp://guest@192.168.40.183:5672//gulimall,3),
         *  conn: Proxy@6ddee60f Shared Rabbit Connection: SimpleConnection@7945b206 [delegate=amqp://guest@192.168.40.183:5672//gulimall, localPort= 61617]
         */

        /**
         * long deliveryTag 消息的派发标签
         * boolean multiple 是否拒绝多个消息或确认多个消息（ACK）,true表示拒绝所有消息（包括当前消息） ,false只拒绝当前消息
         * boolean requeue  拒绝后的消息是否重返队列  false：【丢弃】discarded/dead-lettered【死信队列】
         */

        // Nack 不确认,如果不确认后multiple = false/true , requeue = true ，当拒绝的信息重新入队，那么就会变成死循环
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, true);

        // Reject 拒绝
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);

        // Ack 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
