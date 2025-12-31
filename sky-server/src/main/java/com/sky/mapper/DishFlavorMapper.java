package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insert(List<DishFlavor> dishFlavors);

    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteDishFlavorByDishId(Long dishId);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getFlavorById(Long id);
}
