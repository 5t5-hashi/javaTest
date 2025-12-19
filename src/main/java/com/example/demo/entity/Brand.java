package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("brands")
public class Brand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logo;
    private String description;

    public Brand(String name) {
        this.name = name;
    }
}
