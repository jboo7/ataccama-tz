package org.example.ataccama.datasources;

import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;
import org.example.ataccama.data.DatabaseConnection;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostgresCloseableDatasourceFactory implements CloseableDatasourceFactory {
    @Override
    public CloseableDatasource<?> createCloseableDatasource(DatabaseConnection databaseConnection) {
        final var pgSimpleDatasource = new PGSimpleDataSource();
        pgSimpleDatasource.setServerNames(ArrayUtils.toArray(databaseConnection.getHost()));
        pgSimpleDatasource.setPortNumbers(Ints.toArray(List.of(databaseConnection.getPort())));
        pgSimpleDatasource.setDatabaseName(databaseConnection.getDatabase());
        if (databaseConnection.getUsername() != null) {
            pgSimpleDatasource.setUser(databaseConnection.getUsername());
            pgSimpleDatasource.setPassword(databaseConnection.getPassword());
        }
        return new CloseableDatasource<>(new PGSimpleDataSource(), null);
    }
}
