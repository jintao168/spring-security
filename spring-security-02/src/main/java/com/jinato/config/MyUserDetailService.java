package com.jinato.config;

import com.jinato.mapper.UserMapper;
import com.jinato.pojo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import com.jinato.pojo.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);
        if (ObjectUtils.isEmpty(user)){
            throw new UsernameNotFoundException("用户名不正确");
        }
        //查询权限信息
        List<Role> roles = userMapper.getRolesById(user.getId());
        user.setRoles(roles);
        return user;
    }
}
