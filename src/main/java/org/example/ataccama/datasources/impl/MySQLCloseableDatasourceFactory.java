package org.example.ataccama.datasources.impl;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.datasources.CloseableDatasource;
import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.example.ataccama.exceptions.CreateCloseableDatasourceException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.TimeZone;

@Component
public class MySQLCloseableDatasourceFactory implements CloseableDatasourceFactory {
    @Override
    public CloseableDatasource<?> createCloseableDatasource(DatabaseConnection databaseConnection) throws CreateCloseableDatasourceException {
        try {
            final var mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setServerName(databaseConnection.getHost());
            mysqlDataSource.setPortNumber(databaseConnection.getPort());
            mysqlDataSource.setDatabaseName(databaseConnection.getDatabase());
            mysqlDataSource.setServerTimezone(TimeZone.getDefault().getID());
            if (databaseConnection.getUsername() != null) {
                mysqlDataSource.setUser(databaseConnection.getUsername());
                mysqlDataSource.setPassword(databaseConnection.getPassword());
            }
            return new CloseableDatasource<>(mysqlDataSource, null);
        } catch (SQLException e) {
            throw new CreateCloseableDatasourceException("Failed to create datasoure",e);
        }
    }
}
