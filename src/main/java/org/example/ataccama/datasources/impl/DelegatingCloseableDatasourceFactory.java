package org.example.ataccama.datasources.impl;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.data.DatabaseType;
import org.example.ataccama.datasources.CloseableDatasource;
import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.example.ataccama.exceptions.CreateCloseableDatasourceException;

import java.util.Map;

@RequiredArgsConstructor
public class DelegatingCloseableDatasourceFactory implements CloseableDatasourceFactory {
    private final Map<DatabaseType, CloseableDatasourceFactory> closeableDatasourceFactoryMap;

    @Override
    public CloseableDatasource<?> createCloseableDatasource(DatabaseConnection databaseConnection) throws CreateCloseableDatasourceException {
        final var factory = closeableDatasourceFactoryMap.get(databaseConnection.getType());
        if (factory != null) {
            return factory.createCloseableDatasource(databaseConnection);
        }
        throw new CreateCloseableDatasourceException("No factory defined for: " + databaseConnection.getType());
    }
}
