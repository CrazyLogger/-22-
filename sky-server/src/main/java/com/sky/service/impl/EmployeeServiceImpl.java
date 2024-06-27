package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // DO 后期需要进行md5加密，然后再进行比对
        // 将用户录入的密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }
    /**
     *  员工的添加
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //employeeDTO里面有几个属性？  6个属性
        // 需要保存到数据包中的有几个属性？  12个属性   Employee
        Employee employee = new Employee();
        // 也就是 我们需要进行属性的复制 和 补充
        //employee 的 6个属性来自 employeeDTO
        BeanUtils.copyProperties(employeeDTO,employee);
        // 设置属性 7 password
        // 数据库存的密码是 MD5加密   我们会把默认密码设置为一个常量  类名直接调用的
        //String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);
        //属性8  设置状态
        employee.setStatus(StatusConstant.ENABLE);//状态也用常量
        //属性9  属性10  creteTime 创建时间 updateTime修改时间
//        employee.setCreateTime(LocalDateTime.now()); //  当前系统时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //属性11 属性12  创建人 修改人 需要写的是 创建人或者是修改的id
////        employee.setCreateUser(33L); //把创建人id 写死了
////        employee.setUpdateUser(33L); //把修改人id 写死了
//        //获取登录人的Id  然后把登录人的id设置到 这两个属性里面
//        // 从 当前线程将数据取出来
//        Long currentId = BaseContext.getCurrentId();
//        employee.setCreateUser(currentId); //把创建人id 写死了
//        employee.setUpdateUser(currentId); //把修改人id 写死了
        //谁登录修改的 那么这个id应该是谁
        // mapper层 完成数据的添加
        employeeMapper.insert(employee);//好了 最后交给mapper添加
    }



    /**
     *  完成 某个员工状态的修改
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 员工 要完成修改1     根据id修改状态
        // 员工 要完成修改2     根据id修改其他属性
        // 也就是 具体修改什么属性 其实是不具体的 是动态的 所以 这里可以采用动态sql形式完成动态更新 <set><if>

        //两个修改操作合二为一呢
        //定义一个对象 用来封装被修改的属性信息
        Employee updateEmp   = new Employee();
        //用来封装 id 条件
        updateEmp.setId(id);
        //用来封装 被修改的属性
        updateEmp.setStatus(status);
        // updateEmp可以封装 状态  也可以封装其他属性
        employeeMapper.update(updateEmp);

    }
    @Override
    public Employee getById(Long id) {
        Employee employee =  employeeMapper.getById(id);
        // employee 有密码 --密码不适合返回
        employee.setPassword("******");

        return employee;
    }
    @Override
    public void update(EmployeeDTO employeeDTO) {
        // 更新操作 employeeDTO 存储的是前端传递的修改的基本信息
        //  我们还有两个字段需要完成更新
        // update_time 记录更新时间 update_user 记录更新人
        //定义 Employee对象 用来封装需要 在数据库更新的数据
        Employee employee = new Employee();
        //把基本属性进行复制
        BeanUtils.copyProperties(employeeDTO,employee);


        //所有的更新信息都封装到 employee中
        employeeMapper.update(employee);
    }

}