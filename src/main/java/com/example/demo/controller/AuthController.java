package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.*;

/**
 * 认证控制器
 * 负责处理登录等认证相关请求
 */
@RestController
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录接口
     * 接收用户名和密码，验证成功后返回 Token 和用户信息
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        // 1. 使用 MyBatis-Plus 的 QueryWrapper (普通版) 构建查询条件
        // 这种写法直接使用数据库字段名 "username"，更直观，不需要用 ::
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());

        // 2. 查询数据库
        User user = userMapper.selectOne(queryWrapper);
        
        if (user != null) {
            // 3. 验证密码 (实际项目请务必使用加密比对，如 BCrypt)
            if (user.getPassword().equals(request.getPassword())) {
                
                LoginResponse response = new LoginResponse();
                response.setToken("mock-token-" + UUID.randomUUID().toString());
                
                // 设置用户信息
                UserInfo userInfo = new UserInfo();
                userInfo.setName(user.getUsername());
                userInfo.setRole(user.getRole());
                response.setUserInfo(userInfo);
                
                // 设置菜单列表
                List<MenuItem> menuList = new ArrayList<>();
                menuList.add(new MenuItem("/dashboard/index", "仪表盘"));
                
                // 简单的权限控制演示
                if ("admin".equals(user.getRole())) {
                    // menuList.add(new MenuItem("/admin/settings", "系统设置"));
                }
                
                response.setMenuList(menuList);
                return response;
            }
        }

        throw new RuntimeException("用户名或密码错误");
    }

    // --- 数据传输对象 (DTO) ---

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private UserInfo userInfo;
        private List<MenuItem> menuList;
    }

    @Data
    public static class UserInfo {
        private String name;
        private String role;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuItem {
        private String key;
        private String label;
    }
}
