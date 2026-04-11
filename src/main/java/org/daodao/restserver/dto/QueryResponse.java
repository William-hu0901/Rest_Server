package org.daodao.restserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据库查询响应结果")
public class QueryResponse {
    @Schema(description = "查询状态", example = "success", allowableValues = {"success", "fail"})
    private String status;
    
    @Schema(description = "查询结果数据，JSON格式字符串", example = "{\"rows\": [{\"id\": 1, \"name\": \"John\"}]}")
    private String data;
    
    @Schema(description = "错误信息，仅在查询失败时返回", example = "Connection timeout")
    private String error;

}