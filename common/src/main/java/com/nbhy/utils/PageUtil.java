/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nbhy.utils;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbhy.domain.PageBean;
import com.nbhy.domain.PageQuery;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页工具
 * @author Zheng Jie
 * @date 2018-12-10
 */
public class PageUtil extends cn.hutool.core.util.PageUtil {

    public static final List EMPTY_LIST = new ArrayList(0);

    /**
     * List 分页
     */
    public static List toPage(long page, long size , List list) {
        long fromIndex = page * size;
        long toIndex = page * size + size;
        if(fromIndex > list.size()){
            return new ArrayList();
        } else if(toIndex >= list.size()) {
            return list.subList((int)fromIndex,list.size());
        } else {
            return list.subList((int)fromIndex,(int)toIndex);
        }
    }

    /**
     * Page 数据处理，预防redis反序列化报错
     */
    public static Map<String,Object> toPage(Page page) {
        Map<String,Object> map = new LinkedHashMap<>(4);
        map.put("list",page.getRecords());
        map.put("total",page.getTotal());
        map.put("pageIndex",page.getCurrent());
        map.put("pageSize",page.getSize());
        return map;
    }

    /**
     * 自定义分页
     */
    public static Map<String,Object> toPage(Object object, Long totalElements, PageQuery pageable) {
        Map<String,Object> map = new LinkedHashMap<>(4);
        map.put("list",object);
        map.put("total",totalElements);
        if(pageable !=null){
            map.put("pageIndex",pageable.getPageIndex());
            map.put("pageSize",pageable.getPageSize());
        }
        return map;
    }

    /**
     * 自定义分页
     */
    public static <T> PageBean<T> toPageBean(List<T> list, Long totalElements, PageQuery pageable) {
        return PageBean.<T>builder().
                list(list).
                total(totalElements).
                pageIndex(pageable.getPageIndex()).
                pageSize(pageable.getPageSize()).
                build();
    }



    /**
     * 自定义分页
     */
    public static <T> PageBean<T> toPageBean(Page<T> page) {
        return PageBean.<T>builder().
                list(page.getRecords()).
                total(page.getTotal()).
                pageIndex(page.getCurrent()).
                pageSize(page.getSize()).
                build();
    }



    /**
     * 创建一个空数组
     */
    public static <T> PageBean<T> newEmptyPageBean(PageQuery pageQuery) {
        return PageBean.<T>builder().
                list(EMPTY_LIST).
                total(0L).
                pageIndex(pageQuery.getPageIndex()).
                pageSize(pageQuery.getPageSize()).
                build();
    }
}
