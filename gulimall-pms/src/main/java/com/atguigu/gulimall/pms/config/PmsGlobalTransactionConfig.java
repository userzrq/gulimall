package com.atguigu.gulimall.pms.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PmsGlobalTransactionConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource originDataSource(@Value("${spring.datasource.url}") String url) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        return hikariDataSource;
    }

    /**
     * Spring里面默认的数据源必须是seata包装的
     *
     * @return
     */
    @Bean
    @Primary    // 主要数据源，除非按照id获取，按照类型获取时优先获取此Bean对象
    public DataSource dataSource(DataSource dataSource) {

        // 数据源对象需要被seata包装
        return new DataSourceProxy(dataSource);
    }
}
