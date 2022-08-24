package com.nbhy.modules.hik.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbhy.modules.hik.domain.entity.HikPersonAuth;
import com.nbhy.modules.hik.mapper.HikPersonAuthMapper;
import com.nbhy.modules.hik.service.HikPersonAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonServiceImpl
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikPersonAuthServiceImpl extends ServiceImpl<HikPersonAuthMapper, HikPersonAuth> implements HikPersonAuthService {

}

