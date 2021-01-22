package com.zhy.demo.dao.mapper;

import com.zhy.demo.dao.model.CsShop;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CsShopMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CsShop record);

    CsShop selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CsShop record);

    int updateById(Integer shopId);
}