package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.Category;
import com.example.demo.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理控制器
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取所有分类列表 (不分页)
     * 前端拿到数据后自行组装树形结构
     * @param name 搜索关键字 (可选，用于前端搜索过滤)
     */
    @GetMapping
    public List<Category> getList(@RequestParam(required = false) String name) {
        QueryWrapper<Category> query = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            query.like("name", name);
        }
        // 默认按排序值和ID倒序
        query.orderByDesc("sort").orderByDesc("id");
        return categoryMapper.selectList(query);
    }

    /**
     * 新增分类
     */
    @PostMapping
    public String add(@RequestBody Category category) {
        categoryMapper.insert(category);
        return "success";
    }

    /**
     * 修改分类
     */
    @PutMapping
    public String update(@RequestBody Category category) {
        categoryMapper.updateById(category);
        return "success";
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return "success";
    }
}
