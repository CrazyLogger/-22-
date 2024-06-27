package com.sky.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import com.sky.dto.DishPageQueryDTO;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Select;
import java.util.List;
/**
 * 操作菜品表的
 */
@Mapper
public interface DishMapper {
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    //根据id查询菜品
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);
    //根据id删除菜品
    @Delete("delete from  dish where id=#{id}")
    void delete(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
    List<Dish> list(Dish queryDish);

    @Select("select d.* from dish d left join setmeal_dish sd on d.id = sd.dish_id where sd.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}
