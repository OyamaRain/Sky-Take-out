package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //分页查询
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：page{},pageSize{}", categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    //启用或禁用分类
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id){
        log.info("启用或禁用分类：状态{},ID{}", status, id);
        categoryService.status(status, id);
        return Result.success();
    }

    //修改分类
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    //新增分类
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类{}", categoryDTO.getSort());
        categoryService.save(categoryDTO);
        return Result.success();
    }

    //根据id删除分类
    @DeleteMapping
    public Result delete(Long id){
        log.info("根据id删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();
    }

    //根据类型查询分类
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        log.info("根据类型查询分类：{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
