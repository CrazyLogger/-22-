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

@RequestMapping("/admin/dish")
@RestController
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    /**
     * 菜品添加
     *   请求方式post
     *   请求数据 json DishDTO接收
     *   不需要返回data数据
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("菜品添加的信息:{}",dishDTO);
        //将菜品数据交给service
        dishService.saveWithFlavor(dishDTO);//添加菜品数据 菜品口味数据
        return Result.success();
    }
    /**
     *   完成菜品 条件分页查询
     *     请求方式 get
     *     请求路径 /page
     *     请求参数 普通参数 五个  使用DishPageQueryDTO
     *     返回数据 Result<PageResult>
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("查询条件为:{}",dishPageQueryDTO);
        //需要service完成查询 并封装出 PageResult对象
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);


    }
    /**
     * 完成菜品批量删除
     *    请求方式 delete
     *    请求参数 多值参数 ids  使用 @RequestParam List<Long> ids
     *
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除的id们:{}",ids);
        //serive删除
        dishService.deleteBatch(ids);
        return Result.success();
    }
    /**
     * 根据菜品id 查询菜品信息 （基本信息+口味列表信息）
     *   请求方式 get
     *   请求路径 /{id}
     *   请求参数 路径参数 @PathVariable接收
     *   返回数据
     *      Result<DishVO>
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品数据:{}",id);
        //找到service查  菜品基本信息+口味信息 DishVO
        DishVO dishVO = dishService.getByIdWithFlavors(id);
        return Result.success(dishVO);
    }
    /**
     * 完成 菜品修改
     *   请求方式 put
     *   请求参数 json DishDTO接收
     *   不需要返回数据
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改的菜品数据:{}",dishDTO);
        //交给service修改 修改两张表
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
    /**
     * 完成菜品的启售停售
     *    请求方式 post
     *    请求路径 /status/{status}
     *    请求参数  路径参数 status 表达是菜品修改后的状态
     *             普通参数 id 表达被修改菜品的id
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("id为"+id+"的菜品需要修改状态为:"+status);
        //修改状态的事情交给 service处理
        dishService.startOrStop(status,id);

        return Result.success();
    }
    /**
     * 根据分类查询菜品
     *   请求方式 get
     *   请求地址 /list
     *   请求参数  categoryId
     *   返回 Result--List--Dish
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("查询的是分类id是:"+categoryId);
        //找service 查询出菜品列表
        List<Dish>  list= dishService.list(categoryId);
        return Result.success(list);
    }


}