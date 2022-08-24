package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

import java.util.List;

/**
 * @Author: yyf
 * @Date: Created in 14:07 2022/1/21
 * @ClassName: 注册事件回调实体
 * @Description:
 */
@Data
public class EventRcvVO {
    private String backUrl;
    private List<Integer> eventTypes;
}
