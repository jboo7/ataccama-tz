package org.example.ataccama.services;

import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.services.data.Column;
import org.example.ataccama.services.data.Schema;
import org.example.ataccama.services.data.Table;

import java.util.List;

public interface DatabaseBrowserService {
    List<Schema> getSchemas(DatabaseConnection databaseConnection);

    List<Table> getTables(DatabaseConnection databaseConnection, String schema);

    List<Column> getColumns(DatabaseConnection databaseConnection, String schema, String table);
}
