package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 基本信息
    private String name;
    private BigDecimal price;
    private String coverImage;
    private String description;
    
    // 库存与状态
    private Integer stock;
    private Integer warningThreshold;
    private Integer status; // 0-下架，1-上架
    
    // 关联信息
    private Long categoryId;
    private Long brandId;
    
    // 扩展属性
    private String specs; // JSON格式存储规格
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 为了兼容旧测试代码保留的构造函数（也可以更新测试代码后删除）
    public Product(String name, Integer stock, Integer warningThreshold) {
        this.name = name;
        this.stock = stock;
        this.warningThreshold = warningThreshold;
        this.price = BigDecimal.ZERO; // 默认值
        this.status = 1; // 默认上架
    }
}
