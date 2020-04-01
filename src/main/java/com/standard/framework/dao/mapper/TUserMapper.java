package com.standard.framework.dao.mapper;

import com.standard.framework.dao.model.TUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TUser record);
}