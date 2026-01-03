package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    //新增菜品
    void addDishWithFlavor(DishDTO dishDTO);

    //菜品分页查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //批量删除菜品
    void deleteDish(List<Long> ids);

    //查询回显(根据id查询菜品)
    DishVO getDishById(Long id);

    //菜品启售停售
    void updateStatus(Integer status, Long id);

    //根据id查询菜品
    List<Dish> getDishListByCategory(Long categoryId);

    //修改菜品
    void updateDish(DishDTO dishDTO);

    //根据条件查询菜品和对应的口味
    List<DishVO> listWithFlavor(Dish dish);
}
