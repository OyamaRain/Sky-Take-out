package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void addDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long id = dish.getId();//获取生成的菜品id
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> {dishFlavor.setDishId(id);});
            dishFlavorMapper.insert(dishFlavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //执行查询操作，获取到page对象
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        //获取总记录数
        long total = page.getTotal();
        //获取当前页数据集合
        List<DishVO> records = page.getResult();
        //封装并返回
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        //查询当前菜品状态，是否存在起售菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getStatusById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //起售菜品，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //查询当前菜品是否关联了套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealDishIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            //当前菜品关联了套餐，抛出一个业务异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的数据
        //删除菜品口味表中的数据
        for (Long id : ids) {
            dishMapper.deleteDish(id);
            dishFlavorMapper.deleteDishFlavorByDishId(id);
        }
    }

    @Override
    public DishVO getDishById(Long id) {
         Dish dish = dishMapper.getDishById(id);
         List<DishFlavor> dishFlavors = dishFlavorMapper.getFlavorById(id);
         //封装
         DishVO dishVO = new DishVO();
         BeanUtils.copyProperties(dish, dishVO);
         dishVO.setFlavors(dishFlavors);
         return dishVO;
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }

    @Override
    public List<Dish> getDishListByCategory(Long categoryId) {
        return dishMapper.getDishListByCategory(categoryId);
    }

    @Override
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        //修改菜品表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        Long id = dish.getId();//获取生成的菜品id(主键生成)
        //修改口味表
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            //flavors有值,删除原先数据
            Long dishId = dishDTO.getId();
            dishFlavorMapper.deleteDishFlavorByDishId(dishId);
            //插入新数据
            dishFlavors.forEach(dishFlavor -> {dishFlavor.setDishId(id);});
            dishFlavorMapper.insert(dishFlavors);
        }
        else {
            //flavors无值,删除原先数据
            Long dishId = dishDTO.getId();
            dishFlavorMapper.deleteDishFlavorByDishId(dishId);
        }
    }
}
