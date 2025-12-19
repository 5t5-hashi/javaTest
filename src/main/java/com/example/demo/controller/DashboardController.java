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

        // 4. 计算过去7天的数据趋势
        List<Object> orderTrend = new java.util.ArrayList<>();
        List<Object> revenueTrend = new java.util.ArrayList<>();
        List<Object> customerTrend = new java.util.ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            
            QueryWrapper<Order> dayQuery = new QueryWrapper<>();
            dayQuery.ge("create_time", start).le("create_time", end);
            List<Order> dayOrders = orderMapper.selectList(dayQuery);
            
            // 订单数
            long count = dayOrders.size();
            orderTrend.add(count);
            
            // 营收
            BigDecimal revenue = dayOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueTrend.add(revenue);
            
            // 客单价
            BigDecimal perCustomer = count > 0 ? revenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            customerTrend.add(perCustomer);
        }

        // 5. 计算指标：今日订单数
        long todayOrderCount = todayOrders.size();
        long yesterdayOrderCount = yesterdayOrders.size();
        StatItem orderItem = buildStatItem(todayOrderCount, yesterdayOrderCount);
        orderItem.setDataList(orderTrend);
        response.setTodayOrder(orderItem);

        // 6. 计算指标：今日营收
        BigDecimal todayRevenue = todayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal yesterdayRevenue = yesterdayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        StatItem revenueItem = buildStatItem(todayRevenue, yesterdayRevenue);
        revenueItem.setDataList(revenueTrend);
        response.setTodayRevenue(revenueItem);

        // 7. 计算指标：客单价
        BigDecimal todayPerCustomer = todayOrderCount > 0 ? todayRevenue.divide(BigDecimal.valueOf(todayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal yesterdayPerCustomer = yesterdayOrderCount > 0 ? yesterdayRevenue.divide(BigDecimal.valueOf(yesterdayOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        StatItem customerItem = buildStatItem(todayPerCustomer, yesterdayPerCustomer);
        customerItem.setDataList(customerTrend);
        response.setPerCustomerTransaction(customerItem);

        // 8. 计算指标：库存预警
        QueryWrapper<Product> warningQuery = new QueryWrapper<>();
        warningQuery.apply("stock < warning_threshold");
        Long warningCount = productMapper.selectCount(warningQuery);
        
        StatItem inventoryItem = new StatItem();
        inventoryItem.setValue(warningCount);
        inventoryItem.setComparison(0.0);
        
        // 库存历史通常需要快照表，这里模拟数据：假设过去6天都和今天一样（或者随机波动一下）
        List<Object> inventoryTrend = new java.util.ArrayList<>();
        for(int i=0; i<6; i++) {
             // 简单模拟：用当前值稍微波动一下，或者直接填当前值
             inventoryTrend.add(warningCount); 
        }
        inventoryTrend.add(warningCount);
        inventoryItem.setDataList(inventoryTrend);
        
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
        private List<Object> dataList; // 7天趋势数据
    }
}