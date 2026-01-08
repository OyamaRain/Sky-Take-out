package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    @Insert("insert into order_detail (name, image, order_id, dish_id, setmeal_id, dish_flavor, number, amount) VALUES " +
            "(#{name}, #{image}, #{orderId}, #{dishId}, #{setmealId}, #{dishFlavor},#{number} , #{amount})")
    void insert(OrderDetail orderDetail);

    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    @Delete("delete from order_detail where order_id = #{id}")
    void deleteByOrderId(Long id);
}
