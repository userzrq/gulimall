package com.atguigu.gulimall.pms;

import com.atguigu.gulimall.pms.service.impl.AttrAttrgroupRelationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallPmsApplicationTests {

    @Test
    public void contextLoads() {

        Service mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(AttrAttrgroupRelationServiceImpl.class, Service.class);

        System.out.println(mergedAnnotation);
    }

}
