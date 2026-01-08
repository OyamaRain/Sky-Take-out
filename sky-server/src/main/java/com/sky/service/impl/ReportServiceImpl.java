package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.*;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            LocalDate date = begin.plusDays(1);
            dateList.add(date);
            begin = date;
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {

            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = reportMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            LocalDate date = begin.plusDays(1);
            dateList.add(date);
            begin = date;
        }

        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //统计每天的用户总量
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);

            Integer userSum = reportMapper.sumUsersByMap(map);
            totalUserList.add(userSum);

            //统计每天的新增用户
            LocalDateTime yesterday = beginTime.minusDays(1);
            LocalDateTime today = beginTime;

            Map yesterdayMap = new HashMap<>();
            yesterdayMap.put("begin", yesterday);
            yesterdayMap.put("end", today);
            Integer yesterdayUserSum = reportMapper.sumUsersByMap(yesterdayMap);
            if(yesterdayUserSum < userSum) {
                Integer newUser = userSum - yesterdayUserSum;
                newUserList.add(newUser);
            }else if(yesterdayUserSum == userSum) {
                newUserList.add(0);
            }
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO orderReportVO(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            LocalDate date = begin.plusDays(1);
            dateList.add(date);
            begin = date;
        }

        //统计每天的订单数
        List<Integer> orderList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer orders = reportMapper.sumOrdersByMap(map);
            orders = orders == null ? 0 : orders;
            orderList.add(orders);
        }

        //统计每天的有效订单数
        List<Integer> validOrderList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Integer orders = reportMapper.sumOrdersByMap(map);
            orders = orders == null ? 0 : orders;
            validOrderList.add(orders);
        }

        //统计订单总数
        Integer totalOrderCount = reportMapper.sumAllOrders();

        //统计有效订单数
        Integer validOrderCount = reportMapper.sumAllValidOrders();

        //统计订单完成率
        Double orderCompletionRate = validOrderCount / totalOrderCount.doubleValue();

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO salesTop10ReportVO(LocalDate begin, LocalDate end) {
        //根据日期查询卖出的所有菜品（有效的）
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<Orders> ordersList = orderMapper.getOrdersByTime(beginTime, endTime, Orders.COMPLETED);
        //循环遍历ordersList，获取orderDetailsList
        Map<String, Integer> map = new HashMap<>();
        for (Orders order : ordersList) {
            List<OrderDetail> orderDetailsList = orderDetailMapper.getByOrderId(order.getId());
            for (OrderDetail orderDetail : orderDetailsList) {
                if(map.containsKey(orderDetail.getName())) {
                    //如果map中已经存在该菜品或套餐，数量+1
                    Integer number = map.get(orderDetail.getName());
                    number = number + orderDetail.getNumber();
                    map.put(orderDetail.getName(), number);
                }
                map.put(orderDetail.getName(),orderDetail.getNumber());
            }

        }

        //对map进行排序,直接获取排序后的键值集合
        List<String> nameList = map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Integer> numberList = map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}
