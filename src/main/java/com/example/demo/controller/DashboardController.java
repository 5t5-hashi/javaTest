package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DashboardController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/todayOverview")
    public Map<String, Object> getTodayOverview() {
        // 准备返回的数据结构
        Map<String, Object> result = new HashMap<>();

        // 1. 获取时间范围
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        LocalDateTime endOfYesterday = endOfToday.minusDays(1);

        // 2. 查询今天的订单
        QueryWrapper<Order> todayQuery = new QueryWrapper<>();
        // create_time >= startOfToday AND create_time <= endOfToday
        todayQuery.ge("create_time", startOfToday).le("create_time", endOfToday);
        List<Order> todayOrders = orderMapper.selectList(todayQuery);

        // 3. 查询昨天的订单 (用于计算对比)
        QueryWrapper<Order> yesterdayQuery = new QueryWrapper<>();
        yesterdayQuery.ge("create_time", startOfYesterday).le("create_time", endOfYesterday);
        List<Order> yesterdayOrders = orderMapper.selectList(yesterdayQuery);

        // 4. 计算指标：今日订单数
        long todayOrderCount = todayOrders.size();
        long yesterdayOrderCount = yesterdayOrders.size();
        result.put("todayOrder", buildStatMap(todayOrderCount, yesterdayOrderCount));

        // 5. 计算指标：今日营收 (把金额加起来)
        BigDecimal todayRevenue = todayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal yesterdayRevenue = yesterdayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("todayRevenue", buildStatMap(todayRevenue, yesterdayRevenue));

        // 6. 计算指标：客单价 (营收 / 订单数)
        BigDecimal todayPerCustomer = todayOrderCount > 0 ? todayRevenue.divide(BigDecimal.valueOf(todayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal yesterdayPerCustomer = yesterdayOrderCount > 0 ? yesterdayRevenue.divide(BigDecimal.valueOf(yesterdayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        result.put("perCustomerTransaction", buildStatMap(todayPerCustomer, yesterdayPerCustomer));

        // 7. 计算指标：库存预警 (库存 < 阈值)
        QueryWrapper<Product> warningQuery = new QueryWrapper<>();
        // apply 用于手写 SQL 片段: "stock < warning_threshold"
        warningQuery.apply("stock < warning_threshold");
        Long warningCount = productMapper.selectCount(warningQuery);
        
        Map<String, Object> inventoryMap = new HashMap<>();
        inventoryMap.put("value", warningCount);
        inventoryMap.put("comparison", 0); // 库存预警暂不做昨日对比
        result.put("inventoryWarning", inventoryMap);

        return result;
    }

    // 辅助方法：构建 {value: 100, comparison: 0.1} 这样的 Map
    private Map<String, Object> buildStatMap(Number todayValue, Number yesterdayValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", todayValue);

        double today = todayValue.doubleValue();
        double yesterday = yesterdayValue.doubleValue();
        
        // 计算增长率：(今天 - 昨天) / 昨天
        if (yesterday != 0) {
            double comparison = (today - yesterday) / yesterday;
            // 保留3位小数
            map.put("comparison", Math.round(comparison * 1000.0) / 1000.0);
        } else {
            map.put("comparison", today > 0 ? 1.0 : 0.0);
        }
        return map;
    }
}