package org.example.ataccama.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.data.DatabaseType;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.data.Column;
import org.example.ataccama.services.data.Row;
import org.example.ataccama.services.data.Schema;
import org.example.ataccama.services.data.Table;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DelegatingDatabaseConnectionBrowseService implements DatabaseBrowserService {
    private final Map<DatabaseType, DatabaseBrowserService> databaseBrowserServiceMap;

    @Override
    public List<Schema> getSchemas(DatabaseConnection databaseConnection) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getSchemas(databaseConnection);
    }

    @Override
    public List<Table> getTables(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getTables(databaseConnection, schema);
    }

    @Override
    public List<Column> getColumns(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getColumns(databaseConnection, schema, table);
    }

    @Override
    public List<Row> getRows(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getRows(databaseConnection, schema, table);
    }

    private DatabaseBrowserService getTargetService(DatabaseType databaseType) throws DatabaseBrowseException {
        final var targetService = databaseBrowserServiceMap.get(databaseType);
        if (targetService == null) {
            throw new DatabaseBrowseException("No service found for: " + databaseType);
        }
        return targetService;
    }
}
