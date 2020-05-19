package com.atguigu.gulimall.sms.enume;


public enum BoundWorkEnum {

    // 0000-1111 共16种情况
    // 优惠生效情况[1111（四个状态位，从右到左）;
    // 0 - 无优惠，成长积分是否赠送;
    // 1 - 无优惠，购物积分是否赠送;

    // 2 - 有优惠，成长积分是否赠送;
    // 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]

    ALLNO(0,"任何情况都不赠送积分"),                                                  // 0000
    UNFAVOURABLE_GROWTH(1,"有优惠，无积分赠送，无优惠，赠送成长积分"),                  // 0001
    UNFAVOURABLE_SHOPPING(2,"有优惠，无积分赠送，无优惠，赠送购物积分"),                //0010
    UNFAVOURABLE_GROWTHSHOPPING(3,"有优惠，无积分赠送，无优惠，赠送购物积分和成长积分"), // 0011

    FAVOURABLE_GROWTH(4,"有优惠，只赠送成长积分，无优惠，不赠送任何积分"),                                     // 0100
    FAVOURABLE_GROWTH_UNFAVOURABLE_GROWTH(5,"有优惠，只赠送成长积分，无优惠，只赠送成长积分"),                 //0101
    FAVOURABLE_GROWTH_UNFAVOURABLE_SHOPPING(6,"有优惠，只赠送成长积分，无优惠，只赠送购物积分"),               //0110
    FAVOURABLE_GROWTH_UNFAVOURABLE_GROWTHSHOPPING(7,"有优惠，只赠送成长积分，无优惠，赠送购物积分和成长积分"),  //0111

    FAVOURABLE_SHOPPING(8,"有优惠，只赠送购物积分，无优惠，不赠送任何积分"),                           //1000
    FAVOURABLE_SHOPPING_UNFAVOURABLE_GROWTH(9,"有优惠，只赠送购物积分，无优惠，只赠送成长积分"),       //1001
    FAVOURABLE_SHOPPING_UNFAVOURABLE_SHOPPING(10,"有优惠，只赠送购物积分，无优惠，只赠送购物积分"),    //1010
    FAVOURABLE_SHOPPING_UNFAVOURABLE_GROWTHSHOPPING(11,"有优惠，只赠送购物积分，无优惠，赠送购物积分和成长积分"),  //1011

    FAVOURABLE_GROWTHSHOPPING_UNFAVOURABLE_GROWTH(12,"有优惠，赠送成长积分和购物积分，无优惠，不赠送任何积分"),        //1100
    FAVOURABLE_GROWTHSHOPPING_UNFAVOURABLE_SHOPPING(13,"有优惠，赠送成长积分和购物积分，无优惠，只赠送成长积分"),      //1101
    FAVOURABLE_GROWTHSHOPPING_UNFAVOURABLE_GROWTHSHOPPING(14,"有优惠，赠送成长积分和购物积分，无优惠，只赠送购物积分"),    //1110
    ALLGET(15,"任何情况都能获得成长积分和优惠积分");             //1111


    private Integer code;
    private String msg;

    BoundWorkEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode(){
        return code;
    }

    public String  getMsg(){
        return msg;
    }
}
