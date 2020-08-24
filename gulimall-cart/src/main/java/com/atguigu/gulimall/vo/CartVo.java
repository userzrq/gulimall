package com.atguigu.gulimall.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

                List<SkuFullReductionVo> reductions = item.getReductions();
                if (reductions != null && reductions.size() > 0) {
                    for (SkuFullReductionVo reduction : reductions) {
                        Integer type = reduction.getType();
                        // 0-打折 1-满减
                        if (type == 0) {
                            Integer fullCount = reduction.getFullCount();// 满几件
                            Integer discount = reduction.getDiscount();// 打几折

                            if (item.getNum() >= fullCount) {
                                BigDecimal reduceTotalPrice = item.getTotalPrice().multiply(new BigDecimal("0." + discount)); //折后价
                                BigDecimal reducePrice = item.getTotalPrice().subtract(reduceTotalPrice); // 折后价与原价的差价

                                reduce = reduce.add(reducePrice);
                            }
                        } else if (type == 1) { // 如果是满减
                            BigDecimal fullPrice = reduction.getFullPrice(); //满多少
                            BigDecimal reducePrice = reduction.getReducePrice(); // 减多少

                            if (item.getTotalPrice().subtract(fullPrice).compareTo(new BigDecimal("0.0")) >= 0) {
                                reduce = reduce.add(reducePrice);
                            }
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
