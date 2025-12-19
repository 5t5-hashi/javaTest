package com.example.demo.config;

import com.example.demo.entity.Brand;
import com.example.demo.entity.Category;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.mapper.BrandMapper;
import com.example.demo.mapper.CategoryMapper;
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
    public CommandLineRunner initData(UserMapper userMapper, OrderMapper orderMapper, ProductMapper productMapper,
                                      CategoryMapper categoryMapper, BrandMapper brandMapper) {
        return args -> {
            // 每次启动时检查：如果数据库没用户，就创建一个默认管理员
            if (userMapper.selectCount(null) == 0) {
                User admin = new User("admin", "123456", "admin");
                userMapper.insert(admin);
                System.out.println(">>> 数据库初始化完毕，已自动创建管理员: admin / 123456");
            }

            // 初始化分类数据
            if (categoryMapper.selectCount(null) == 0) {
                categoryMapper.insert(new Category("数码产品", 0L));
                categoryMapper.insert(new Category("家用电器", 0L));
                categoryMapper.insert(new Category("生活日用", 0L));
                System.out.println(">>> 初始分类数据已添加");
            }

            // 初始化品牌数据
            if (brandMapper.selectCount(null) == 0) {
                Brand apple = new Brand("Apple");
                apple.setLogo("https://picsum.photos/200/300?random=1");
                apple.setDescription("Think Different");
                brandMapper.insert(apple);

                Brand xiaomi = new Brand("Xiaomi");
                xiaomi.setLogo("https://picsum.photos/200/300?random=2");
                xiaomi.setDescription("为发烧而生");
                brandMapper.insert(xiaomi);

                Brand sony = new Brand("Sony");
                sony.setLogo("https://picsum.photos/200/300?random=3");
                sony.setDescription("Make.believe");
                brandMapper.insert(sony);
                
                System.out.println(">>> 初始品牌数据已添加");
            }

            // 初始化商品数据
            if (productMapper.selectCount(null) == 0) {
                Product p1 = new Product("MacBook Pro 16", 10, 5);
                p1.setPrice(new BigDecimal("18999.00"));
                p1.setCategoryId(1L); // 假设1是数码产品
                p1.setBrandId(1L);    // 假设1是Apple
                p1.setDescription("M3 Max芯片，性能怪兽");
                productMapper.insert(p1);

                Product p2 = new Product("iPhone 15 Pro", 100, 20);
                p2.setPrice(new BigDecimal("7999.00"));
                p2.setCategoryId(1L);
                p2.setBrandId(1L);
                productMapper.insert(p2);

                Product p3 = new Product("Sony WH-1000XM5", 3, 10);
                p3.setPrice(new BigDecimal("2499.00"));
                p3.setCategoryId(1L);
                p3.setBrandId(3L); // Sony
                productMapper.insert(p3);

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
