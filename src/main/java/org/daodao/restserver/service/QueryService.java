package org.daodao.restserver.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.daodao.restserver.connector.PostgresConnector;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryService {

    @Resource
    private PostgresConnector postgresConnector;

    public String executeQuery(String sql) throws Exception {
        try {
            return postgresConnector.readAsString(sql);
        } catch (Exception e) {
            log.error("Database error occurred: ", e);
            throw new Exception("Database error occurred: " + e.getMessage());
        }
    }

    public String executeQueryWithParams(String sql, Object... params) throws Exception {
        try {
            return postgresConnector.readAsStringWithParams(sql, params);
        } catch (Exception e) {
            log.error("Database error occurred: ", e);
            throw new Exception("Database error occurred: " + e.getMessage());
        }
    }
}
