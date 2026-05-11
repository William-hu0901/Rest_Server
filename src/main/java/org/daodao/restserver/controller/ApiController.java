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
import org.daodao.restserver.service.QueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "API Controller", description = "Provides database query and basic API services")
public class ApiController {

    @Resource
    private QueryService queryService;

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
            String data = queryService.executeQuery(request.getSql());
            log.debug("Query data: {}", data);
            return new QueryResponse("success", data, null);

        } catch (Exception e) {
            log.error("Application error: ", e);
            return new QueryResponse("fail", null, e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Get User By Username", description = "Retrieve user information based on username")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QueryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public QueryResponse getUserByUsername(
            @Parameter(description = "Username to search for", required = true)
            @PathVariable String username) {
        log.info("Fetching user by username: {}", username);
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            String data = queryService.executeQueryWithParams(sql, username);
            log.debug("User data: {}", data);
            if (data.isEmpty() || data.equals("{\"rows\":[]}")) {
                return new QueryResponse("fail", null, "User not found");
            }
            return new QueryResponse("success", data, null);
        } catch (Exception e) {
            log.error("Application error: ", e);
            return new QueryResponse("fail", null, e.getMessage());
        }
    }

}
