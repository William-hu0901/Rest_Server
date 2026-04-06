package org.daodao.restserver.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Slf4j
@Component
public class PostgresConnector {
    private final DataSource dataSource;

    public PostgresConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ResultSet read(String query) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        log.info("Executed READ query: {}", query);
        return resultSet;
    }
}