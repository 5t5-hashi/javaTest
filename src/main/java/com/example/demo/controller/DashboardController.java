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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
public class DashboardController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/todayOverview")
    public OverviewResponse getTodayOverview() {
        OverviewResponse response = new OverviewResponse();

        // 1. 获取时间范围
        // 今日时间范围：00:00:00 到 23:59:59
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        // 昨日时间范围：00:00:00 到 23:59:59
        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
        LocalDateTime endOfYesterday = endOfToday.minusDays(1);

        // 2. 查询今天的订单
        QueryWrapper<Order> todayQuery = new QueryWrapper<>();
        todayQuery.ge("create_time", startOfToday).le("create_time", endOfToday);
        List<Order> todayOrders = orderMapper.selectList(todayQuery);

        // 3. 查询昨天的订单
        QueryWrapper<Order> yesterdayQuery = new QueryWrapper<>();
        yesterdayQuery.ge("create_time", startOfYesterday).le("create_time", endOfYesterday);
        List<Order> yesterdayOrders = orderMapper.selectList(yesterdayQuery);

        // 4. 计算指标：今日订单数
        long todayOrderCount = todayOrders.size();
        long yesterdayOrderCount = yesterdayOrders.size();
        response.setTodayOrder(buildStatItem(todayOrderCount, yesterdayOrderCount));

        // 5. 计算指标：今日营收
        BigDecimal todayRevenue = todayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal yesterdayRevenue = yesterdayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTodayRevenue(buildStatItem(todayRevenue, yesterdayRevenue));

        // 6. 计算指标：客单价
        BigDecimal todayPerCustomer = todayOrderCount > 0 ? todayRevenue.divide(BigDecimal.valueOf(todayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal yesterdayPerCustomer = yesterdayOrderCount > 0 ? yesterdayRevenue.divide(BigDecimal.valueOf(yesterdayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        response.setPerCustomerTransaction(buildStatItem(todayPerCustomer, yesterdayPerCustomer));

        // 7. 计算指标：库存预警
        QueryWrapper<Product> warningQuery = new QueryWrapper<>();
        warningQuery.apply("stock < warning_threshold");
        Long warningCount = productMapper.selectCount(warningQuery);
        
        StatItem inventoryItem = new StatItem();
        inventoryItem.setValue(warningCount);
        inventoryItem.setComparison(0.0);
        response.setInventoryWarning(inventoryItem);

        return response;
    }

    // 辅助方法：构建 StatItem 对象
    private StatItem buildStatItem(Number todayValue, Number yesterdayValue) {
        StatItem item = new StatItem();
        item.setValue(todayValue);

        double today = todayValue.doubleValue();
        double yesterday = yesterdayValue.doubleValue();
        
        if (yesterday != 0) {
            double comparison = (today - yesterday) / yesterday;
            item.setComparison(Math.round(comparison * 1000.0) / 1000.0);
        } else {
            item.setComparison(today > 0 ? 1.0 : 0.0);
        }
        return item;
    }

    // --- DTO 内部类 ---

    @Data
    public static class OverviewResponse {
        private StatItem todayOrder;
        private StatItem todayRevenue;
        private StatItem perCustomerTransaction;
        private StatItem inventoryWarning;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatItem {
        private Object value;     // 可能是 Long (订单数) 也可能是 BigDecimal (金额)
        private Double comparison; // 增长率
    }
}