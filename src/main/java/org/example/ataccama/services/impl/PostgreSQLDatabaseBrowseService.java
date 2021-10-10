package org.example.ataccama.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.exceptions.GetConnectionException;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.DatabaseConnectionService;
import org.example.ataccama.services.data.Column;
import org.example.ataccama.services.data.Row;
import org.example.ataccama.services.data.Schema;
import org.example.ataccama.services.data.Table;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostgreSQLDatabaseBrowseService implements DatabaseBrowserService {
    private final DatabaseConnectionService databaseConnectionService;

    @Override
    public List<Schema> getSchemas(DatabaseConnection databaseConnection) throws DatabaseBrowseException {
        try (Connection connection = databaseConnectionService.getConnection(databaseConnection); //
             ResultSet tableRs = connection.getMetaData()
                                           .getTables(null, null, null, null)) {
            final var schemaStreamBuilder = Stream.<String>builder();
            while (tableRs.next()) {
                final var schema = tableRs.getString("TABLE_SCHEM");
                schemaStreamBuilder.add(schema);
            }
            return schemaStreamBuilder.build()
                                      .collect(groupingBy(identity(), counting()))
                                      .entrySet()
                                      .stream()
                                      .map(e -> Schema.builder()
                                                      .name(e.getKey())
                                                      .tableCount(e.getValue())
                                                      .build())
                                      .sorted(comparing(Schema::getName))
                                      .collect(toList());
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed browse schemas", e);
        }
    }

    @Override
    public List<Table> getTables(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException {
        try (Connection connection = databaseConnectionService.getConnection(databaseConnection); //
             ResultSet tableRs = connection.getMetaData()
                                           .getTables(null, schema, null, null); //
             ResultSet columnRs = connection.getMetaData()
                                            .getColumns(null, schema, null, null)) {
            final var tableColumnCount = new HashMap<String, Long>();
            while (columnRs.next()) {
                final var s = columnRs.getString("TABLE_SCHEM");
                final var t = columnRs.getString("TABLE_NAME");
                final var st = String.format("%s.%s", s, t);
                tableColumnCount.put(st, tableColumnCount.getOrDefault(st, 0L) + 1);
            }
            final var tableStreamBuilder = Stream.<Table>builder();
            while (tableRs.next()) {
                final var s = tableRs.getString("TABLE_SCHEM");
                final var t = tableRs.getString("TABLE_NAME");
                final var st = String.format("%s.%s", s, t);

                tableStreamBuilder.add(Table.builder()
                                            .schema(s)
                                            .name(t)
                                            .columnCount(tableColumnCount.getOrDefault(st, 0L))
                                            .build());
            }
            return tableStreamBuilder.build()
                                     .sorted(nullsFirst(comparing(Table::getSchema)).thenComparing(Table::getName))
                                     .collect(toList());
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed browse tables", e);
        }
    }

    @Override
    public List<Column> getColumns(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        try (Connection connection = databaseConnectionService.getConnection(databaseConnection); //
             ResultSet columnRs = connection.getMetaData()
                                            .getColumns(null, schema, table, null)) {
            final var columnStreamBuilder = Stream.<Column>builder();
            while (columnRs.next()) {
                final var c = columnRs.getString("COLUMN_NAME");
                final var t = columnRs.getString("TYPE_NAME");
                final var n = columnRs.getString("IS_NULLABLE");
                final var cs = columnRs.getInt("COLUMN_SIZE");
                columnStreamBuilder.add(Column.builder()
                                              .name(c)
                                              .type(t)
                                              .nullable(n != null ? "NO".equals(n) : null)
                                              .columnSize(cs)
                                              .build());
            }
            return columnStreamBuilder.build()
                                      .collect(toList());
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed browse tables", e);
        }
    }

    @Override
    public List<Row> getRows(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        return null;
    }
}
