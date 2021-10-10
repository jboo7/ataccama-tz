package org.example.ataccama.services;

import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.GetConnectionException;

import java.sql.Connection;

public interface DatabaseConnectionService {
    Connection getConnection(DatabaseConnection databaseConnection) throws GetConnectionException;
}
