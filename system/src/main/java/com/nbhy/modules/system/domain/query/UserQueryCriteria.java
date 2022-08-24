package com.nbhy.modules.system.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbhy.annotation.Query;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserQueryCriteria implements Serializable {

    @Query
    private Long id;


    @Query(type = Query.Type.INNER_LIKE)
    private String username;

    @Query(type = Query.Type.INNER_LIKE)
    private String nickName;


    @Query(type = Query.Type.INNER_LIKE)
    private String email;

    @Query
    private Boolean enabled;

    @Query(type = Query.Type.BETWEEN)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<Date> createTime;

    @Query(type = Query.Type.SORT_DESC,propName = "id")
    private Boolean idDesc;
}
