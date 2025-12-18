package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * MyBatis-Plus 版本
 */
@TableName("users") // 对应数据库中的 users 表
@Data
public class User {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;

    // MP 默认会自动把 camelCase 映射为 underscore_case (username -> username)
    private String username;

    private String password;

    private String role; // 例如: "admin", "common"

    // 方便我们 new 对象的构造函数
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {
    }
}
