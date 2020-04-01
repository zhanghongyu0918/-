package com.standard.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.standard.framework.dao.mapper.TUserMapper;
import com.standard.framework.dao.model.TUser;
import com.standard.framework.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/3/31
 * Time: 10:58
 * Description: No Description
 */
@Slf4j
@Service
public class TestServiceImpl implements TestService {
    @Resource
    private TUserMapper tUserMapper;

    @Override
    public String testSql() {
        TUser tUser = tUserMapper.selectByPrimaryKey(1);
        log.info("【tUser】{}", JSON.toJSONString(tUser));
        return JSON.toJSONString(tUser);
    }
}
