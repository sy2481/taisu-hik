package com.nbhy.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nbhy.modules.system.domain.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Set;

public interface UserMapper extends BaseMapper<User> {


    /**
     * 根据角色查询用户
     * @param roleId /
     * @return /
     */
    Set<Long> findByRoleId(Long roleId);


    /**
     * 根据角色查询是否有关联的用户
     * @param ids /
     * @return /
     */
    long countByRoles(@Param("ids") Set<Long> ids);


    List<User> findByTest(@Param(Constants.WRAPPER) Wrapper<User> queryWrapper);

//
//    List<UserDTO> queryAll(UserQueryCriteria criteria);
//
//    List<UserDTO> queryPage(UserQueryCriteria criteria, PageQuery pageQuery);

}
