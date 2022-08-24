package com.nbhy.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解防止表单重复提交
 *
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit
{
    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    public int interval() default 5000;

    /**
     * 提示消息
     */
    public String message() default "不允许重复提交，请稍后再试";

    /**
     * 参数的Key，多个key以,为分割,当类型为IP的时候，paramKey无效
     * 当类型为HEAD的时候 比如 paramKey = "Authorization,token" 的时候就回去请求头查找这个两个值，作为redisKey
     * 当类型为PARAM的时候 比如 paramKey = "user.name,createTime" 的时候就回去请求参数查找这个两个值，作为redisKey
     * @return
     */
    public String paramKey() default "";



    public KeyType keyType() default KeyType.IP;

    /**
     * 校验参数从那里获取
     */
    enum KeyType {
        //头
        HEAD,
        //参数
        PARAM,
        //ip地址
        IP
    }



}
