package com.jinato.mapper;

import com.jinato.pojo.Role;
import com.jinato.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Mapper
public interface UserMapper {
    //根据用户名返回用户方法
    User loadUserByUsername(@Param("username") String username);

    //根据用户id查询用户角色信息
    List<Role> getRolesById(@Param("uid") Integer uid);

}
