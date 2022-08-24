package com.nbhy.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbhy.modules.system.domain.entity.Role;
import com.nbhy.modules.system.domain.dto.RoleSmallDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据名称查询
     * @param name /
     * @return /
     */
    Long findByName(String name);

    /**
     * 删除多个角色
     * @param ids /
     */
    void deleteAllByIdIn(@Param("ids") Set<Long> ids);

    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return /
     */
    Set<Long> findByUserId(Long id);


    /**
     * 根据用户ID查询
     * @param userId 用户ID
     * @return /
     */
    Set<RoleSmallDTO> findRoleSmallByUserId(Long userId);


//    List<RoleDTO> queryAll(RoleQueryCriteria criteria);
}
