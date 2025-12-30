package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    @Insert("insert category(name, sort, type, status, create_time, update_time, create_user, update_user) " +
            "values(#{name}, #{sort}, #{type}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    List<Category> getByType(Integer type);
}
