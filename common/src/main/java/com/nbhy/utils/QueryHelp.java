package com.nbhy.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbhy.annotation.Query;
import com.nbhy.domain.PageQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
@SuppressWarnings({"unchecked","all"})
public class QueryHelp{

    public  static <Q,T> QueryWrapper<T>  getWrappers(Q query,Class<T> clazz){
        return getWrappers(query, Wrappers.<T>query());
    }


    public  static <Q,T> QueryWrapper<T>  getWrappers(Q query,QueryWrapper<T> queryWrapper) {
        if(queryWrapper == null){
            queryWrapper = Wrappers.query();
        }
        if(query==null){
            return queryWrapper;
        }
        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                // 设置对象的访问权限，保证对private的属性的访
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String propName = q.propName();
                    String joinName = q.joinName();
                    String blurry = q.blurry();
                    String attributeName = isBlank(propName) ? StringUtils.toUnderScoreCase(field.getName()) : propName;
                    Class<?> fieldType = field.getType();
                    Object val = field.get(query);
                    if (ObjectUtil.isNull(val) || "".equals(val)) {
                        continue;
                    }else if(fieldType.equals(String.class)){
                        val = ((String)val).trim();
                        if(ObjectUtil.isNull(val) || "".equals(val)){
                            continue;
                        }
                    }
                    // 模糊多字段
                    if (ObjectUtil.isNotEmpty(blurry)) {
                        String[] blurrys = blurry.split(",");
                        Object finalVal = val;
                        queryWrapper.and(blurrys.length > 0 ? true : false, wq -> {
                            for (int i = 0; i < blurrys.length; i++) {
                                wq.like(blurrys[i], finalVal);
                                if (i != blurrys.length - 1) {
                                    wq.or();
                                }
                            }
                        });
//                    // 模糊多字段
//                    if (ObjectUtil.isNotEmpty(blurry)) {
//                        String[] blurrys = blurry.split(",");
//                            for (int i = 0; i < blurrys.length; i++) {
//                                queryWrapper.like(blurrys[i],val);
//                                if(i != blurrys.length -1){
//                                    queryWrapper.or();
//                                }
//                            }
                        continue;
                    }
                    switch (q.type()) {
                        case EQUAL:
                            queryWrapper.eq(attributeName,val);
                            break;
                        case GREATER_THAN:
                            queryWrapper.ge(attributeName,val);
                            break;
                        case LESS_THAN:
                            queryWrapper.le(attributeName,val);
                            break;
                        case LESS_THAN_NQ:
                            queryWrapper.lt(attributeName,val);
                            break;
                        case INNER_LIKE:
                            queryWrapper.like(attributeName,val);
                            break;
                        case LEFT_LIKE:
                            queryWrapper.likeLeft(attributeName,val);
                            break;
                        case RIGHT_LIKE:
                            queryWrapper.likeRight(attributeName,val);
                            break;
                        case IN:
                            if(val instanceof Collection){
                                if(CollectionUtil.isEmpty((Collection)val)){
                                    break;
                                }
                                queryWrapper.in(attributeName,(Collection)val);
                            }else{
                                log.error("in只能使用Collection接收");
                            }
                            break;
                        case NOT_EQUAL:
                            queryWrapper.ne(attributeName,val);
                            break;
                        case NOT_NULL:
                            queryWrapper.isNotNull(attributeName);
                            break;
                        case IS_NULL:
                            queryWrapper.isNull(attributeName);
                            break;
                        case BETWEEN:
                            List<Object> between = new ArrayList<>((List<Object>)val);
                            if(CollectionUtil.isEmpty(between)){
                                break;
                            }
                            queryWrapper.between(attributeName, between.get(0), between.get(1));
                            break;
                        case SORT_ASC:
                            queryWrapper.orderByAsc(attributeName);
                            break;
                        case SORT_DESC:
                            queryWrapper.orderByDesc(attributeName);
                            break;
                        default: break;
                    }
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return queryWrapper;
    }



    public  static <Q> Page getPage(PageQuery pageQuery) {
       return new Page(pageQuery.getPageIndex(),pageQuery.getPageSize());
    }

    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}
