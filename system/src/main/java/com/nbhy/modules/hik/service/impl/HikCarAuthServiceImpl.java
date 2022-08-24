package com.nbhy.modules.hik.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.constant.CarAuthConstant;
import com.nbhy.modules.hik.constant.HikDeviceConstant;
import com.nbhy.modules.hik.domain.entity.Car;
import com.nbhy.modules.hik.domain.entity.HikCarAuth;
import com.nbhy.modules.hik.domain.vo.CarVO;
import com.nbhy.modules.hik.mapper.HikCarAuthMapper;
import com.nbhy.modules.hik.mapper.HikCarMapper;
import com.nbhy.modules.hik.mapper.HikPersonMapper;
import com.nbhy.modules.hik.service.HikCarAuthService;
import com.nbhy.modules.hik.service.HikCarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonServiceImpl
 * @Description: 海康车辆
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikCarAuthServiceImpl extends ServiceImpl<HikCarAuthMapper,HikCarAuth> implements HikCarAuthService {

}

