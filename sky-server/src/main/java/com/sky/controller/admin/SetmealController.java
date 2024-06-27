package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    /*
    添加套餐及套餐和菜品的关联
       请求方式 post
       请求路径 /
       请求参数 json  后端设计了一个类SetmealDTO来接收
       返回 不需要数据
    */
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("需要添加的套餐信息:{}",setmealDTO);
        //找service来完成
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }
    /**
     * 套餐分页查询
     *   请求方式 get
     *   请求路径 /page
     *   请求参数 3个条件2个分页 五个参数 SetmealPageQueryDTO
     *   响应数据 Result---PageResult--total，records(集合泛型 SetmealVO)
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("当前查询条件为:{}",setmealPageQueryDTO);
        //交给service 完成 service返回前端需要PageResult对象
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 删除套餐业务
     *   请求方式 delete
     *   请求路径 如上
     *   请求参数 多值参数 ids  @RequestParam List<Long> ids
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除id数组:{}",ids);
        //service完成
        setmealService.deleteBatch(ids);
        return Result.success();
    }
    /**
     * 根据id查询套餐及套餐菜品关联数据
     *  请求方式get
     *  请求路径 /{id}
     *  请求参数 路径参数 @PathVariable接收
     *  响应数据 SetmealVO
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("需要查询的套餐id是:",id);
        //交给Service 查询套餐基本信息以及关联信息
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }
    /**
     *  完成套餐修改
     *    请求地址 同上
     *    请求方式 put
     *    请求参数  json格式数据 SetmealDTO接收
     */
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改的数据为:{}",setmealDTO);
        //service完成更新
        setmealService.update(setmealDTO);
        return Result.success();
    }
    /**
     * 完成套餐的启售和停售
     *   请求路径 /status/{status}
     *   请求方式 post
     *   请求参数 一个路径参数 表示状态  一个普通参数表示id
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("套餐id为"+id+"的,要修改状态为:"+status);
        //交给service
        setmealService.startOrStop(status,id);
        return Result.success();
    }



}