package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 该注解是来标识 哪些方法需要进行 公共字段的自动填充
 */
//两个元注解 修饰 我们注解
@Target(ElementType.METHOD)//自动填充注解用在方法上
@Retention(RetentionPolicy.RUNTIME)//自动填充注解 需要被解析--设置为运行的生命周期
public @interface AutoFill {
    //定义一个 操作选项
    OperationType value();//为啥定义这个呢？ 因为被增强的方法有可能设置四个属性(插入)  有可能设置两个属性(更新)
}
