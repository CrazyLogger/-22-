package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
  操作分类信息业务层的实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    //业务层依赖于mapper层
    @Autowired
    private CategoryMapper categoryMapper;
    /*
   完成 分类信息的添加
     基础信息 有四个
   */
    @Override
    public void save(CategoryDTO categoryDTO) {
        // 需要创建一个封装 添加所有的数据 Category分类对象
        Category category = new Category();
        // 将 前端传递的基础信息存到 这个对象中
        BeanUtils.copyProperties(categoryDTO,category);
        // 还要完成 status 创建时间 创建人 更新时间更新人 的设置
        category.setStatus(StatusConstant.DISABLE);//禁用
        //从BaseContext取出 登录id
        //        Long currentId = BaseContext.getCurrentId();
        //        category.setCreateTime(LocalDateTime.now());
        //        category.setUpdateTime(LocalDateTime.now());
        //        category.setCreateUser(currentId);
        //        category.setUpdateUser(currentId);
        //将完整的 分类信息存到数据库中
        categoryMapper.insert(category);
    }
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //使用分页插件来完成
        //1:开启分页  页码和每页条数
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        //2:程序员完成基础条件查询
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);//条件对象

        //3:解析返回的Page对象
        long total = page.getTotal();
        List<Category> records = page.getResult();

        return new PageResult(total,records);
    }
    @Override
    public void deleteById(Integer id) {
        //根据id删除分类信息
        // 这两个todo 后面在做---这两个todo就是 所谓的 逻辑外键的控制
        //TODO 如果该分类下有菜品信息 则抛出异常 告诉前端 该分类下有菜品 不能删除
        //TODO 如果该分类下有套餐信息 则抛出异常 告诉前端 该分类下有套餐 不能删除


        categoryMapper.deleteById(id);



    }
    @Override
    public void update(CategoryDTO categoryDTO) {
        // categoryDTO 前端传递过来的之后四个参数
        //构建一个 category对象 来表达封装修改的数据
        Category category = new Category();
        //传递的属性 放到category中
        BeanUtils.copyProperties(categoryDTO,category);
        // 补充 需要更新的基础信息
//        Long currentId = BaseContext.getCurrentId();
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(currentId);

        //由mapper来完成
        categoryMapper.update(category);
    }
    /*
     根据type属性 查询相关的分类列表
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}