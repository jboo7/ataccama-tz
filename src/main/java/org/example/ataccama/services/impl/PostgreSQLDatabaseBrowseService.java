package org.example.ataccama.services.impl;

import org.example.ataccama.services.DatabaseConnectionService;
import org.springframework.stereotype.Service;

@Service
public class PostgreSQLDatabaseBrowseService extends CommonDatabaseBrowseService {
    public PostgreSQLDatabaseBrowseService(DatabaseConnectionService databaseConnectionService) {
        super(databaseConnectionService);
    }
}
