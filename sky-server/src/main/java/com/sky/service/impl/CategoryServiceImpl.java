package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sky.constant.MessageConstant.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        PageResult pageResult = new PageResult(total, records);
        return pageResult;
    }

    @Override
    public void status(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .status(StatusConstant.DISABLE)
                .build();
        categoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查是否为启售状态
        Category category = categoryMapper.getById(id);
        if (category.getStatus() == StatusConstant.ENABLE) {
            throw new DeletionNotAllowedException(CATEGORY_ON_SALE);
        }
        // 检查是否关联了菜品或套餐
        Long categoryId = category.getId();
        List<Dish> dishListByCategory = dishMapper.getDishListByCategory(categoryId);
        List<Setmeal> setmeals = setmealMapper.selectByCategoryId(categoryId);
        if(!dishListByCategory.isEmpty()){
            throw new DeletionNotAllowedException(CATEGORY_BE_RELATED_BY_DISH);
        }
        else if(!setmeals.isEmpty()){
            throw new DeletionNotAllowedException(CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 删除分类
        categoryMapper.delete(id);
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.getByType(type);
    }
}
