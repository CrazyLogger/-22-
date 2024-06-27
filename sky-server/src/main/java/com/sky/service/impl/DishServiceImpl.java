package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional//事务控制注解  两个添加操作是同一事务
    public void saveWithFlavor(DishDTO dishDTO) {
        //dishDTO 有菜品基本信息 以及 菜品口味信息
        //1:将菜品基本信息添加到菜品表中
        // 创建一个Dish对象 来保存要存储的菜品基本信息
        Dish dish = new Dish();
        //拷贝数据
        BeanUtils.copyProperties(dishDTO,dish);
        //差什么数据呢？四个 基础字段 ？ 因为这四个交给公共字段填充
        dishMapper.insert(dish);//这个sql要完成主键回显
        //  要将 添加后的 主键值 回显到 dish对象中 因为后面还需要菜品id数据
        Long dishId = dish.getId();//回显后获取id

        //2:将相关连的口味信息 添加到口味表中
        //将集合中的每个口味信息 都先加上 菜品id
        // 取出所有的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //遍历集合和数组之前要做非空判断
        if(flavors==null || flavors.size()==0){
            return;//因为没有口味 不需要往口味表添加
        }
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        //循环做完 每个口味信息都有了 菜品id
        // 调用口味mapper 进行批量添加
        dishFlavorMapper.insertBatch(flavors);
    }
    //可能会涉及多张表
    @Override
    @Transactional //事务四大特性:原子性、一致性、隔离性、持久性
    // 事务隔离级别:读未提交、读已提交、可重复读、串行化
    // 事务玩法:
    public void startOrStop(Integer status, Long id) {
        //1:修改菜品信息 --修改菜品状态
        Dish dish = new Dish();//dish封装需要修改信息
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);

        //2:菜品停售时  包含菜品的套餐也要停售
        if(status==StatusConstant.DISABLE){//要停售菜品
            // 先根据菜品的id 查询出 关联的套餐id
            //  因为 这个功能的参数需要一个集合  将我的菜品id 放到集合中
            List<Long> ids = new ArrayList<>();
            ids.add(id);//将我们的菜品id添加到集合
            //这个功能已经实现了 setmealdishMapper
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
            // 到底有没有关联过套餐呢 ？
            if(setmealIds!=null && setmealIds.size()>0){//菜品关联上了套餐
                // 停售该套餐  套餐要一个个停售
                for (Long setmealId : setmealIds) {
                    //修改 setmeal表  找到指定的 套餐完成 状态的修改
                    // update setmeal set status=0 ,update_time=now(),update_user=1 where id=1;
                    //先来封装被修改的setmeal信息
                    Setmeal setmeal = new Setmeal();
                    setmeal.setStatus(StatusConstant.DISABLE);
                    setmeal.setId(setmealId);
                    //交给 setmealMapper去做
                    setmealMapper.update(setmeal);
                }
            }
        }

    }
    /**
     *  菜品分页查询
     * @param dishPageQueryDTO  两个分页条件  三个查询条件
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //1:开启分页操作
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //2:完成处理 条件查询 DishVO 存储 多表查询的结果
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        //3:解析page
        Long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total,records);
    }
    /*
    菜品批量删除
    */
    @Override
    @Transactional //涉及多张表的操作 需要加事务控制
    public void deleteBatch(List<Long> ids) { //17,18,19
        //删除菜品之前
        // do1 起售中的菜品不能删除   你怎么知道是启售的菜品呀？
        for (Long id : ids) {//ids 需要被删除的菜品id们
            //去数据库根据菜品id查询菜品数据 查询一下是不是启售的
            Dish dish = dishMapper.getById(id);//根据id查询菜品信息
            Integer status = dish.getStatus();
            if(status== StatusConstant.ENABLE){//是起售 启售  不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // do2 被套餐关联的菜品不能删除
        // 需要我们去完成中间表 mapper接口的创建
        List<Long>  setmaelIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        //select setmeal_id from setmeal_dish where dish_id in(17,18,19);
        if(setmaelIds!=null && setmaelIds.size()>0){//有关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // todo3 删除菜品，再删除关联的口味数据
        //动作是 删除一个菜品 再删除该菜品的关联数据
        for (Long id : ids) {
            dishMapper.delete(id);//根据菜品id删除菜品数据
            Long dishId = id;
            dishFlavorMapper.deleteByDishId(dishId);//根据菜品id 删除 指定口味信息
        }

    }
    /**
     * 根据菜品id查询 菜品基本信息 查询菜品关联的口味列表
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavors(Long id) {
        //1:查询菜品基本数据
        Dish dish = dishMapper.getById(id);
        //2: 查询菜品关联的口味列表
        Long dishId = id;
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishId);
        //3:合二为一
        DishVO dishVO = new DishVO();
        //基本菜品数据添加进来
        BeanUtils.copyProperties(dish,dishVO);
        //口味列表添加进来
        dishVO.setFlavors(flavors);

        return dishVO;
    }
    /*
   修改菜品基本信息 和 关联口味信息
    */
    @Override
    @Transactional //事务控制
    public void updateWithFlavor(DishDTO dishDTO) {
        //1:根据菜品id修改菜品基本信息
        Dish dish = new Dish();//封装修改的信息
        BeanUtils.copyProperties(dishDTO,dish);
        //更新时间 更新人 因为做了自动填充
        dishMapper.update(dish);

        Long dishId = dish.getId();
        //2.1: 根据菜品id 删除之前关联的口味
        dishFlavorMapper.deleteByDishId(dishId);//根据菜品id 删除 指定口味信息
        //2.2: 根据菜品id 添加最新关联的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //遍历集合和数组之前要做非空判断
        if(flavors==null || flavors.size()==0){
            return;//因为没有口味 不需要往口味表添加
        }
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        //循环做完 每个口味信息都有了 菜品id
        // 调用口味mapper 进行批量添加
        dishFlavorMapper.insertBatch(flavors);
    }
    @Override
    public List<Dish> list(Long categoryId) {
        //查询 select * from dish where categoryId=? and status=?
        //创建一个dish对象来封装查询的条件信息
        Dish queryDish = new Dish();
        queryDish.setCategoryId(categoryId);
        queryDish.setStatus(StatusConstant.ENABLE);


        return dishMapper.list(queryDish);
    }

}