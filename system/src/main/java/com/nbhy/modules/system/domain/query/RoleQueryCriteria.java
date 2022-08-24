package com.nbhy.modules.system.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbhy.annotation.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class RoleQueryCriteria {

    @Query(blurry = "name,description")
    private String blurry;



    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    /**
     * 为了swagger好看。统一改成这个模式，使用@DateTimeFormat注解来控制时间戳的格式。
     */
    @Query(type = Query.Type.BETWEEN)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<Timestamp> createTime;

    @Query
    private Integer level;

    @Query(type = Query.Type.SORT_ASC,propName = "level")
    private Boolean levelSort;

    @Query(type = Query.Type.SORT_DESC,propName = "id")
    private Boolean id;

}
