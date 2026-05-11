package org.daodao.restserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.daodao.restserver.dto.QueryRequest;
import org.daodao.restserver.dto.QueryResponse;
import org.daodao.restserver.service.MySqlService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mysql")
@Slf4j
@Tag(name = "MySQL Controller", description = "MySQL database query API")
public class MySqlController {

    @Resource
    private MySqlService mySqlService;

    @PostMapping("/query")
    @Operation(summary = "Query MySQL Database", description = "Execute SQL SELECT query on MySQL database and return results")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QueryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public QueryResponse queryData(
            @Parameter(description = "Query request parameters, including SQL statement", required = true)
            @RequestBody QueryRequest request) {
        log.info("Querying data from MySQL database.");
        try {
            String data = mySqlService.executeQuery(request.getSql());
            log.debug("Query data: {}", data);
            return new QueryResponse("success", data, null);
        } catch (Exception e) {
            log.error("Application error: ", e);
            return new QueryResponse("fail", null, e.getMessage());
        }
    }

    @GetMapping("/test")
    @Operation(summary = "MySQL Connection Test", description = "Test MySQL database connection")
    @SecurityRequirement(name = "bearerAuth")
    public QueryResponse testConnection() {
        log.info("Testing MySQL database connection.");
        try {
            String data = mySqlService.executeQuery("SELECT 1 AS test");
            return new QueryResponse("success", data, null);
        } catch (Exception e) {
            log.error("MySQL connection test failed: ", e);
            return new QueryResponse("fail", null, e.getMessage());
        }
    }
}
