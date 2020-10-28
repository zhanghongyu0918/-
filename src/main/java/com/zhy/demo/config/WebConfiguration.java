package com.zhy.demo.config;

import com.zhy.demo.filter.MyTestFilter;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/3/30
 * Time: 15:03
 * Description: web配置类
 */
@Configuration
public class WebConfiguration {
    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }

    @Bean
    public FilterRegistrationBean<MyTestFilter> testFilterRegistration() {

        FilterRegistrationBean<MyTestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MyTestFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("MyTestFilter");
        registration.setOrder(1);
        return registration;
    }

}
