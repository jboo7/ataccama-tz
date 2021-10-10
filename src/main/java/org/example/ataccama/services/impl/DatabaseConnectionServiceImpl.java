package org.example.ataccama.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.CreateCloseableDatasourceException;
import org.example.ataccama.exceptions.GetConnectionException;
import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.example.ataccama.services.DatabaseConnectionService;
import org.example.ataccama.datasources.CloseableDatasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {
    private final Map<Long, CloseableDatasource<?>> datasourceCache;
    private final CloseableDatasourceFactory closeableDatasourceFactory;

    @Override
    public Connection getConnection(DatabaseConnection databaseConnection) throws GetConnectionException {
        try {
            final var id = databaseConnection.getId();
            var closeableDatasource = datasourceCache.get(id);
            if (closeableDatasource == null) {
                closeableDatasource = closeableDatasourceFactory.createCloseableDatasource(databaseConnection);
                datasourceCache.put(id, closeableDatasource);
            }
            return closeableDatasource.getConnection();
        } catch (SQLException e) {
            throw new GetConnectionException("Failed to get a connection to datasource", e);
        } catch (CreateCloseableDatasourceException e) {
            throw new GetConnectionException("Failed to create a datasource", e);
        }
    }
}
