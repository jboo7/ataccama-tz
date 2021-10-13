package org.example.ataccama.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.data.DatabaseType;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.data.ColumnsMetadata;
import org.example.ataccama.services.data.ColumnsStatistic;
import org.example.ataccama.services.data.SchemasMetadata;
import org.example.ataccama.services.data.TableDataPreview;
import org.example.ataccama.services.data.TablesMetadata;
import org.example.ataccama.services.data.TablesStatistic;

import java.util.Map;

@RequiredArgsConstructor
public final class DelegatingDatabaseConnectionBrowseService implements DatabaseBrowserService {
    private final Map<DatabaseType, DatabaseBrowserService> databaseBrowserServiceMap;

    @Override
    public SchemasMetadata getSchemasMetadata(DatabaseConnection databaseConnection) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getSchemasMetadata(databaseConnection);
    }

    @Override
    public TablesMetadata getTablesMetadata(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getTablesMetadata(databaseConnection, schema);
    }

    @Override
    public ColumnsMetadata getColumnsMetadata(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getColumnsMetadata(databaseConnection, schema, table);
    }

    @Override
    public TableDataPreview getTableDataPreview(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getTableDataPreview(databaseConnection, schema, table);
    }

    @Override
    public TablesStatistic getTablesStatistic(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getTablesStatistic(databaseConnection, schema);
    }

    @Override
    public ColumnsStatistic getColumnsStatistic(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        return getTargetService(databaseConnection.getType()).getColumnsStatistic(databaseConnection, schema, table);
    }

    private DatabaseBrowserService getTargetService(DatabaseType databaseType) throws DatabaseBrowseException {
        final var targetService = databaseBrowserServiceMap.get(databaseType);
        if (targetService == null) {
            throw new DatabaseBrowseException("No service found for: " + databaseType);
        }
        return targetService;
    }
}
