package com.nbhy.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbhy.modules.system.domain.entity.RoleMenus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface RoleMenusMapper extends BaseMapper<RoleMenus> {
    void insertBatch(@Param("roleMenusList") Set<RoleMenus> roleMenuList);
}
