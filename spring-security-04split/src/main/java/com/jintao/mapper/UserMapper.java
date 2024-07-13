package com.jintao.mapper;


import com.jintao.pojo.Role;
import com.jintao.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    //根据用户名返回用户方法
    User loadUserByUsername(@Param("username") String username);

    //根据用户id查询用户角色信息
    List<Role> getRolesById(@Param("uid") Integer uid);

}
