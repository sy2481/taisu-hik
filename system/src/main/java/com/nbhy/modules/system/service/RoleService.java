/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nbhy.modules.system.service;

import com.nbhy.domain.PageBean;
import com.nbhy.domain.PageQuery;
import com.nbhy.modules.system.domain.entity.Role;
import com.nbhy.modules.system.domain.dto.RoleDTO;
import com.nbhy.modules.system.domain.query.RoleQueryCriteria;
import com.nbhy.modules.system.domain.dto.RoleSmallDTO;
import com.nbhy.modules.system.domain.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
public interface RoleService {

    /**
     * 查询全部数据
     * @return /
     */
    List<RoleDTO> queryAll();


    List<RoleSmallDTO> queryAllRoleSmallDTO();


    /**
     * 根据ID查询
     * @param id /
     * @return /
     */
    RoleDTO findById(long id);

    /**
     * 创建
     * @param resources /
     */
    void create(Role resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(Role resources);

    /**
     * 删除
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return /
     */
    Set<Long> findByUsersId(Long id);

//    /**
//     * 根据角色查询角色级别
//     * @param roles /
//     * @return /
//     */
//    Integer findByRoles(Set<Role> roles);

    /**
     * 待条件分页查询
     * @param criteria 条件
     * @param page 分页参数
     * @return /
     */
    PageBean<RoleDTO> queryAll(RoleQueryCriteria criteria, PageQuery page);

    /**
     * 查询全部
     * @param criteria 条件
     * @return /
     */
    List<RoleDTO> queryAll(RoleQueryCriteria criteria);

    /**
     * 导出数据
     * @param criteria 查询条件
     * @param response /
     * @throws IOException /
     */
    void download(RoleQueryCriteria criteria, HttpServletResponse response) throws IOException;

    /**
     * 获取用户权限信息
     * @param user 用户信息
     * @return 权限信息
     */
    List<GrantedAuthority> mapToGrantedAuthorities(UserDTO user);

    /**
     * 验证是否被用户关联
     * @param ids /
     */
    void verification(Set<Long> ids);

}
