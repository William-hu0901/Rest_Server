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
@Schema(description = "Database query response result")
public class QueryResponse {
    @Schema(description = "Query status", example = "success", allowableValues = {"success", "fail"})
    private String status;
    
    @Schema(description = "Query result data in JSON format string", example = "{\"rows\": [{\"id\": 1, \"name\": \"John\"}]}")
    private String data;
    
    @Schema(description = "Error message, returned only when query fails", example = "Connection timeout")
    private String error;

}