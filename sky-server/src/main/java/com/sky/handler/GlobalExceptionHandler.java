package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;
/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获 sql操作异常
     * @param ex
     * @return
     */
    @ExceptionHandler//用于接收异常的注解
    public Result exceptionHandler2(SQLIntegrityConstraintViolationException ex){
        log.error("异常信息：{}", ex.getMessage());
        String message = ex.getMessage();
        //判断 是不是 违背了唯一约束
        if(message.contains("Duplicate entry")){
            //拼接一个信息  jack 已经存在了
            String[] split = message.split(" ");
            String username = split[2];//拿到用户名
            //拼接一个返回的消息
            String msg = username+ MessageConstant.ALREADY_EXISTS;//常用的字符串开发中都会使用常量表达
            return Result.error(msg);//返回了一个统一的 结果
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
        //   Duplicate entry 'jack' for key 'employee.idx_username'
        //   Duplicate entry 'rose' for key 'employee.idx_username'
    }

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

}
