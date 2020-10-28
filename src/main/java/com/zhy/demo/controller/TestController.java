package com.zhy.demo.controller;

import com.zhy.demo.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/3/30
 * Time: 14:42
 * Description: 测试接口
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    TestService testService;

    @RequestMapping("/hello")
    public String hello() {
        log.info("【Hello World】");
        return "Hello World";
    }

    @RequestMapping("/sql")
    public String testSql(){
        return testService.testSql();
    }
}
