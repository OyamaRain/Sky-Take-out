package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //将setmealDTO中的属性复制到setmeal中
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //将setmeal插入到数据库中
        setmealMapper.insert(setmeal);
        //获取setmeal的id(主键生成)
        Long setmealId = setmeal.getId();
        //获取setmealDTO中的setmealDishes
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                //将setmealId设置到setmealDish的setmealId中
                setmealDish.setSetmealId(setmealId);
                //将setmealDish插入到数据库中
                setmealDishMapper.insert(setmealDish);
            }
        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageSelect(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //判断当前套餐是否为启售状态，如果是则抛出业务异常
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.select(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                //当前套餐为启售状态，不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐表中的数据---setmeal
        for (Long id : ids) {
            setmealMapper.deleteById(id);
            //删除套餐菜品关系表中的数据---setmeal_dish
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    @Override
    public SetmealVO selectById(Long id) {
        Setmeal setmeal = setmealMapper.select(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //菜品：先删除，后添加
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishMapper.insert(setmealDish);
        }
    }

    @Override
    public void updateStatusById(Long id, Integer status) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        //判断该套餐下是否包含未启售菜品，有则无法启售
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        for (SetmealDish setmealDish : setmealDishes) {
            Long dishId = setmealDish.getDishId();
            Dish dishStatus = dishMapper.getStatusById(dishId);
            if(dishStatus.getStatus() == StatusConstant.DISABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
