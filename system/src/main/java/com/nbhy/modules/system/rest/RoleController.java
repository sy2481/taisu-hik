///*
// *  Copyright 2019-2020 Zheng Jie
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *  http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package com.nbhy.modules.system.rest;
//
//import com.nbhy.annotation.Log;
//import com.nbhy.annotation.validation.Update;
//import com.nbhy.domain.PageBean;
//import com.nbhy.domain.PageQuery;
//import com.nbhy.modules.system.domain.dto.RoleSmallDTO;
//import com.nbhy.modules.system.domain.entity.Role;
//import com.nbhy.modules.system.service.RoleService;
//import com.nbhy.modules.system.domain.dto.RoleDTO;
//import com.nbhy.modules.system.domain.query.RoleQueryCriteria;
//import com.nbhy.result.CommonResult;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.*;
//
///**
// * @author Zheng Jie
// * @date 2018-12-03
// */
//@RestController
//@RequiredArgsConstructor
//@Api(tags = "系统：角色管理")
//@RequestMapping("/api/roles")
//public class RoleController {
//
//    private final RoleService roleService;
//
//    private static final String ENTITY_NAME = "角色";
//
//    @ApiOperation("获取单个role")
//    @GetMapping(value = "/{id}")
//    @PreAuthorize("@el.check('roles:list')")
//    public CommonResult<RoleDTO> query(@PathVariable Long id){
//        return  CommonResult.querySuccess(roleService.findById(id),ENTITY_NAME);
//    }
//
//    @Log("导出角色数据")
//    @ApiOperation("导出角色数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('role:list')")
//    public void download(HttpServletResponse response, RoleQueryCriteria criteria) throws IOException {
//        criteria.setLevelSort(true);
//        roleService.download(criteria, response);
//    }
//
//    @ApiOperation("返回全部的角色")
//    @GetMapping(value = "/all")
//    @PreAuthorize("@el.check('roles:list','user:add','user:edit')")
//    public CommonResult<List<RoleSmallDTO>> query(){
//        return CommonResult.querySuccess(roleService.queryAllRoleSmallDTO(),ENTITY_NAME);
//    }
//
//    @Log("查询角色")
//    @ApiOperation("查询角色")
//    @GetMapping
//    @PreAuthorize("@el.check('roles:list')")
//    public CommonResult<PageBean<RoleDTO>> query(RoleQueryCriteria criteria, PageQuery pageable){
//        criteria.setId(true);
//        return  CommonResult.querySuccess(roleService.queryAll(criteria,pageable),ENTITY_NAME);
//    }
//
//
//
//    @Log("新增角色")
//    @ApiOperation("新增角色")
//    @PostMapping
//    @PreAuthorize("@el.check('roles:add')")
//    public CommonResult<Object> create(@Validated @RequestBody Role resources){
//        resources.setId(null);
//        roleService.create(resources);
//        return  CommonResult.insertSuccess(ENTITY_NAME);
//    }
//
//    @Log("修改角色")
//    @ApiOperation("修改角色")
//    @PutMapping
//    @PreAuthorize("@el.check('roles:edit')")
//    public CommonResult<Object> update(@Validated(Update.class) @RequestBody Role resources){
//        roleService.update(resources);
//        return  CommonResult.updateSuccess(ENTITY_NAME);
//    }
//
//
//
//    @Log("删除角色")
//    @ApiOperation("删除角色")
//    @DeleteMapping("/{id}")
//    @PreAuthorize("@el.check('roles:del')")
//    public CommonResult<Object> delete(@PathVariable("id") Long id){
//        RoleDTO role = roleService.findById(id);
//        Set<Long> ids = new HashSet<Long>(1){{
//            add(id);
//        }};
//        // 验证是否被用户关联
//        roleService.verification(ids);
//        roleService.delete(ids);
//        return  CommonResult.deleteSuccess(ENTITY_NAME);
//    }
//
//
//    @Log("删除多个角色")
//    @ApiOperation("删除多个角色")
//    @DeleteMapping
//    @PreAuthorize("@el.check('roles:del')")
//    public CommonResult<Object> delete(@RequestBody Set<Long> ids){
//        // 验证是否被用户关联
//        roleService.verification(ids);
//        roleService.delete(ids);
//        return  CommonResult.deleteSuccess(ENTITY_NAME);
//    }
//
//
//}
