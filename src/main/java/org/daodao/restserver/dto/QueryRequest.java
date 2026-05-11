package org.daodao.restserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据库查询请求参数")
public class QueryRequest {

    @Schema(description = "SQL查询语句", example = "SELECT * FROM users", required = true)
    private String sql;

}