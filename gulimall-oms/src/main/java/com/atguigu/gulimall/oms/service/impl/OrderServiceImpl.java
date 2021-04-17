package com.atguigu.gulimall.oms.service.impl;

import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderItemMqTo;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.commons.to.order.OrderItemVo;
import com.atguigu.gulimall.commons.to.order.OrderVo;
import com.atguigu.gulimall.oms.dao.OrderItemDao;
import com.atguigu.gulimall.oms.entity.OrderItemEntity;
import com.atguigu.gulimall.oms.enume.OrderStatusEnume;
import com.atguigu.gulimall.oms.vo.CartItemVo;
import com.atguigu.gulimall.oms.vo.CartVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.oms.dao.OrderDao;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.service.OrderService;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }

    @Transactional
    @Override
    public OrderEntity createAndSaveOrder(OrderSubmitVo orderSubmitVo) throws IOException {

        OrderMqTo mqTo = new OrderMqTo();

        CartVo cartVo = orderSubmitVo.getCartVo();
        List<CartItemVo> items = cartVo.getItems();

        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderSubmitVo, orderEntity);

        // 提交订单时生成的订单号存一下
        orderEntity.setOrderSn(orderSubmitVo.getOrderToken());
        // 价格信息
        orderEntity.setTotalAmount(cartVo.getTotalPrice());
        orderEntity.setPayAmount(cartVo.getCartPrice());
        orderEntity.setPromotionAmount(cartVo.getReductionPrice());
        orderEntity.setNote(orderSubmitVo.getRemark());
        orderEntity.setMemberId(orderSubmitVo.getUserId());
        // 订单初始化状态为0
        orderEntity.setStatus(OrderStatusEnume.UNPAY.getCode());

        orderDao.insert(orderEntity);
        // 将插入成功的订单的id赋值到订单，作为rabbitmq解锁原因的区别，但本质没区别，只是解锁的原因不同而已
        BeanUtils.copyProperties(orderEntity, mqTo);

        List<OrderItemMqTo> itemMqTos = new ArrayList<>();
        // 订单中的订单项
        items.forEach((itemVo) -> {
            OrderItemEntity itemEntity = new OrderItemEntity();
            OrderItemMqTo orderItemMqTo = new OrderItemMqTo();

            // 关联订单Id 和 订单号
            itemEntity.setOrderId(orderEntity.getId());
            itemEntity.setOrderSn(orderEntity.getOrderSn());

            //
            itemEntity.setSkuId(itemVo.getSkuId());
            itemEntity.setSkuName(itemVo.getSkuTitle());
            itemEntity.setSkuPrice(itemVo.getPrice());
            itemEntity.setSkuQuantity(itemVo.getNum());
            // 成长值 和 积分需要去 sms优惠系统中查
            itemEntity.setGiftGrowth(1000);
            // 积分
            itemEntity.setGiftIntegration(1000);

            int insert = orderItemDao.insert(itemEntity);
            BeanUtils.copyProperties(itemEntity, orderItemMqTo);

            if (insert == 1) {
                log.info("【{}】订单项插入成功,订单项id【{}】", orderEntity.getOrderSn(), itemEntity.getId());
                itemMqTos.add(orderItemMqTo);
            }
        });

        mqTo.setOrderItems(itemMqTos);

        // AMQP.Tx.SelectOk selectOk = channel.txSelect();

        ConnectionFactory factory = rabbitTemplate.getConnectionFactory();
        Connection connection = factory.createConnection();
        // 是否开启事务
        Channel channel = connection.createChannel(false);

        rabbitTemplate.convertAndSend(
                RabbitMQConstant.order_exchange,
                RabbitMQConstant.order_create_event_routing_key,
                mqTo);
        // rabbitMQ接受消息 成功或者失败的回调方法
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {

            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                log.info("消息投递成功...删除消息");
                channel.basicAck(deliveryTag, multiple);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {

            }
        });

        // 订单创建成功了
        return orderEntity;
    }

    @Override
    public OrderVo getOrderInfoByOrderSn(String orderSn) {

        OrderEntity orderEntity = orderDao.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        List<OrderItemEntity> orderItemEntities = orderItemDao.selectList(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));

        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(orderEntity, orderVo);

        ArrayList<OrderItemVo> itemVos = new ArrayList<>();

        orderItemEntities.forEach(orderItemEntity -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItemEntity, orderItemVo);
            itemVos.add(orderItemVo);
        });

        orderVo.setOrderItems(itemVos);
        return orderVo;
    }

    @Override
    public void payedOrder(OrderEntity order) {

        orderDao.updateOrderStatusByOrderSn(order);
    }

}