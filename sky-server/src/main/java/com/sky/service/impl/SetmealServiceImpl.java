package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper; //套餐表--mapper

    @Autowired
    private SetmealDishMapper setmealDishMapper;//套餐菜品中间表--mapper
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        //1:完成套餐的新增
        Setmeal setmeal = new Setmeal();//对象封装基本信息的
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //找mapper完成套餐添加 --注意 添加的时候要完成主键回显
        System.out.println("当前前端传递的数据中是否有id值呢？"+setmeal.getId());
        setmealMapper.insert(setmeal);
        System.out.println("新增套餐之后,做了主键回显,再查看id:"+setmeal.getId());

        //2:完成套餐菜品 关系的新增
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long setmealId = setmeal.getId();//添加后的套餐id获取出来
        //我们前端应该设置  新增套餐 必选 菜品！！！
        for (SetmealDish setmealDish : setmealDishes) {
            //默认前端传递的 套餐菜品关系 是没有 套餐id
            //把套餐id 封装到每个 套餐菜品关系对象中
            setmealDish.setSetmealId(setmealId);
        }
        //将 套餐菜品关系数据 保存到 套餐菜品关系表中
        setmealDishMapper.insertBatch(setmealDishes);
    }
    /**
     * 完成套餐的分页查询
     * @param setmealPageQueryDTO 三个查询条件两个分页条件信息
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 1:开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        //2: 完成 条件查询
        Page<SetmealVO> page = setmealMapper.queryPage(setmealPageQueryDTO);
        //3:解析查询后的page对象
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        return new PageResult(total,records);
    }
    /**
     *  完成套餐的删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //删除套餐有什么业务需求吗
        //1:启售状态下的 套餐是不能删除的
        for (Long id : ids) {
            //id 套餐id
            //需要根据套餐id查询套餐信息
            Setmeal setmeal = setmealMapper.getById(id);
            //查看套餐的 状态
            Integer status = setmeal.getStatus();
            if(status== StatusConstant.ENABLE){
                //启售的套餐是不能删除的 因为有可能被用户 点上了
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //2:删除套餐的同时  同时删除 套餐菜品的关系数据
        for (Long id : ids) {
            //根据id删除套餐表中 套餐信息
            setmealMapper.deleteById(id);
            Long setmealId = id;//套餐id
            //根据套餐id 删除中间关联表中 相关的数据
            setmealDishMapper.deleteBySetmealId(setmealId);
        }
    }
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        SetmealVO setmealVO = new SetmealVO();

        //1:查询套餐基本信息  封装到setmealVO中
        Setmeal setmeal = setmealMapper.getById(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        //2:查询关联表中的关联信息 封装setmealVO中
        Long setmealId = id;//套餐id
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmealId);
        //把查询出来的数据 封装到VO中
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }
    /**
     * 修改套餐 及 相关连的 套餐菜品关联数据
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // setmealDTO  包含基本修改信息   包含 修改后的套餐菜品关联数据
        //1:修改基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改
        setmealMapper.update(setmeal);

        //获取一下 被修改套餐id
        Long setmealId = setmeal.getId();
        //2:修改套餐菜品关联数据
        // 删除原有的关联数据
        setmealDishMapper.deleteBySetmealId(setmealId);
        // 添加新的关联数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //我们前端应该设置  新增套餐 必选 菜品！！！
        for (SetmealDish setmealDish : setmealDishes) {
            //默认前端传递的 套餐菜品关系 是没有 套餐id
            //把套餐id 封装到每个 套餐菜品关系对象中
            setmealDish.setSetmealId(setmealId);
        }
        //将 套餐菜品关系数据 保存到 套餐菜品关系表中
        setmealDishMapper.insertBatch(setmealDishes);
    }
    @Autowired
    private DishMapper dishMapper;

    @Override
    public void startOrStop(Integer status, Long id) {
        Long setmealId = id;//套餐id
        //完成启售停售修改 套餐如果要变成启售 需检查其关联的所有的菜品数据 是否存在停售状态  如果有就抛出异常
        if(status==StatusConstant.ENABLE){//套餐要启售了
            //select d.* from dish d left join setmeal_dish sd on d.id = sd.dish_id
            //    where sd.setmeal_id = 32;
            List<Dish> dishList = dishMapper.getBySetmealId(setmealId);
            for (Dish dish : dishList) {
                if(dish.getStatus()==StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        // 做更新
        Setmeal updateSetmeal = new Setmeal();//封装 更新条件对象
        updateSetmeal.setId(id);
        updateSetmeal.setStatus(status);

        setmealMapper.update(updateSetmeal);
    }

}