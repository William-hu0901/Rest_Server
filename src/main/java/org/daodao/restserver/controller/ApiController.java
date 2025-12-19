package org.daodao.restserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.daodao.restserver.connector.PostgresConnector;
import org.daodao.restserver.dbconfig.DatabaseConfig;
import org.daodao.restserver.dto.QueryRequest;
import org.daodao.restserver.dto.QueryResponse;
import org.daodao.restserver.exceptions.PropertyException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @PostMapping("/queryData")
    public QueryResponse queryData(@RequestBody QueryRequest request) {
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
        PostgresConnector postgresConnector = null;
        try {
            // Load configuration from application.properties
            DatabaseConfig config = new DatabaseConfig();

            // Create PostgreSQL connector using configuration
            postgresConnector = new PostgresConnector(
                    config.getPostgresHost(),
                    config.getPostgresPort(),
                    config.getPostgresDatabase(),
                    request.getUsername(),
                    request.getPassword()
            );

            postgresConnector.connect();
            log.info("Successfully connected to PostgreSQL database.");

            // Execute query and return result
            ResultSet resultSet = postgresConnector.read(request.getSql());
            return resultSet;

        } catch (SQLException e) {
            log.error("Database error occurred: ", e);
        } catch (PropertyException e) {
            log.error("Configuration error occurred: ", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred: ", e);
        } finally {
            if (postgresConnector != null) postgresConnector.disconnect();
        }

        return null;
    }

    private String parseResultset(ResultSet resultSet) throws SQLException {
        JSONObject jsonResult = new JSONObject();
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
            jsonResult.append("rows", row);
        }
        return jsonResult.toString();
    }

}