package com.atguigu.gulimall.cart.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 整个购物车
 */
@Data
public class CartVo {

    private Integer total; // 总商品量

    private BigDecimal totalPrice; // 总商品价格

    private BigDecimal reductionPrice; // 优惠减去的价格

    private BigDecimal cartPrice; // 购物车应该支付的价格

    /**
     * 关键项,购物车中所有的购物项
     * ---------------------------------------------
     */
    @Getter
    @Setter
    private List<CartItemVo> items;

    /**
     * 临时用户key
     */
    @Getter
    @Setter
    private String userKey;


    /**
     * --------------------------------------------- 剩下的属性都是根据购物车中的购物项计算的
     */

    public Integer getTotal() {
        Integer num = 0;
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                if (!item.isCheck()) {
                    continue;
                }
                num += item.getNum();
            }
        }
        return num;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = new BigDecimal("0.0");
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                // 累加每个购物项的总价格
                if (!item.isCheck()) {
                    continue;
                }
                BigDecimal totalPrice = item.getTotalPrice();
                total = total.add(totalPrice);
            }
        }
        return total;
    }


    /**
     * 能优惠/促销的金额
     *
     * @return
     */
    public BigDecimal getReductionPrice() {
        BigDecimal reduce = new BigDecimal("0.0");

        // 拿到每一项的满减信息和优惠信息
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                if (!item.isCheck()) {
                    continue;
                }
                List<SkuFullReductionVo> reductions = item.getReductions();

                LinkedBlockingDeque<SkuFullReductionVo> fullReductionLinkedBlockingDeque = new LinkedBlockingDeque<>();

                for (SkuFullReductionVo reduction : reductions) {
                    if (reduction.getAddOther() == 1) {   // 1 可以叠加其他优惠
                        fullReductionLinkedBlockingDeque.addFirst(reduction);   // 从队列的头部插入可叠加优惠
                    } else {
                        // 不能叠加其他优惠
                        fullReductionLinkedBlockingDeque.addLast(reduction);    // 从队列的尾部插入不可叠加优惠
                    }
                }


                if (reductions != null && reductions.size() > 0) {
                    for (SkuFullReductionVo reduction : fullReductionLinkedBlockingDeque) {

//                    }
//                    for (SkuFullReductionVo reduction : reductions) {
                        Integer type = reduction.getType();
                        Integer addOther = reduction.getAddOther();

                        // type: 0-打折 1-满减
                        if (type == 0) {
                            Integer fullCount = reduction.getFullCount();// 满几件
                            BigDecimal discount = reduction.getDiscount();// 打几折

                            if (item.getNum() >= fullCount) {
                                // 折后价
                                // 如果打几折的参数 discount 为整型的话
                                // 100/100 = 1 + "." + 100 %100 = 1.0
                                // 98/100  = 0 + "." + 98 % 100 = 0.98
                                BigDecimal reduceTotalPrice = item.getTotalPrice().multiply(new BigDecimal("0." + discount.divide(new BigDecimal("100.00")))); //折后价
                                // 折后价与原价的差价
                                BigDecimal reducePrice = item.getTotalPrice().subtract(reduceTotalPrice); // 折后价与原价的差价

                                // 如果不打折，折后价会计算为0
                                if (reduceTotalPrice.compareTo(new BigDecimal("0")) == 0) {
                                    reduce = new BigDecimal("0");
                                } else {
                                    reduce = reduce.add(reducePrice);
                                }
                            }
                        } else if (type == 1) { // 如果是满减
                            BigDecimal fullPrice = reduction.getFullPrice(); //满多少
                            BigDecimal reducePrice = reduction.getReducePrice(); // 减多少

                            if (item.getTotalPrice().subtract(fullPrice).compareTo(new BigDecimal("0.0")) >= 0) {
                                reduce = reduce.add(reducePrice);
                            }
                        }
                        if (addOther == 0) { // 叠到的永远是队列从头到尾第一个碰到的不能叠加的为止
                            break;
                        }
                    }
                }
                // 计算优惠券可以减掉的金额
                List<SkuCouponVo> coupons = item.getCoupons();

                if (coupons != null && coupons.size() > 0) {
                    for (SkuCouponVo coupon : coupons) {
                        BigDecimal amount = coupon.getAmount();
                        reduce = reduce.add(amount);
                    }
                }
            }
        }
        return reduce;
    }


    /**
     * 促销后的价格
     *
     * @return
     */
    public BigDecimal getCartPrice() {
        BigDecimal reductionPrice = getReductionPrice();
        BigDecimal totalPrice = getTotalPrice();
        BigDecimal subtract = totalPrice.subtract(reductionPrice);
        return subtract;
    }

}
