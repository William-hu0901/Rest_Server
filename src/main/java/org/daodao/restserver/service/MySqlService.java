package org.daodao.restserver.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.daodao.restserver.connector.MySqlConnector;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MySqlService {

    @Resource
    private MySqlConnector mySqlConnector;

    public String executeQuery(String sql) throws Exception {
        try {
            return mySqlConnector.readAsString(sql);
        } catch (Exception e) {
            log.error("MySQL database error occurred: ", e);
            throw new Exception("MySQL database error occurred: " + e.getMessage());
        }
    }

    public String executeQueryWithParams(String sql, Object... params) throws Exception {
        try {
            return mySqlConnector.readAsStringWithParams(sql, params);
        } catch (Exception e) {
            log.error("MySQL database error occurred: ", e);
            throw new Exception("MySQL database error occurred: " + e.getMessage());
        }
    }
}
