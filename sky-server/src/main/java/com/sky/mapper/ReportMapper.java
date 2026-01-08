package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ReportMapper {
    Double sumByMap(Map<String, Object> map);

    Integer sumUsersByMap(Map<String, Object> map);

    Integer sumOrdersByMap(Map map);

    @Select("select count(*) from orders")
    Integer sumAllOrders();

    @Select("select count(*) from orders where status = 5")
    Integer sumAllValidOrders();
}
