package com.nbhy.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbhy.modules.system.domain.entity.Menu;
import com.nbhy.modules.system.domain.dto.MenuSmallDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


public interface MenuMapper extends BaseMapper<Menu> {


    /**
     * 查找除了基础菜单之外的菜单或者按钮
     * @return
     */
    List<MenuSmallDTO> findControlMenu();

    /**
     * 根据角色ID和菜单的类型进行查询
     * @param roleIds
     * @param type
     * @return
     */
    Set<String> findByRoleIdsAndIsMenu(@Param("roleIds") Set<Long> roleIds, @Param("type") byte type);


    /**
     * 根据类型查询所有资源
     * @param type
     * @return
     */
    Set<String> queryAllResources(byte type);



    /**
     * 查询所有基础菜单的ID
     * @return
     */
    List<Long> findByBasics();

    /**
     * 根据角色查询所有权限
     * @param roleIds
     * @return
     */
    Set<String> findByRolePermissions(@Param("roleIds") Set<Long> roleIds);

    Set<MenuSmallDTO> findMenuSmallDTOByRoleIds(Long id);

}
