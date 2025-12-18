package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 继承 BaseMapper 后，自动拥有 CRUD 能力
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 这里不需要写 findByUsername，我们直接用 MP 提供的 Wrapper 查询即可
}
