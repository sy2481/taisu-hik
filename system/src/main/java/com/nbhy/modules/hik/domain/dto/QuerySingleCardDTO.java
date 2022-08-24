package com.nbhy.modules.hik.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuerySingleCardDTO {
    private String cardNo;
    private String cardId;
}
