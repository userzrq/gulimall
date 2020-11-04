package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-ums")
public interface MemberAddressFeignService {

    /**
     * 远程调用 获取某个用户的所有收货地址
     *
     * @param memberId
     * @return
     */
    @GetMapping("/member/{memberId}")
    public Resp<List<MemberAddressVo>> getMemberAddress(@PathVariable Long memberId);
}
