package org.example.ataccama.services.impl;

import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.data.Column;
import org.example.ataccama.services.data.Schema;
import org.example.ataccama.services.data.Table;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseBrowseServiceImpl implements DatabaseBrowserService {
    @Override
    public List<Schema> getSchemas(DatabaseConnection databaseConnection) {
        return List.of();
    }

    @Override
    public List<Table> getTables(DatabaseConnection databaseConnection, String schema) {
        return null;
    }

    @Override
    public List<Column> getColumns(DatabaseConnection databaseConnection, String schema, String table) {
        return null;
    }
}
