package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer stock;
    private Integer warningThreshold;

    public Product(String name, Integer stock, Integer warningThreshold) {
        this.name = name;
        this.stock = stock;
        this.warningThreshold = warningThreshold;
    }
}