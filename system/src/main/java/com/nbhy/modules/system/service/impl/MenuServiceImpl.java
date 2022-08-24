package com.nbhy.modules.system.service.impl;

import com.nbhy.modules.system.constant.MenuConstant;
import com.nbhy.modules.system.mapper.MenuMapper;
import com.nbhy.modules.system.service.MenuService;
import com.nbhy.modules.system.service.RoleService;
import com.nbhy.modules.system.domain.dto.MenuSmallDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "menu")
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final RoleService roleService;



    @Override
    public List<MenuSmallDTO> buildTree(List<MenuSmallDTO> menuDTOS) {
        Map<Long, List<MenuSmallDTO>> menuDtosGroupByPid = menuDTOS.stream().collect(Collectors.groupingBy(MenuSmallDTO::getPid));
        menuDTOS.stream().forEach(menuDTO -> menuDTO.setChildren(menuDtosGroupByPid.get(menuDTO.getId())));
        return menuDTOS.stream().filter(menuDTO -> menuDTO.getPid().equals(Long.valueOf(0))).collect(Collectors.toList());
    }

    /**
     * 用户角色改变时需清理缓存
     * @param currentUserId /
     * @return /
     */
    @Override
    @Cacheable(key = "'user_menu:' + #p0")
    public Set<String> findMenuByUser(Long currentUserId,boolean isAdmin) {
        Set<String> menus = null;
        if(isAdmin){
            menus = menuMapper.queryAllResources(MenuConstant.MENU);
        }else{
            Set<Long> roleIds = getRoleSmallDTOList(currentUserId);
            menus = menuMapper.findByRoleIdsAndIsMenu(roleIds, MenuConstant.MENU);
        }
        return menus;
    }


    /**
     * 获取该用户所有的角色Id
     * @param currentUerId
     * @return
     */
    private Set<Long> getRoleSmallDTOList(Long currentUerId){
        Set<Long> roleIds = roleService.findByUsersId(currentUerId);
        return roleIds;
    }


    /**
     * 用户角色改变时需清理缓存
     * @param currentUserId /
     * @return /
     */
    @Override
    @Cacheable(key = "'user_button:' + #p0")
    public Set<String> findButtonByUser(Long currentUserId,boolean isAdmin) {
        Set<String> buttons = null;
        if(isAdmin){
            buttons = menuMapper.queryAllResources(MenuConstant.BUTTON);
        }else{
            Set<Long> roleIds = getRoleSmallDTOList(currentUserId);
            buttons = menuMapper.findByRoleIdsAndIsMenu(roleIds, MenuConstant.BUTTON);
        }
        return buttons;
    }



    @Override
    public List<MenuSmallDTO> findControlMenu() {
        return menuMapper.findControlMenu();
    }


}
