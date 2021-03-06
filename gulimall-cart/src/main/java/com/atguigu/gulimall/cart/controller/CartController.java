package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.vo.ClearCartVo;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Api(tags = "购物车系统")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    @Qualifier("otherExecutor")
    ThreadPoolExecutor executor;


    @PostMapping("/clearCartSku")
    public Resp<Object> clearSkuIds(@RequestBody ClearCartVo clearCartVo) {
        cartService.clearSkuIds(clearCartVo);

        return Resp.ok(null);
    }


    /**
     * 下单时远程调用的方法 用户必须先登录再下单
     *
     * @param request
     * @return
     */
    @ApiOperation("返回购物车中所有 选中的 商品信息以及总价格，优惠等信息")
    @GetMapping("/getItemsForOrder")
    public Resp<CartVo> getCartCheckItemsAndStatics(HttpServletRequest request) {
        /**
         * 登录后的token由后端返回，前端在请求的时候带上代表，代表用户身份
         */
        String authorization = request.getHeader("Authorization");
        Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(authorization);

        Long userId = (Long) jwtBody.get("userId");
        CartVo cartVo = cartService.getCartForOrder(userId);
        return Resp.ok(cartVo);
    }


    /**
     * 当内存不够时，运维可通过接口销毁线程池，释放资源
     *
     * @return
     */
    @GetMapping("/stop/other")
    public Resp<Object> closeUnnecessaryThreadPool() {
        // shutdown 和 shutdownnow的区别
        executor.shutdown();

        Map<String, Object> map = new HashMap();
        map.put("closeQueue", executor.getQueue().size());
        map.put("waitActiveCount", executor.getActiveCount());
        return Resp.ok(map);
    }


    /**
     * @param skuId
     * @param status        0代表不选中   1代表选中
     * @param userKey
     * @param authorization
     * @return
     * @RequestParam("skuIds") Long[] skuId, 必须的
     * @RequestParam("status") Integer status,
     * String userKey,从请求参数中取，不是必须的 == @RequestParam(value="skuIds",required=false)
     * <p>
     * 某个请求参数有多个值封装数组：
     * 传：skuId=1&skuId=2&skuId=3&skuId=4#
     * 请求参数中封装：@RequestParam("skuId") Long[] skuId,
     * <p>
     * 选中不选中会影响总价
     */
    @ApiOperation("选中/不选中购物车中的购物项")
    @PostMapping("/check")
    public Resp<CartVo> checkCart(@RequestParam("skuIds") Long[] skuId,
                                  @RequestParam("status") Integer status,
                                  String userKey,
                                  @RequestHeader(name = "Authorization", required = false) String authorization) {


        CartVo cartVo = cartService.checkCart(skuId, status, userKey, authorization);

        return Resp.ok(cartVo);
    }

    @ApiOperation("更新购物车商品数量")
    @PostMapping("/update")
    public Resp<CartVo> updateCart(@RequestParam(name = "skuId", required = true) Long skuId,
                                   @RequestParam(name = "num", defaultValue = "1") Integer num,
                                   String userKey,
                                   @RequestHeader(name = "Authorization", required = false) String authorization) {

        CartVo cartVo = cartService.updateCart(skuId, num, userKey, authorization);

        return Resp.ok(cartVo);
    }

    /**
     * 以后购物车的所有操作，前端最多会携带两个令牌
     * 1)登陆后的jwt放在请求头的"Authentication"字段（不一定会带）
     * 2)只要前端收到服务器响应的 userKey ，以后所有操作购物车的操作都会带上这个参数
     *
     * @return
     */
    @ApiOperation("获取购物车中的数据")
    @GetMapping("/list")
    public Resp<CartVo> getCart(String userKey,
                                @RequestHeader(name = "Authentication", required = false) String authentication) throws ExecutionException, InterruptedException {

        CartVo cartVo = cartService.getCart(userKey, authentication);

        return Resp.ok(cartVo);
    }


    /**
     * @param skuId
     * @param num
     * @param userKey        临时用户的令牌，如果有就传
     * @param authentication
     * @return
     */
    @ApiOperation("将某个sku添加到购物车")
    @GetMapping("/add")
    public Resp<Object> addToCart(@RequestParam(name = "skuId", required = true) Long skuId,
                                  @RequestParam(name = "num", defaultValue = "1") Integer num,
                                  String userKey,
                                  @RequestHeader(name = "Authentication", required = false) String authentication) throws ExecutionException, InterruptedException {

        // authentication = request.getHeader("Authentication");  Authentication header的值直接由注解获取

        CartVo cartVo = cartService.addToCart(skuId, num, userKey, authentication);

        Map<String, Object> map = new HashMap<>();
        // 当用户登录时，userKey为null，未登录时，会将临时用户令牌传给前端
        map.put("userKey", cartVo.getUserKey());
        map.put("item", cartVo.getItems());

        // 操作成功时返回给前端操作的用户标识，前端下次请求时带上，即能操作同一个购物车
        return Resp.ok(map);
    }
}
