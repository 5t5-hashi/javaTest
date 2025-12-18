package com.example.demo.config;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserMapper userMapper, OrderMapper orderMapper, ProductMapper productMapper) {
        return args -> {
            // 每次启动时检查：如果数据库没用户，就创建一个默认管理员
            // MP 的 selectCount 方法
            if (userMapper.selectCount(null) == 0) {
                User admin = new User("admin", "123456", "admin");
                // MP 的 insert 方法
                userMapper.insert(admin);
                System.out.println(">>> 数据库初始化完毕，已自动创建管理员: admin / 123456");
            }

            // 初始化商品数据
            if (productMapper.selectCount(null) == 0) {
                productMapper.insert(new Product("MacBook Pro", 10, 5));
                productMapper.insert(new Product("iPhone 15", 100, 20));
                productMapper.insert(new Product("AirPods", 3, 10)); // 库存预警：库存3 < 阈值10
                System.out.println(">>> 初始商品数据已添加");
            }

            // 初始化订单数据
            if (orderMapper.selectCount(null) == 0) {
                // 今天的数据 (2笔)
                orderMapper.insert(new Order(new BigDecimal("12000.00"), LocalDateTime.now()));
                orderMapper.insert(new Order(new BigDecimal("299.00"), LocalDateTime.now().minusHours(1)));

                // 昨天的数据 (3笔) - 用于展示对比数据的变化
                orderMapper.insert(new Order(new BigDecimal("8000.00"), LocalDateTime.now().minusDays(1)));
                orderMapper.insert(new Order(new BigDecimal("4000.00"), LocalDateTime.now().minusDays(1)));
                orderMapper.insert(new Order(new BigDecimal("100.00"), LocalDateTime.now().minusDays(1)));
                
                System.out.println(">>> 初始订单数据已添加");
            }
        };
    }
}
