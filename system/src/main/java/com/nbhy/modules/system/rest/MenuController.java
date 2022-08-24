//package com.nbhy.modules.system.rest;
//
//import com.nbhy.modules.system.domain.dto.MenuSmallDTO;
//import com.nbhy.modules.system.service.MenuService;
//import com.nbhy.result.CommonResult;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//
//@RestController
//@RequiredArgsConstructor
//@Api(tags = "系统：菜单管理")
//@RequestMapping("/api/menus")
//public class MenuController {
//
//    private final MenuService menuService;
//    private static final String ENTITY_NAME = "菜单";
//
//
//    @GetMapping(value = "/build")
//    @ApiOperation("获取所有需要权限控制的菜单和按钮")
//    @PreAuthorize("@el.check('role')")
//    public CommonResult<List<MenuSmallDTO>> buildMenus(){
//        List<MenuSmallDTO> controlMenu = menuService.findControlMenu();
//        List<MenuSmallDTO> menuDTOS = menuService.buildTree(controlMenu);
//        return  CommonResult.querySuccess(menuDTOS,ENTITY_NAME);
//    }
//
//
//}
