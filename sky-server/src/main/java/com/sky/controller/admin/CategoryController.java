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
    /*
     完成 分类信息的添加
     请求方式 post  路径 /admin/category
     参数 json格式的 使用CategoryDTO接收
     */
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("要添加的分类信息为:{}",categoryDTO);
        //将添加的数据给 service 完成添加
        categoryService.save(categoryDTO);//包含四个属性
        return Result.success();
    }
    /*
     完成分页查询  带条件
        请求方式 get 请求路径 /page
        请求参数 普通参数 四个属性 页码 每页显示条数  分类名称 分类类型
            我们采用 CategoryPageQueryDTO 接收
        响应内容
           Reuslt<PageResult>
     */
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("查询条件信息:{}",categoryPageQueryDTO);
        //contoller数据接收到了 业务层去处理 给我返回前端需要的PageReuslt对象
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    /*
   根据id删除分类信息
     请求方式 delete
     请求参数 普通参数 id
     返回数据 无
    */
    @DeleteMapping
    public Result delete(Integer id){
        log.info("删除的id是:"+id);
        //service 来完成
        categoryService.deleteById(id);

        return Result.success();
    }
    /*
   修改分类信息
      请求方式 put
      请求参数 json格式  CategoryDTO
      返回数据 无
    */
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改的分类信息是:"+categoryDTO);
        log.info("修改的分类信息是:{}",categoryDTO);
        //找service
        categoryService.update(categoryDTO);//传递被修改的信息

        return Result.success();

    }
    /**
     *  根据类型 type 查询相关的分类信息
     *   请求方式  get
     *   请求路径  /list
     *   请求参数  type  简单参数
     */
    @GetMapping("/list")
    public Result<List> list(Integer type){
        log.info("根据分类的类型:"+type+"查询相关分类列表");
        List<Category> categoryList = categoryService.list(type);
        return Result.success(categoryList);
    }
}