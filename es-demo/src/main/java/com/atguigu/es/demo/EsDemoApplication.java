package com.atguigu.es.demo;

import io.searchbox.client.JestClient;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class EsDemoApplication {

    @Autowired
    JestClient jestClient;

    public static void main(String[] args) {
        SpringApplication.run(EsDemoApplication.class, args);
    }

    @Test
    public void contextLoads() {
    }


    @Test
    public void index() throws IOException {
        User user = new User("张三", "zhangsan@qq.com", 36);

        // 1.获取一个Index动作的建造者
        Index.Builder builder = new Index.Builder(user)
                .index("user")
                .type("info")
                .id("1");
        // 2.构造出这个index动作
        Index build = builder.build();

        // 3.执行该动作
        DocumentResult result = jestClient.execute(build);

        System.out.println(result.getId());
        System.out.println(result.getValue("_version"));
        System.out.println(result.getId());
    }


    @Test
    public void updateData() throws IOException {
        User user = new User();
        user.setAge(30);
        user.setEmail("zhangsan@126.com");
        // 要手动将需要修改的对象放在doc中
        Map<String,User> map = new HashMap<>();
        map.put("doc",user);

        Update.Builder builder = new Update.Builder(map)
                .index("user")
                .type("info")
                .id("1");

        Update update = builder.build();

        // 打印出操作的详情
        System.out.println(update.toString());
        DocumentResult result = jestClient.execute(update);
    }

    @Test
    public void deleteData() throws IOException {

        Delete delete = new Delete.Builder("1").index("user").type("info").build();

        System.out.println(delete.toString());
        DocumentResult result = jestClient.execute(delete);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class User {
        private String username;
        private String email;
        private Integer age;
    }
}
