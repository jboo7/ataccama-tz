package org.example.ataccama.datasources;

import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.CreateCloseableDatasourceException;

public interface CloseableDatasourceFactory {
    CloseableDatasource<?> createCloseableDatasource(DatabaseConnection databaseConnection) throws CreateCloseableDatasourceException;
}
