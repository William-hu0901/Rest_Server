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
import org.daodao.restserver.connector.PostgresConnector;
import org.daodao.restserver.dto.QueryRequest;
import org.daodao.restserver.dto.QueryResponse;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "API Controller", description = "Provides database query and basic API services")
public class ApiController {

    @Resource
    private PostgresConnector postgresConnector;

    @GetMapping("/hello")
    @Operation(summary = "Health Check Endpoint", description = "Returns Hello World for testing if API is running normally")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned greeting message",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)))
    })
    public String sayHello() {
        return "Hello, World!";
    }

    @PostMapping("/queryData")
    @Operation(summary = "Query Database", description = "Execute SQL query and return results")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QueryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public QueryResponse queryData(
            @Parameter(description = "Query request parameters, including username, password and SQL statement", required = true)
            @RequestBody QueryRequest request) {
        log.info("Querying data from PostgreSQL database.");
        try {
            ResultSet resultSet = actionOnPostgres(request);
            String data = "";
            if (resultSet != null) {
                data = parseResultset(resultSet);
                log.debug("Query data: {}", data);
            }
            return new QueryResponse("success",  data, null);

        } catch (Exception e) {
            log.error("Application error: ", e);
            return new QueryResponse("fail",  null, e.getMessage());
        }
    }

    public ResultSet actionOnPostgres(QueryRequest request) {
        try {
            log.info("Executing query using Spring Boot datasource.");
            ResultSet resultSet = postgresConnector.read(request.getSql());
            return resultSet;

        } catch (SQLException e) {
            log.error("Database error occurred: ", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred: ", e);
        }

        return null;
    }

    private String parseResultset(ResultSet resultSet) throws SQLException {
        JSONObject jsonResult = new JSONObject();
        org.json.JSONArray rows = new org.json.JSONArray();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            JSONObject row = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                int columnType = metaData.getColumnType(i);

                if (columnType == Types.VARCHAR || columnType == Types.CHAR) {
                    row.put(columnName, resultSet.getString(i));
                } else if (columnType == Types.INTEGER) {
                    row.put(columnName, resultSet.getInt(i));
                }
            }
            rows.put(row);
        }
        jsonResult.put("rows", rows);
        return jsonResult.toString();
    }

}