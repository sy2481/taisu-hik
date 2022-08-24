package com.nbhy.modules.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbhy.domain.PageBean;
import com.nbhy.domain.PageQuery;
import com.nbhy.exception.BadRequestException;
import com.nbhy.exception.EntityExistException;
import com.nbhy.exception.EntityNotFoundException;
import com.nbhy.modules.security.service.OnlineUserService;
import com.nbhy.modules.system.domain.entity.Role;
import com.nbhy.modules.system.domain.entity.User;
import com.nbhy.modules.system.domain.entity.UserRoles;
import com.nbhy.modules.system.mapper.RoleMapper;
import com.nbhy.modules.system.mapper.UserMapper;
import com.nbhy.modules.system.mapper.UserRolesMapper;
import com.nbhy.modules.system.service.UserService;
import com.nbhy.modules.system.domain.dto.RoleSmallDTO;
import com.nbhy.modules.system.domain.dto.UserDTO;
import com.nbhy.modules.system.domain.query.UserQueryCriteria;
import com.nbhy.modules.system.service.mapstruct.UserConverter;
import com.nbhy.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private final UserMapper userMapper;
    private final UserRolesMapper userRolesMapper;
    private final UserConverter userConverter;
    private final RedisUtils redisUtils;
    private final OnlineUserService onlineUserService;
    private final RoleMapper roleMapper;

    @Lazy
    @Autowired
    private  UserService userService;


    @Override
    public PageBean<UserDTO> queryAll(UserQueryCriteria criteria, PageQuery pageable) {
        Page<User> page = userMapper.selectPage(QueryHelp.getPage(pageable), QueryHelp.getWrappers(criteria,User.class));
        List<UserDTO> userDTOS = page.getRecords().stream().map(user -> {
            UserDTO userDTO = userConverter.toDto(user);
            userDTO.setRoles(roleMapper.findRoleSmallByUserId(userDTO.getId()));
            return userDTO;
        }).collect(Collectors.toList());
        return PageUtil.toPageBean(userDTOS,page.getTotal(),pageable);
    }

    @Override
    public List<UserDTO> queryAll(UserQueryCriteria criteria) {
        List<User> users = userMapper.selectList(QueryHelp.getWrappers(criteria,User.class));
        return users.stream().map(user -> {
            UserDTO userDTO = userConverter.toDto(user);
            userDTO.setRoles(roleMapper.findRoleSmallByUserId(userDTO.getId()));
            return userDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public UserDTO findById(long id) {
        User user = userMapper.selectById(id);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        UserDTO userDTO = userConverter.toDto(user);
        userDTO.setRoles(roleMapper.findRoleSmallByUserId(userDTO.getId()));
        return userDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(User resources) {
        if (userMapper.selectOne(Wrappers.<User>lambdaQuery().select(User::getId).eq(User::getUsername,resources.getUsername())) != null) {
            throw new BadRequestException("登录名已存在");
        }
        if (userMapper.selectOne(Wrappers.<User>lambdaQuery().select(User::getId).eq(User::getEmail,resources.getEmail())) != null) {
            throw new BadRequestException("邮箱已存在");
        }
        userMapper.insert(resources);
        List<UserRoles> userRoleList = resources.getRoles().stream().map(role -> {
            UserRoles userRoles = new UserRoles();
            userRoles.setUserId(resources.getId());
            userRoles.setRoleId(role.getId());
            return userRoles;
        }).collect(Collectors.toList());
        userRolesMapper.insertBatch(userRoleList);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User resources) {
        //1、校验用户
        User user = userMapper.selectById(resources.getId());
        ValidationUtil.isNull(user, "User", "id", resources.getId());
        User user1 = userMapper.selectOne(Wrappers.<User>lambdaQuery().select(User::getId).eq(User::getUsername,resources.getUsername()));
        User user2 = userMapper.selectOne(Wrappers.<User>lambdaQuery().select(User::getId).eq(User::getEmail,resources.getEmail()));

        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new BadRequestException("登录名已存在");
        }

        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new BadRequestException("邮箱已存在");
        }

        //2、检查角色

        Set<Long> resourcesRoleIds = resources.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
        Set<Long> roleIdsDAO = roleMapper.findByUserId(user.getId());


        // 如果用户的角色改变
        if (!resourcesRoleIds.equals(roleIdsDAO)) {
            //删除用户旧的角色
            userRolesMapper.delete(Wrappers.<UserRoles>lambdaUpdate().eq(UserRoles::getUserId,user.getId()));
            //保存新的角色
            userRolesMapper.insertBatch(resourcesRoleIds.stream().map(roleId->{
                UserRoles userRoles = new UserRoles();
                userRoles.setRoleId(roleId);
                userRoles.setUserId(user.getId());
                return userRoles;
            }).collect(Collectors.toList()));
            redisUtils.del(CacheKey.MENU_USER_MENU + resources.getId());
            redisUtils.del(CacheKey.MENU_USER_BUTTON + resources.getId());
            redisUtils.del(CacheKey.ROLE_AUTH + resources.getId());
        }

        // 如果用户名称修改
        if(!resources.getUsername().equals(user.getUsername()) || resources.getEnabled() == false){
            delCaches(resources.getId(),resources.getUsername());
            onlineUserService.checkLoginOnUser(user.getUsername(),null);
        }

        //3、更新用户信息
        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setGender(resources.getGender());
        userMapper.updateById(user);
        // 清除缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCenter(User resources) {
        User user = userMapper.selectById(resources.getId());
        ValidationUtil.isNull(user, "User", "id", resources.getId());
        user.setNickName(resources.getNickName());
        user.setPhone(resources.getPhone());
        user.setGender(resources.getGender());
        userMapper.updateById(user);
        // 清理缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            // 清理缓存
            UserDTO user = userService.findById(id);
            if(user == null){
                throw new BadRequestException("用户不存在,无法删除");
            }
            if(user.getId().equals(SecurityUtils.getCurrentUserId())){
                throw new BadRequestException("您不能删除自身");
            }
            if(user.getIsAdmin()){
                throw new BadRequestException(user.getUsername()+"是超级管理员，无法删除。");
            }
            delCaches(user.getId(), user.getUsername());
        }
        userRolesMapper.delete(Wrappers.<UserRoles>lambdaUpdate().in(UserRoles::getUserId,ids));
        userMapper.deleteBatchIds(ids);
    }

    @Override
    @Cacheable(key = "'username:' + #p0")
    public UserDTO findByName(String userName) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername,userName));
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {

            //预防mysql不区分大小写查询
            if(!userName.equals(user.getUsername())){
                return null;
            }

            UserDTO userDTO = userConverter.toDto(user);
            userDTO.setRoles(roleMapper.findRoleSmallByUserId(userDTO.getId()));
            return userDTO;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(Long userId,String pass, String username) {
        userMapper.update(null,Wrappers.<User>lambdaUpdate().
                eq(User::getId, userId)
                .set(User::getPassword, pass).
                        set(User::getPwdResetTime, new Timestamp(System.currentTimeMillis())));
        redisUtils.del("user::username:" + username);
    }


    @Override
    public void download(List<UserDTO> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDTO userDTO : queryAll) {
            List<String> roles = userDTO.getRoles().stream().map(RoleSmallDTO::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDTO.getUsername());
            map.put("角色", roles);
            map.put("邮箱", userDTO.getEmail());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDTO.getPhone());
            map.put("修改密码的时间", userDTO.getPwdResetTime());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 清理缓存
     * @param id /
     */
    public void delCaches(Long id, String username) {
        redisUtils.del(CacheKey.USER_ID + id);
        redisUtils.del(CacheKey.USER_NAME + username);
    }

}
