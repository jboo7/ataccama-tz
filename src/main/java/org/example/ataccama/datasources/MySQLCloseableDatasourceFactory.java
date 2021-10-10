package org.example.ataccama.datasources;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.example.ataccama.data.DatabaseConnection;
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
