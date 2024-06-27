package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //根据菜品id 去中间表查询 关联的 套餐id们
    // select setmeal_id from setmeal_dish where dish_id in(17,18,19);
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    //批量添加
    void insertBatch(List<SetmealDish> setmealDishes);

    //根据套餐id 删除关系表中相关联数据
    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);

    //根据套餐id 查询关联数据
    @Select("select * from setmeal_dish where setmeal_id=#{setmealId1}")
    List<SetmealDish> getBySetmealId(Long setmealId);

}