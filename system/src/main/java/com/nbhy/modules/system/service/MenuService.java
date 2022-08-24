package com.nbhy.modules.system.service;

import com.nbhy.modules.system.domain.dto.MenuSmallDTO;

import java.util.List;
import java.util.Set;


public interface MenuService {
    /**
     * 构建菜单树
     * @param menuDTOS 原始数据
     * @return /
     */
    List<MenuSmallDTO> buildTree(List<MenuSmallDTO> menuDTOS);


    /**
     * 根据当前用户获取按钮
     * @param currentUserId /
     * @return /
     */
    Set<String> findMenuByUser(Long currentUserId, boolean isAdmin);


    /**
     * 根据当前用户获取菜单
     * @param currentUserId /
     * @return /
     */
    Set<String> findButtonByUser(Long currentUserId,boolean isAdmin);

    /**
     *获取需要控制的菜单或者按钮
     * @return
     */
    List<MenuSmallDTO> findControlMenu();

}
