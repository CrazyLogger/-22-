package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/*
  分类表增删改查的接口
 */
@Mapper
public interface CategoryMapper {
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) VALUES " +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Category category);
    // 在 xml中写动态sql
    //@Select("select * from category where name like concat('%',#{name},'%') and type = #{type}")
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    @Delete("delete from category where id = #{id}")
    void deleteById(Integer id);

    @AutoFill(OperationType.UPDATE)
    void update(Category category);
    //这块业务后期还会进行改进 在我们后期 小程序端访问的时候
    @Select("select * from category where type=#{type}")
    List<Category> list(Integer type);

}