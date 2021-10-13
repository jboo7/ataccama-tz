package org.example.ataccama.services;

import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.services.data.ColumnsMetadata;
import org.example.ataccama.services.data.ColumnsStatistic;
import org.example.ataccama.services.data.SchemasMetadata;
import org.example.ataccama.services.data.TableDataPreview;
import org.example.ataccama.services.data.TablesMetadata;
import org.example.ataccama.services.data.TablesStatistic;

public interface DatabaseBrowserService {
    SchemasMetadata getSchemasMetadata(DatabaseConnection databaseConnection) throws DatabaseBrowseException;

    TablesMetadata getTablesMetadata(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException;

    ColumnsMetadata getColumnsMetadata(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException;

    TableDataPreview getTableDataPreview(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException;

    TablesStatistic getTablesStatistic(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException;

    ColumnsStatistic getColumnsStatistic(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException;
}
