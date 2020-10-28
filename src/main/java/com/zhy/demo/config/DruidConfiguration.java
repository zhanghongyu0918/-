package com.zhy.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/3/31
 * Time: 14:41
 * Description: druid配置类
 */
@Slf4j
@Configuration
public class DruidConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource defaultDataSource(){
        return new DruidDataSource();
    }

}
