package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.constant.BizCode;
import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.order.enume.OrderStatusEnume;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberAddressFeignService;
import com.atguigu.gulimall.order.feign.OrderCreateFeignService;
import com.atguigu.gulimall.order.feign.WareHouseFeignService;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.CartItemVo;
import com.atguigu.gulimall.order.vo.CartVo;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.cart.ClearCartVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.order.OrderFeignSubmitVo;
import com.atguigu.gulimall.order.vo.payment.PayAsyncVo;
import com.atguigu.gulimall.order.vo.ware.LockStockVo;
import com.atguigu.gulimall.order.vo.ware.SkuLock;
import com.atguigu.gulimall.order.vo.ware.SkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author userzrq
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    /**
     * RabbitTemplate：用于收发消息
     * AmqpAdmin：管理RabbitMQ中exchange, queue, binding的创建、删除
     */
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MemberAddressFeignService memberAddressFeignService;

    @Autowired
    private WareHouseFeignService wareHouseFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderCreateFeignService orderCreateFeignService;

    @Autowired
    JedisPool jedisPool;

    // ScheduledThreadPool 可调度的线程池
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    /**
     * 注入项目中配置的线程池
     */
    @Autowired
    ThreadPoolExecutor executor;

    //---------------------------------⬇ rabbitMQ测试  ==== 虚假的创建订单逻辑 ⬇----------------------------------------

    /**
     * rabbitMQ测试:创建订单
     *
     * @return
     */
    @Override
    public Order createOrder() {
        Order order = new Order();
        order.setOrderId(IdWorker.getId());
        order.setDesc("商品xxxxxx-xxxxx");
        // 0代表订单的未支付状态
        order.setStatus(0);

        // 订单创建完，将订单信息发送给MQ
        // String exchange, String routingKey, final Object object
        rabbitTemplate.convertAndSend("orderCreateExchange", "create.order", order);

        /**
         * 定时线程池执行也可以执行关单流程，但是没有持久化，订单数据与随着系统的生命周期结束而结束，MQ的优势在于不依赖于系统的持久化
         * 在延迟30s后执行任务
         */
        executorService.schedule(() -> {
            System.out.println(order + "已经过期,正准备查询数据库，决定是否关单");
        }, 30, TimeUnit.SECONDS);


        return order;
    }


    /**
     * 监听最后接收死信交换机发来信息的队列closeOrderQueue,消息被消费后删除
     */
    @RabbitListener(queues = "closeOrderQueue")
    public void closeOrder(Order order, Channel channel, Message message) throws IOException {
        System.out.println("收到的订单" + order.toString());
        /**
         * 拿到订单的id 去数据库里中查询支付状态，支付状态为未支付的，就关单并解库存
         */
        Long orderId = order.getOrderId();
        System.out.println("正在数据库中查询订单号【" + orderId + "】的支付状态:" + order.getStatus());

        if (order.getStatus() != 1) {
            System.out.println("该订单没有被支付，准备关单，数据库状态改为 -1");
        }

        // 给MQ回复，我们已经处理完此消息了
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    //---------------------------------⬆ rabbitMQ测试  ==== 虚假的关单逻辑⬆----------------------------------------


    /**
     * 异步多线程远程调用，避免单线程阻塞过多时间
     * 但是异步其实是另起线程了，与controller层进来的主线程不是同一线程
     * 而RequestContextHolder用的是ThreadLocal，异步线程中的 HttpServletRequest 对象为空，容易引发空指针
     * 因此要先获取主线程中的线程属性再赋值给异步线程
     *
     * @param userId
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrderData(Long userId) {
        log.info("从controller层进来的主线程号: {}", Thread.currentThread().getId());
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        // 获取
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        // 封装远程的用户地址信息
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            log.info("memberAddressFeignService服务进来的线程号:{}", Thread.currentThread().getId());

            // 利用ThreadLocal在同一线程共享数据（先将主线程中的 ServletRequestAttributes 复制一份到异步线程的 RequestContextHolder 上下文容器内）
            RequestContextHolder.setRequestAttributes(requestAttributes);
            Resp<List<MemberAddressVo>> memberAddress = memberAddressFeignService.getMemberAddress(userId);
            confirmVo.setAddresses(memberAddress.getData());
        }, executor);

        // 封装购物车请求，远程调用时是给其他微服务发一个新请求，【如何保证新的请求中仍持有代表用户身份的请求头】
        // 1.改造方法参数
        // 2.feign: 写一个拦截器
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            log.info("cartFeignService服务进来的线程号:{}", Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            Resp<CartVo> checkItemsAndStatics = cartFeignService.getCartCheckItemsAndStatics();
            confirmVo.setCartVo(checkItemsAndStatics.getData());
        }, executor);

        try {
            // 异步任务阻塞式等待（万一远程服务不可用，查不到购物车中的信息怎么办）
            CompletableFuture.allOf(future1, future2).get();

            // 创建一个交易令牌，并缓存到redis中，提交订单的时候要携带，，提交订单后会删这个令牌作为验证操作
            // String orderToken = UUID.randomUUID().toString().replace("-", "");
            String orderToken = IdWorker.getTimeId();

            redisTemplate.opsForValue().set(Constant.ORDER_TOKEN + orderToken, orderToken, Constant.ORDER_TOKEN_TIMEOUT, TimeUnit.MINUTES);
            confirmVo.setOrderToken(orderToken);
            return confirmVo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return confirmVo;
    }

    /**
     * @param orderSubmitVo
     * @param userId
     * @return
     */
    @Override
    public Resp<Object> submitOrder(OrderSubmitVo orderSubmitVo, Long userId) throws ExecutionException, InterruptedException {
        String orderToken = orderSubmitVo.getOrderToken();
        /**
         * 0.验是否重复提交了（也必须是原子性操作）
         * 在高并发环境下 if(token.equals(orderToken)){ redisTemplate.delete(Constant.ORDER_TOKEN + orderToken); }
         * 大量请求通过if判断后，就会执行大量的redis删除操作
         */
        String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        /**
         * LUA脚本保持操作的原子性
         * String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1])
         *                                                        else return 0 end";
         * RedisScript<T> script    执行的脚本
         * List<K> keys     KEYS数组，在Redis中需要查找的key值形成的数组
         * Object... args   ARGV数组，需要比对的参数
         */
        // Long executeResult = redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList(Constant.ORDER_TOKEN + orderToken), orderToken);

        // 获取jedis操作客户端
        Jedis jedis = jedisPool.getResource();

        Long executeResult = (Long) jedis.eval(script, Arrays.asList(Constant.ORDER_TOKEN + orderToken), Arrays.asList(orderToken));
        // redis中有令牌，验证通过后并删除了，成功删除返回1

        try {

            if (executeResult == 1) {
                // 1.验库存\锁库存 （要求操作具有原子性，防止验库存的时候有，锁的时候有没有）
                // 1.1) 先从购物车中获取勾选的购物项（假如用户在另一浏览器中继续勾选商品）
                CartVo cartVo = cartFeignService.getCartCheckItemsAndStatics().getData();

                List<CartItemVo> items = cartVo.getItems();
                List<Long> skuIds = new ArrayList<>();
                List<SkuLockVo> skuLockVos = new ArrayList<>();

                items.forEach((item) -> {
                    // 勾选中的商品
                    if (item.isCheck()) {
                        skuIds.add(item.getSkuId());
                        // 将商品封装成锁库存的vo：为哪个订单的哪种商品锁多少库存
                        skuLockVos.add(new SkuLockVo(item.getSkuId(), item.getNum(), orderToken));
                    }
                });
                // 1.2) 验库存同时锁库存
                Resp<LockStockVo> resp = null;
                try {
                    resp = wareHouseFeignService.lockAndCheckStock(skuLockVos);
                    log.info("返回的数据...{}", resp.getData());
                } catch (Exception e) {
                    Resp<Object> fail = Resp.fail(null);
                    fail.setCode(BizCode.SERVICE_UNAVAILABLE.getCode());
                    fail.setMsg(BizCode.SERVICE_UNAVAILABLE.getMsg());
                    return fail;
                }

                if (resp.getData().getLocked()) {
                    // 锁库存成功
                    // 2.验价格，与前端传来的价格进行比对
                    BigDecimal totalPrice = orderSubmitVo.getTotalPrice();
                    // 最新查询到的购物车的价格信息
                    // 去购物车服务 通过缓存中最新的数据进行查询
                    CartVo latestCart = cartFeignService.getCartCheckItemsAndStatics().getData();

                    int i = latestCart.getCartPrice().compareTo(totalPrice);
                    if (i != 0) {
                        // 验价失败
                        Resp<Object> fail = Resp.fail(null);
                        fail.setCode(BizCode.ORDER_NEED_REFRESH.getCode());
                        fail.setMsg(BizCode.ORDER_NEED_REFRESH.getMsg());
                        return fail;
                    } else {
                        // 3.生成订单，保存订单中所有的订单项，一个大订单由多个 order_item拼起来生成
                        // 远程生成保存订单与订单项信息
                        OrderFeignSubmitVo orderFeignSubmitVo = new OrderFeignSubmitVo();
                        orderFeignSubmitVo.setCartVo(latestCart);

                        // 对拷 remark addressId totalPrice提交订单总额  payType cartVo
                        BeanUtils.copyProperties(orderSubmitVo, orderFeignSubmitVo);

                        Long addressId = orderSubmitVo.getAddressId();
                        Resp<MemberAddressVo> memberAddressVoResp = memberAddressFeignService.info(addressId);
                        MemberAddressVo data = memberAddressVoResp.getData();
                        orderFeignSubmitVo.setUserId(userId);

                        orderFeignSubmitVo.setReceiverName(data.getName());
                        orderFeignSubmitVo.setReceiverDetailAddress(data.getDetailAddress());
                        orderFeignSubmitVo.setReceiverPhone(data.getPhone());
                        orderFeignSubmitVo.setOrderToken(orderToken);
                        orderFeignSubmitVo.setUserId(userId);
                        // 创建订单
                        Resp<OrderEntityVo> saveOrder = null;
                        try {
                            // 远程调用不一定会成功
                            // 但如果调用是成功的，返回数据时，order微服务挂了，往消息队列中添加消息的步骤就可能遗失了
                            // 所以要在oms的createAndSaveOrder远程服务方法中给mq发消息
                            saveOrder = orderCreateFeignService.createAndSaveOrder(orderFeignSubmitVo);
                        } catch (Exception e) {
                            Resp<Object> fail = Resp.fail(null);
                            fail.setCode(BizCode.SERVICE_UNAVAILABLE.getCode());
                            fail.setMsg(BizCode.SERVICE_UNAVAILABLE.getMsg());

                            // 失败了还要解锁库存
                            LockStockVo lockStockVo = resp.getData();
                            List<SkuLock> locks = lockStockVo.getLocks();
                            // 远程服务进行解锁...
                            // 但要考虑到，如果代码在这里炸了，远程服务就无法感知到需要解锁的库存，哪些需要解锁

                            return fail;
                        }

                        // 4.锁库存（已经在验库存的时候做过了，保持原子性操作）
                        // 5.清除用户购物车中选中的商品
                        List<Long> needClearSkuIds = new ArrayList<>();
                        latestCart.getItems().forEach((item) -> {
                            needClearSkuIds.add(item.getSkuId());
                        });
                        ClearCartVo clearCartVo = new ClearCartVo();
                        clearCartVo.setSkuIds(needClearSkuIds);
                        clearCartVo.setUserId(userId);

                        // 异步线程调用 + Feign远程调用
                        CompletableFuture<Void> clearFuture = CompletableFuture.runAsync(() -> {
                            cartFeignService.clearSkuIds(clearCartVo);
                        }, executor);

                        // 6.支付成功扣库存


                        clearFuture.get();
                        log.info("购物车商品删除成功");

                        return Resp.ok(saveOrder.getData());
                    }
                } else {
                    // 锁库存失败
                    Resp<Object> fail = Resp.fail(null);
                    fail.setCode(BizCode.STOCK_NOT_ENOUGH.getCode());
                    fail.setMsg(BizCode.STOCK_NOT_ENOUGH.getMsg());
                    List<SkuLock> locks = resp.getData().getLocks();
                    List<Long> skusNoStock = new ArrayList<>();

                    locks.forEach((item) -> {
                        Boolean successLocked = item.getSuccess();
                        if (!successLocked) {
                            skusNoStock.add(item.getSkuId());
                        }
                    });
                    Map<String, List<Long>> map = new HashMap<>();
                    map.put("notEnoughStockSkus", skusNoStock);
                    fail.setData(map);
                    return fail;
                }
            } else {
                Resp<Object> fail = Resp.fail(null);
                fail.setCode(BizCode.TOKEN_INVALID.getCode());
                fail.setMsg(BizCode.TOKEN_INVALID.getMsg());
                return fail;
            }
        } finally {
            jedis.close();
        }
    }

    @Override
    public void paySuccess(PayAsyncVo vo) {
        // 订单号
        String tradeNo = vo.getOut_trade_no();
        OrderMqTo mqTo = new OrderMqTo();
        mqTo.setOrderSn(tradeNo);
        // 修改订单状态

        OrderEntityVo orderEntityVo = new OrderEntityVo();
        orderEntityVo.setOrderSn(tradeNo);
        orderEntityVo.setStatus(OrderStatusEnume.PAYED.getCode());

        orderCreateFeignService.payedOrder(orderEntityVo);
        log.info("支付成功......详情:{}", vo);

        // 支付成功，将信息打到消息队列中，真正去执行扣库存的操作
        // order_pay_success_routing_key 这一个路由键会打到两个队列中
        rabbitTemplate.convertAndSend(RabbitMQConstant.order_exchange, RabbitMQConstant.order_pay_success_routing_key, mqTo);
    }
}
