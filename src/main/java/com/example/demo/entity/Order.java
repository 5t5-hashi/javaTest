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
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;

    // 方便我们造数据的构造函数
    public Order(BigDecimal totalAmount, LocalDateTime createTime) {
        this.totalAmount = totalAmount;
        this.createTime = createTime;
    }
}