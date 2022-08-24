package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

import java.util.List;

@Data
public class Params<T>{
    //事件分类	可以使用这个来判断是否是自己需要的回调
    private String ability;

    //事件消息
    private List<Events<T>> events;
    //发送事件
    private String sendTime;
}