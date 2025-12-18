package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库操作演示
 * 包含：实体类 (Entity) + 仓库接口 (Repository) + 控制器 (Controller)
 */
@RestController
@RequestMapping("/sql")
public class sqlDemo {

    // 注入 Repository，准备操作数据库
    @Autowired
    private ProductRepository productRepository;

    /**
     * 1. 增：保存一个商品
     * 请求：POST http://localhost:8080/sql/add?name=手机&price=999
     */
    @PostMapping("/add")
    public Product addProduct(@RequestParam String name, @RequestParam double price) {
        // 局部变量 p 不用写private或public，因为它只在当前方法中使用
        Product p = new Product(name, price);
        // 关键点：调用 save 方法，自动生成 INSERT 语句
        return productRepository.save(p);
    }

    /**
     * 2. 查：获取所有商品
     * 请求：GET http://localhost:8080/sql/list
     */
    @GetMapping("/list")
    public List<Product> listProducts() {
        // 关键点：调用 findAll 方法，自动生成 SELECT * 语句
        return productRepository.findAll();
    }
}

/**
 * ==========================================
 * 实体类 (Entity) -> 对应数据库表
 * ==========================================
 */
// - 作用 ：这是告诉 Spring Boot 和 Hibernate（ORM 框架）， 这个类对应数据库里的一张表 。
// - 效果 ：如果不指定表名，默认表名就是 product （类名小写）
@Entity
@Data
@NoArgsConstructor
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
}

/**
 * ==========================================
 * 仓库接口 (Repository) -> 负责操作数据库
 * ==========================================
 * 只要继承 JpaRepository，就自动拥有了增删改查能力
 */
// 泛型 <Product, Long> :
// - Product : 告诉它，这个仓库是专门用来管理 Product 表的。
// - Long : 告诉它， Product 表的主键类型是 Long 。
interface ProductRepository extends JpaRepository<Product, Long> {
}

