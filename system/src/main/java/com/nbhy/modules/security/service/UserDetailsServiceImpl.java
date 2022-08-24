package com.nbhy.modules.security.service;

import com.nbhy.exception.BadRequestException;
import com.nbhy.exception.EntityNotFoundException;
import com.nbhy.modules.security.config.bean.LoginProperties;
import com.nbhy.modules.security.service.dto.JwtUserDto;
import com.nbhy.modules.system.service.MenuService;
import com.nbhy.modules.system.service.RoleService;
import com.nbhy.modules.system.service.UserService;
import com.nbhy.modules.system.domain.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;


    @Override
    public JwtUserDto loadUserByUsername(String username) {
        JwtUserDto jwtUserDto = null;
        UserDTO user;
        try {
            user = userService.findByName(username);
        } catch (EntityNotFoundException e) {
            // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
            throw new UsernameNotFoundException("", e);
        }


        if (user == null) {
            throw new UsernameNotFoundException("");
        } else {
            if (!user.getEnabled()) {
                throw new BadRequestException("账号未激活");
            }
            jwtUserDto = new JwtUserDto(
                    user,
                    roleService.mapToGrantedAuthorities(user),
                    menuService.findMenuByUser(user.getId(),user.getIsAdmin()),
                    menuService.findButtonByUser(user.getId(),user.getIsAdmin())
                    );
        }
        return jwtUserDto;
    }
}
