package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工的登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工的退出")
    public Result<String> logout() {
        return Result.success();
    }
    /*
       完成员工的添加
          需要我们根据接口文档 设计请求内容和响应内容
        */
    @PostMapping
    public Result  save(@RequestBody EmployeeDTO employeeDTO){
        //  @RequestBody  将json格式数据转换成对应的对象
        //EmployeeDTO employeeDTO 接收 前端的数据的对象
        log.info("新增员工数据:{}",employeeDTO);//日志中展示 接收的数据
        employeeService.save(employeeDTO);//需要service帮忙完成员工信息的保存
        //爆红 是因为 没有这个方法 ..alt+enter解决
        return Result.success();//当前添加成功的结果

    }
    /**
     * 完成 员工的启用和禁用
     *    请求方式 post
     *    请求参数
     *       路径参数status  状态
     *       普通参数 id     哪一个员工
     *    返回数据 不需要封装data
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("id为"+id+"的员工,状态修改为:"+status);
        //需要找 service 完成 这名员工 状态的修改
        employeeService.startOrStop(status,id);//status设置的状态  id 谁要设置
        return Result.success();
    }
    /**
     * 完成 修改的回显 根据id查询 员工基本信息
     *   请求方式 get 请求参数路径参数id
     *   返回的数据 Reuslt<Employee>
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("查询的员工id是:"+id);
        //service来根据id查询一个员工信息
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
    /**
     * 完成员工的修改操作
     *   请求方式put 请求路径 /admin/employee
     *   请求数据 json格式 怎么接收 @RequestBody EmployeeDTO
     */
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("修改的数据为:{}",employeeDTO);
        //service完成修改 不需要返回内容
        employeeService.update(employeeDTO);
        return Result.success();
    }

}