package org.example.ataccama.datasources.impl;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.datasources.CloseableDatasource;
import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.springframework.stereotype.Component;

@Component
public class MySQLCloseableDatasourceFactory implements CloseableDatasourceFactory {
    @Override
    public CloseableDatasource<?> createCloseableDatasource(DatabaseConnection databaseConnection) {
        final var mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setServerName(databaseConnection.getHost());
        mysqlDataSource.setPortNumber(databaseConnection.getPort());
        mysqlDataSource.setDatabaseName(databaseConnection.getDatabase());
        if (databaseConnection.getUsername() != null) {
            mysqlDataSource.setUser(databaseConnection.getUsername());
            mysqlDataSource.setPassword(databaseConnection.getPassword());
        }
        return new CloseableDatasource<>(mysqlDataSource, null);
    }
}
