package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import java.util.List;
/*
  操作 分类业务的接口
 */
public interface CategoryService {
    void save(CategoryDTO categoryDTO);
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    void deleteById(Integer id);
    void update(CategoryDTO categoryDTO);
    List<Category> list(Integer type);


}