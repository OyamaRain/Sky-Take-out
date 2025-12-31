package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    //新增菜品
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        return Result.success();
    }

    //菜品分页查询
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    //批量删除菜品
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品:{}",ids);
        dishService.deleteDish(ids);
        return Result.success();
    }

    //菜品启售停售
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status,@RequestParam Long id){
        log.info("菜品{}启售停售:{}", id,status);
        dishService.updateStatus(status,id);
        return Result.success();
    }


    //查询回显(根据id查询菜品)
    @GetMapping("/{id}")
    public Result<DishVO> getDishById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }

    //根据分类id查询菜品
    @GetMapping("/list")
    public Result<List<Dish>> getDishListByCategory(Long categoryId){
        log.info("根据分类id查询菜品:{}",categoryId);
        List<Dish> dish = dishService.getDishListByCategory(categoryId);
        return Result.success(dish);
    }

    //修改菜品
    @PutMapping
    public Result updateDishWithFlavors(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }

}
