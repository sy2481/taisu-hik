package com.nbhy.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbhy.domain.PageBean;
import com.nbhy.domain.PageQuery;
import com.nbhy.exception.BadRequestException;
import com.nbhy.exception.EntityExistException;
import com.nbhy.modules.system.domain.entity.Menu;
import com.nbhy.modules.system.domain.entity.Role;
import com.nbhy.modules.system.domain.entity.RoleMenus;
import com.nbhy.modules.system.mapper.MenuMapper;
import com.nbhy.modules.system.mapper.RoleMenusMapper;
import com.nbhy.modules.system.mapper.RoleMapper;
import com.nbhy.modules.system.mapper.UserMapper;
import com.nbhy.modules.system.service.RoleService;
import com.nbhy.modules.system.domain.dto.RoleDTO;
import com.nbhy.modules.system.domain.query.RoleQueryCriteria;
import com.nbhy.modules.system.domain.dto.RoleSmallDTO;
import com.nbhy.modules.system.domain.dto.UserDTO;
import com.nbhy.modules.system.service.mapstruct.RoleConverter;
import com.nbhy.modules.system.service.mapstruct.RoleSmallConverter;
import com.nbhy.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "role")
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleMenusMapper roleMenusMapper;
    private final RoleConverter roleConverter;
    private final RoleSmallConverter roleSmallConverter;
    private final RedisUtils redisUtils;
    private final UserMapper userMapper;
    private final MenuMapper menuMapper;


    @Override
    public List<RoleDTO> queryAll() {
        List<RoleDTO> roleDTOS = roleConverter.toDto(roleMapper.selectList(Wrappers.<Role>lambdaQuery().orderByAsc(Role::getLevel)));
        roleDTOS.forEach(roleDTO -> {
            roleDTO.setMenus(menuMapper.findMenuSmallDTOByRoleIds(roleDTO.getId()));
        });
        return roleDTOS;
    }

    @Override
    public List<RoleSmallDTO> queryAllRoleSmallDTO() {
        return roleSmallConverter.toDto(roleMapper.selectList(
                Wrappers.<Role>lambdaQuery().
                        select(Role::getId,Role::getName,Role::getLevel)));
    }

    @Override
    public List<RoleDTO> queryAll(RoleQueryCriteria criteria) {
        List<RoleDTO> roleDTOS = roleConverter.toDto(roleMapper.selectList(QueryHelp.getWrappers(criteria,Role.class)));
        roleDTOS.forEach(roleDTO -> {
            roleDTO.setMenus(menuMapper.findMenuSmallDTOByRoleIds(roleDTO.getId()));
        });
        return roleDTOS;
    }

    @Override
    public PageBean<RoleDTO> queryAll(RoleQueryCriteria criteria, PageQuery pageQuery) {
        Page<Role> page = roleMapper.selectPage(QueryHelp.getPage(pageQuery),QueryHelp.getWrappers(criteria,Role.class));
        List<RoleDTO> roleDTOS = page.getRecords().stream().map(role -> {
            RoleDTO roleDTO = roleConverter.toDto(role);
            roleDTO.setMenus(menuMapper.findMenuSmallDTOByRoleIds(roleDTO.getId()));
            return roleDTO;
        }).collect(Collectors.toList());
        return PageUtil.toPageBean(roleDTOS,page.getTotal(),pageQuery);
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO findById(long id) {
        Role role = roleMapper.selectById(id);
        ValidationUtil.isNull(role, "Role", "id", id);
        RoleDTO roleDTO = roleConverter.toDto(role);
        roleDTO.setMenus(menuMapper.findMenuSmallDTOByRoleIds(role.getId()));
        return roleDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Role resources) {
        if (roleMapper.findByName(resources.getName()) != null) {
            throw new BadRequestException("角色已存在");
        }
        if(CollectionUtil.isEmpty(resources.getMenus())){
            throw new BadRequestException("菜单不能为空，最起码要有一个菜单");
        }

        roleMapper.insert(resources);
        Set<RoleMenus> roleMenuList = new HashSet<>();
        resources.getMenus().stream().forEach(menu -> {
            RoleMenus roleMenus = new RoleMenus();
            roleMenus.setMenuId(menu.getId());
            roleMenus.setRoleId(resources.getId());
            roleMenuList.add(roleMenus);
        });
        //增加基础菜单
        List<Long> menuIds = menuMapper.findByBasics();
        menuIds.stream().forEach(menuId->{
            RoleMenus roleMenus = new RoleMenus();
            roleMenus.setMenuId(menuId);
            roleMenus.setRoleId(resources.getId());
            roleMenuList.add(roleMenus);
        });
        roleMenusMapper.insertBatch(roleMenuList);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Role resources) {
        Role role = roleMapper.selectById(resources.getId());
        ValidationUtil.isNull(role, "Role", "id", resources.getId());

        Long roleId = roleMapper.findByName(resources.getName());

        if (roleId != null && !roleId.equals(role.getId())) {
            throw new BadRequestException("角色已存在");
        }
        //1、更新角色
        role.setName(resources.getName());
        role.setDescription(resources.getDescription());
        role.setLevel(resources.getLevel());
        roleMapper.updateById(role);

        //2、更新角色关联的菜单
        List<Long> menuIds = (List<Long>)(Object) roleMenusMapper.selectObjs(Wrappers.<RoleMenus>lambdaQuery().
                select(RoleMenus::getMenuId).eq(RoleMenus::getRoleId, role.getId()));

        Set<Long> userIds = null;
        List<Long> resourcesMenuIds = resources.getMenus().stream().map(Menu::getId).collect(Collectors.toList());

        if(!menuIds.equals(resourcesMenuIds)){ //如果菜单有改变。那么更改菜单
            roleMenusMapper.delete(Wrappers.<RoleMenus>lambdaUpdate().eq(RoleMenus::getRoleId,role.getId()));
            //增加基础菜单
            List<Long> baseMenuIds = menuMapper.findByBasics();
            resourcesMenuIds.addAll(baseMenuIds);

            Set<RoleMenus> roleMenusList = resourcesMenuIds.stream().map(menuId -> {
                RoleMenus roleMenus = new RoleMenus();
                roleMenus.setRoleId(resources.getId());
                roleMenus.setMenuId(menuId);
                return roleMenus;
            }).collect(Collectors.toSet());
            roleMenusMapper.insertBatch(roleMenusList);
            userIds = userMapper.findByRoleId(role.getId());
        }
        // 更新相关缓存
        delCaches(role.getId(), userIds);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            // 更新相关缓存
            delCaches(id, null);
        }
        roleMenusMapper.delete(Wrappers.<RoleMenus>lambdaUpdate().in(RoleMenus::getRoleId,ids));
        roleMapper.deleteAllByIdIn(ids);
    }


    @Override
    public Set<Long> findByUsersId(Long id) {
        return roleMapper.findByUserId(id);
    }

//    @Override
//    public Integer findByRoles(Set<Role> roles) {
//        Set<RoleDTO> roleDTOS = new HashSet<>();
//        //由于走的是自己的方法，所以不会走缓存。但是公司的角色一般是一对一。
//        // 所以不用优化。如果需要做一个用户对应多个角色的时候需要优化让他走缓存；
//        for (Role role : roles) {
//            roleDTOS.add(findById(role.getId()));
//        }
//        return Collections.min(roleDTOS.stream().map(RoleDTO::getLevel).collect(Collectors.toList()));
//    }

    @Override
    @Cacheable(key = "'auth:' + #p0.id")
    public List<GrantedAuthority> mapToGrantedAuthorities(UserDTO user) {
        Set<String> permissions = new HashSet<>();
        // 如果是管理员直接返回
        if (user.getIsAdmin()) {
            permissions.add("admin");
            return permissions.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        Set<Long> roleIds = roleMapper.findByUserId(user.getId());
        permissions = menuMapper.findByRolePermissions(roleIds);
        return permissions.stream().filter(StringUtils::isNotBlank).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public void download(RoleQueryCriteria criteria, HttpServletResponse response) throws IOException {
        List<Role> roles = roleMapper.selectList(QueryHelp.getWrappers(criteria,Role.class));
        List<Map<String, Object>> list = new ArrayList<>();
        for (Role role : roles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("角色名称", role.getName());
            map.put("角色级别", role.getLevel());
            map.put("描述", role.getDescription());
            map.put("创建日期", role.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userMapper.countByRoles(ids) > 0) {
            throw new BadRequestException("所选角色存在用户关联，请解除关联再试！");
        }
    }


    /**
     * 清理缓存
     * @param id /
     */
    public void delCaches(Long id, Set<Long> userIds) {
        userIds = CollectionUtil.isEmpty(userIds) ? userMapper.findByRoleId(id) : userIds;
        if (CollectionUtil.isNotEmpty(userIds)) {
            redisUtils.delByKeys(CacheKey.MENU_USER_MENU, userIds);
            redisUtils.delByKeys(CacheKey.MENU_USER_BUTTON, userIds);
            redisUtils.delByKeys(CacheKey.ROLE_AUTH, userIds);
            redisUtils.del(CacheKey.ROLE_ID + id);
        }
    }
}
