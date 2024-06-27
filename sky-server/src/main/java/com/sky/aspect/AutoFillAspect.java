package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
  自定义 切面   实现公共字段填充
 */
@Aspect //标明当前是一个切面类
@Component //将该对象交给spring关键
@Slf4j
public class AutoFillAspect {

    //配置切面  =  切入点+通知     切入点---@Before(切入点表达式)--注解方式
    // 通知  --- 表现为一个方法

    //切入点 是一个表达式  如果是注解"@annotation(注解的位置)"
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFilPointCut(){}

    // 通知--表现为一个方法
    @Before("autoFilPointCut()")//配置了一个切面
    public void autoFill(JoinPoint joinPoint){
        //切面要干什么？
        log.info("哥们是aop的切面 在执行被增强方法之前执行。。我要在执行之前完成公共字段的填充");
        //对原始的方法进行增强--对原始方法中的 参数  进行赋值操作！！！
        // 1:获取 原始方法method对象形式  和传递的实际参数
        // 1.1 获取原始方法的方法签名
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        // 1.2 通过方法签名 获取 方法的反射形式 Method
        Method method = signature.getMethod();
        // 1.3  通过joinPoint 获取方法的参数
        Object[] args = joinPoint.getArgs();//获取被拦截的方法的参数列表
        if(args==null || args.length==0){//非空判断
            //说明没有参数  没法进行自动填充
            return;
        }
        Object entry = args[0];//entry 就是 被拦截方法的参数

        //2: 区分是新增还是更新  新增添加四个字段 更新 添加两个字段
        // 2.1 通过 method反射形式 判断 @AutoFill注解上 是新增还是更新
        // 解析  方法上的注解
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        //解析注解的值
        OperationType type = autoFill.value();

        //准备好数据
        LocalDateTime now = LocalDateTime.now();//当前时间
        Long currentId = BaseContext.getCurrentId();//登录人id

        // type 只有俩取值 INSERT  UPDATE
        if(type==OperationType.INSERT){
            log.info("新增操作 设置四个属性 到 参数entry中");
            try {
                //完成四个属性添加
                //只能通过反射形式设置
                //找到  entry中的setUpdateTime方法
                Class entryClass = entry.getClass();
                //需要找到 设置值四个方法
                //  setCreateTime  LocalDateTime 类型
                Method setCreateTime = entryClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                //  setUpdateTime  LocalDateTime 类型
                Method setUpdateTime = entryClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //  setCreateUser  Long 类型
                Method setCreateUser = entryClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                // setUpdateUser  Long 类型
                Method setUpdateUser = entryClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //反射运行四个方法
                setCreateTime.invoke(entry,now);
                setUpdateTime.invoke(entry,now);
                setCreateUser.invoke(entry,currentId);
                setUpdateUser.invoke(entry,currentId);
            }catch (Exception e){
                e.printStackTrace();
                log.info("没有对应set方法,反射异常");
            }

        }else{
            log.info("更新操作 设置两个属性 到 参数entry中");

            try {
                //完成2个属性添加
                //只能通过反射形式设置
                //找到  entry中的setUpdateTime方法
                Class entryClass = entry.getClass();
                //需要找到 设置值四个方法

                //  setUpdateTime  LocalDateTime 类型
                Method setUpdateTime = entryClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                // setUpdateUser  Long 类型
                Method setUpdateUser = entryClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //反射运行2个方法

                setUpdateTime.invoke(entry,now);

                setUpdateUser.invoke(entry,currentId);


            }catch (Exception e){
                e.printStackTrace();
                log.info("没有对应set方法,反射异常");
            }
        }


    }
}