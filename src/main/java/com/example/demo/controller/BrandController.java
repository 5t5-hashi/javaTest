package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Brand;
import com.example.demo.mapper.BrandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 品牌管理控制器
 */
@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 获取品牌列表 (分页)
     * @param page 页码 (默认1)
     * @param size 每页大小 (默认10)
     * @param name 搜索关键字 (可选)
     */
    @GetMapping
    public IPage<Brand> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        // 1. 创建分页对象
        Page<Brand> pageParam = new Page<>(page, size);
        
        // 2. 构建查询条件
        QueryWrapper<Brand> query = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            query.like("name", name);
        }
        query.orderByDesc("id");
        
        // 3. 执行分页查询
        return brandMapper.selectPage(pageParam, query);
    }

    /**
     * 新增品牌
     */
    @PostMapping
    public String add(@RequestBody Brand brand) {
        brandMapper.insert(brand);
        return "success";
    }

    /**
     * 修改品牌
     */
    @PutMapping
    public String update(@RequestBody Brand brand) {
        brandMapper.updateById(brand);
        return "success";
    }

    /**
     * 删除品牌
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        brandMapper.deleteById(id);
        return "success";
    }
}
