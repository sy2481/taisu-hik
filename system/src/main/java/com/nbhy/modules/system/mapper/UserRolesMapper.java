package com.nbhy.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbhy.modules.system.domain.entity.UserRoles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRolesMapper extends BaseMapper<UserRoles> {
    void insertBatch(@Param("roleMenuList") List<UserRoles> roleMenuList);

}
