package org.daodao.restserver.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;

@Slf4j
@Component
public class MySqlConnector {

    private final DataSource dataSource;

    public MySqlConnector(@Qualifier("mysqlDataSource") DataSource mysqlDataSource) {
        this.dataSource = mysqlDataSource;
    }

    public String readAsString(String query) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            log.info("Executed MySQL READ query: {}", query);
            return parseResultsetWithJackson(resultSet);
        } finally {
            closeQuietly(resultSet, statement, connection);
        }
    }

    public String readAsStringWithParams(String query, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            log.info("Executed MySQL READ query with params: {} | params: {}", query, Arrays.toString(params));
            return parseResultsetWithJackson(resultSet);
        } finally {
            closeQuietly(resultSet, preparedStatement, connection);
        }
    }

    private String parseResultsetWithJackson(ResultSet resultSet) throws SQLException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonResult = mapper.createObjectNode();
            ArrayNode rows = mapper.createArrayNode();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                ObjectNode row = mapper.createObjectNode();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    if (value == null) {
                        row.putNull(columnName);
                    } else {
                        row.putPOJO(columnName, value);
                    }
                }
                rows.add(row);
            }
            jsonResult.set("rows", rows);
            return mapper.writeValueAsString(jsonResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse ResultSet with Jackson", e);
        }
    }

    private void closeQuietly(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("Error closing ResultSet", e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("Error closing Statement", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Error closing Connection", e);
            }
        }
    }

    /**
     * Execute UPDATE/INSERT/DELETE statement
     */
    public String executeUpdate(String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(sql);
            log.info("Executed MySQL UPDATE statement: {}, rows affected: {}", sql, rowsAffected);
            return "{\"rowsAffected\": " + rowsAffected + "}";
        } finally {
            closeQuietly(null, statement, connection);
        }
    }
}
