package com.nbhy.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.modules.system.domain.entity.SysConfig;
import com.nbhy.modules.system.mapper.SysConfigMapper;
import com.nbhy.modules.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:25 2022/3/17
 * @ClassName: SysConfigServiceIpml
 * @Description: 系统配置
 * @Version: 1.0
 */
@Service
@CacheConfig(cacheNames = "c")
@RequiredArgsConstructor
public class SysConfigServiceIpml implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    @Cacheable
    public SysConfig getConfig() {
        List<SysConfig> sysConfigs = sysConfigMapper.selectList(null);
        return sysConfigs.get(0);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void setLocationCardEnabled(Boolean locationCardEnabled) {
        SysConfig cofnig = getConfig();
        cofnig.setLocationCardEnabled(locationCardEnabled);
        sysConfigMapper.updateById(cofnig);
    }
}
