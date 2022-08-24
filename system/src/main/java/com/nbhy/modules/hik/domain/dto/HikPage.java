package com.nbhy.modules.hik.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HikPage<T> {
    private List<T> list;

    private Integer totalPage;
}
