package com.atguigu.gulimall.ums.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.exception.EmailExistException;
import com.atguigu.gulimall.commons.exception.PhoneExistException;
import com.atguigu.gulimall.commons.exception.UsernameAndPasswordInvalidException;
import com.atguigu.gulimall.commons.exception.UsernameExistException;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.ums.vo.MemberLoginVo;
import com.atguigu.gulimall.ums.vo.MemberRegisterVo;
import com.atguigu.gulimall.ums.vo.MemberRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.ums.dao.MemberDao;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.ums.service.MemberService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    /**
     * 重载方法，目前不用
     *
     * @param username
     * @param password
     */
    @Override
    public void registerUser(String username, String password) {
        MemberEntity member = new MemberEntity();

        // entity设置其他属性信息
        member.setUsername(username);

        // 密码加密存储
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(password);

        // 密码解密认证
        boolean matches = encoder.matches(password, encodePassword);
    }

    @Override
    public void registerUser(MemberRegisterVo vo) throws UsernameExistException, PhoneExistException, EmailExistException {
        MemberEntity member = new MemberEntity();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(vo.getPassword());

        member.setUsername(vo.getUsername());
        member.setPassword(encodePassword);
        member.setEmail(vo.getEmail());
        member.setMobile(vo.getUserphone());

        // 注册时 用户名，手机号，邮箱都不能为空
        Integer username = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", vo.getUsername()));
        if (username > 0) {
            throw new UsernameExistException();
        }
        Integer mobile = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", vo.getUserphone()));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
        Integer email = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("email", vo.getEmail()));
        if (email > 0) {
            throw new EmailExistException();
        }

        memberDao.insert(member);
    }

    @Override
    public MemberRespVo login(MemberLoginVo vo) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<MemberEntity>()
                .or().eq("username", vo.getLoginacct())
                .or().eq("mobile", vo.getLoginacct())
                .or().eq("email", vo.getLoginacct());

        MemberEntity entity = memberDao.selectOne(wrapper);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


        if (!Objects.isNull(entity)) {
            // 查询到合法的用户信息后再验证密码的合法性
            if (encoder.matches(vo.getPassword(), entity.getPassword())) {
                // 匹配到且，验证通过后
                // 1.把用户的详细信息保存在redis中
                // 为什么不使用jwt 令牌 ???
                String token = UUID.randomUUID().toString().replace("-", "").substring(0, 5);

                redisTemplate.opsForValue().set(Constant.LOGIN_USER_PREFIX + token, JSON.toJSONString(entity), Constant.LOGIN_USER_TIMEOUT_MINUTES, TimeUnit.MINUTES);

                Map<String, Object> payload = new HashMap<>();

                payload.put("token", token);
                payload.put("userId", entity.getId());
                String jwt = GuliJwtUtils.buildJwt(payload, null);

                MemberRespVo memberRespVo = new MemberRespVo();
                BeanUtils.copyProperties(entity, memberRespVo);
                memberRespVo.setToken(jwt);
                //memberRespVo.setId(entity.getId());
                return memberRespVo;
            } else {
                throw new UsernameAndPasswordInvalidException();
            }
        } else {
            //登陆失败
            throw new UsernameAndPasswordInvalidException();
        }

    }

}