package org.example.ataccama.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.exceptions.GetConnectionException;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.DatabaseConnectionService;
import org.example.ataccama.services.data.ColumnMetadata;
import org.example.ataccama.services.data.ColumnStatistic;
import org.example.ataccama.services.data.ColumnsMetadata;
import org.example.ataccama.services.data.ColumnsStatistic;
import org.example.ataccama.services.data.IndexMetadata;
import org.example.ataccama.services.data.PrimaryKeyMetadata;
import org.example.ataccama.services.data.SchemaMetadata;
import org.example.ataccama.services.data.SchemasMetadata;
import org.example.ataccama.services.data.TableDataPreview;
import org.example.ataccama.services.data.TableMetadata;
import org.example.ataccama.services.data.TableRow;
import org.example.ataccama.services.data.TableStatistic;
import org.example.ataccama.services.data.TablesMetadata;
import org.example.ataccama.services.data.TablesStatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public abstract class CommonDatabaseBrowseService implements DatabaseBrowserService {
    private static final String TABLE_SCHEM_COLUMN = "TABLE_SCHEM";
    private static final String TABLE_NAME_COLUMN = "TABLE_NAME";
    private static final String COLUMN_NAME_COLUMN = "COLUMN_NAME";
    private static final String TYPE_NAME_COLUMN = "TYPE_NAME";
    private static final String IS_NULLABLE_COLUMN = "IS_NULLABLE";
    private static final String COLUMN_SIZE_COLUMN = "COLUMN_SIZE";
    private static final String ORDINAL_POSITION_COLUMN = "ORDINAL_POSITION";
    private static final String KEY_SEQ_COLUMN = "KEY_SEQ";
    private static final String PK_NAME_COLUMN = "PK_NAME";
    private static final String INDEX_NAME_COLUMN = "INDEX_NAME";
    private static final int DEFAULT_LIMIT = 100;

    private final DatabaseConnectionService databaseConnectionService;

    @Override
    public final SchemasMetadata getSchemasMetadata(DatabaseConnection databaseConnection) throws DatabaseBrowseException {
        try (final var connection = databaseConnectionService.getConnection(databaseConnection)) {
            return SchemasMetadata.builder()
                                  .schemaMetadata(getSchemasMetadataInternal(connection).sorted(comparing(SchemaMetadata::getName))
                                                                                        .collect(toUnmodifiableList()))
                                  .build();
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed to get schemas metadata", e);
        }
    }

    @Override
    public final TablesMetadata getTablesMetadata(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException {
        try (final var connection = databaseConnectionService.getConnection(databaseConnection)) {
            return TablesMetadata.builder()
                                 .tableMetadata(getTablesMetadataInternal(connection, schema).sorted(comparing(TableMetadata::getName))
                                                                                             .collect(toUnmodifiableList()))
                                 .build();
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed to get tables metadata", e);
        }
    }

    @Override
    public final ColumnsMetadata getColumnsMetadata(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        try (final var connection = databaseConnectionService.getConnection(databaseConnection); //
             final var primaryKeysRs = connection.getMetaData()
                                                 .getPrimaryKeys(null, schema, table); //
             final var indicesRs = connection.getMetaData()
                                             .getIndexInfo(null, schema, table, false, true)) {
            // collecting column metadata
            final var columnsMetadata = getColumnsMetadataInternal(connection, schema, table);
            // collecting primary keys metadata
            final var primaryKeysMap = new HashMap<String, TreeMap<Short, String>>();
            while (primaryKeysRs.next()) {
                final var columnName = primaryKeysRs.getString(COLUMN_NAME_COLUMN);
                final var keySeq = primaryKeysRs.getShort(KEY_SEQ_COLUMN);
                final var pkName = primaryKeysRs.getString(PK_NAME_COLUMN);
                primaryKeysMap.computeIfAbsent(pkName, k -> new TreeMap<>())
                              .put(keySeq, columnName);
            }
            // collecting indices metadata
            final var indexMap = new HashMap<String, TreeMap<Short, String>>();
            while (indicesRs.next()) {
                final var indexName = indicesRs.getString(INDEX_NAME_COLUMN);
                final var columnName = indicesRs.getString(COLUMN_NAME_COLUMN);
                final var ordinalPosition = indicesRs.getShort(ORDINAL_POSITION_COLUMN);
                indexMap.computeIfAbsent(indexName, k -> new TreeMap<>())
                        .put(ordinalPosition, columnName);
            }
            return ColumnsMetadata.builder()
                                  .columnMetadata(columnsMetadata.sorted(comparing(ColumnMetadata::getOrdinal))
                                                                 .collect(toUnmodifiableList()))
                                  .primaryKeyMetadata(primaryKeysMap.entrySet()
                                                                    .stream()
                                                                    .map(e -> PrimaryKeyMetadata.builder()
                                                                                                .name(e.getKey())
                                                                                                .columnNames(e.getValue()
                                                                                                              .values()
                                                                                                              .stream()
                                                                                                              .collect(toUnmodifiableList()))
                                                                                                .build())
                                                                    .sorted(comparing(PrimaryKeyMetadata::getName, nullsFirst(naturalOrder())))
                                                                    .collect(toUnmodifiableList()))
                                  .indexMetadata(indexMap.entrySet()
                                                         .stream()
                                                         .filter(e -> !primaryKeysMap.containsKey(e.getKey()))
                                                         .map(e -> IndexMetadata.builder()
                                                                                .name(e.getKey())
                                                                                .columnNames(e.getValue()
                                                                                              .values()
                                                                                              .stream()
                                                                                              .collect(toUnmodifiableList()))
                                                                                .build())
                                                         .sorted(comparing(IndexMetadata::getName))
                                                         .collect(toUnmodifiableList()))
                                  .build();
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed to get columns metadata", e);
        }
    }

    @Override
    public final TableDataPreview getTableDataPreview(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        final var sql = String.format("select * from %s limit %d", getFullTableName(schema, table), DEFAULT_LIMIT);
        try (final var connection = databaseConnectionService.getConnection(databaseConnection); //
             PreparedStatement ps = connection.prepareStatement(sql); //
             ResultSet rs = ps.executeQuery()) {
            final var columnNames = getColumnsMetadataInternal(connection, schema, table).sorted(comparing(ColumnMetadata::getOrdinal))
                                                                                         .map(ColumnMetadata::getName)
                                                                                         .collect(toUnmodifiableList());
            var rowIndex = 0;
            final var columnValues = new ArrayList<>();
            final var rows = new ArrayList<TableRow>();
            while (rs.next()) {
                columnValues.clear();
                for (int i = 0; i < columnNames.size(); i++) {
                    columnValues.add(rs.getObject(i + 1));
                }
                rows.add(TableRow.builder()
                                 .index(rowIndex++)
                                 .values(unmodifiableList(new ArrayList<>(columnValues)))
                                 .build());
            }
            return TableDataPreview.builder()
                                   .columnNames(columnNames)
                                   .rows(List.copyOf(rows))
                                   .build();
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed to get table data preview", e);
        }
    }

    @Override
    public final TablesStatistic getTablesStatistic(DatabaseConnection databaseConnection, String schema) throws DatabaseBrowseException {
        try (final var connection = databaseConnectionService.getConnection(databaseConnection)) {
            final var tablesStatisticStreamBuilder = Stream.<TableStatistic>builder();
            final var tablesMetadataIt = getTablesMetadataInternal(connection, schema).iterator();
            while (tablesMetadataIt.hasNext()) {
                tablesStatisticStreamBuilder.add(getTableStatisticInternal(connection, schema, tablesMetadataIt.next()
                                                                                                               .getName()));
            }
            return TablesStatistic.builder()
                                  .tablesStatistic(tablesStatisticStreamBuilder.build()
                                                                               .sorted(comparing(TableStatistic::getName))
                                                                               .collect(toUnmodifiableList()))
                                  .build();
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed to get table stats", e);
        }
    }

    @Override
    public final ColumnsStatistic getColumnsStatistic(DatabaseConnection databaseConnection, String schema, String table) throws DatabaseBrowseException {
        try (final var connection = databaseConnectionService.getConnection(databaseConnection)) {
            final var columnsStatisticStreamBuilder = Stream.<ColumnStatistic>builder();
            final var columnsMetadataIt = getColumnsMetadataInternal(connection, schema, table).iterator();
            while (columnsMetadataIt.hasNext()) {
                columnsStatisticStreamBuilder.add(getColumnStatisticInternal(connection, schema, table, columnsMetadataIt.next()
                                                                                                                         .getName()));
            }
            return ColumnsStatistic.builder()
                                   .columnsStatistic(columnsStatisticStreamBuilder.build()
                                                                                  .sorted(comparing(ColumnStatistic::getName))
                                                                                  .collect(toUnmodifiableList()))
                                   .build();
        } catch (GetConnectionException e) {
            throw new DatabaseBrowseException("Failed to get a connection to datasource", e);
        } catch (SQLException e) {
            throw new DatabaseBrowseException("Failed to get column stats", e);
        }
    }

    Stream<SchemaMetadata> getSchemasMetadataInternal(Connection connection) throws SQLException {
        try (final var schemasRs = connection.getMetaData()
                                             .getSchemas()) {
            final var schemaMetadataStreamBuilder = Stream.<SchemaMetadata>builder();
            while (schemasRs.next()) {
                final var schemaName = schemasRs.getString(TABLE_SCHEM_COLUMN);
                schemaMetadataStreamBuilder.add(SchemaMetadata.builder()
                                                              .name(schemaName)
                                                              .build());
            }
            return schemaMetadataStreamBuilder.build();
        }
    }

    Stream<TableMetadata> getTablesMetadataInternal(Connection connection, String schema) throws SQLException {
        try (final var tablesRs = connection.getMetaData()
                                            .getTables(null, schema, null, null)) {
            final var tablesMetadataStreamBuilder = Stream.<TableMetadata>builder();
            while (tablesRs.next()) {
                final var schemaName = tablesRs.getString(TABLE_SCHEM_COLUMN);
                final var tableName = tablesRs.getString(TABLE_NAME_COLUMN);
                tablesMetadataStreamBuilder.add(TableMetadata.builder()
                                                             .schema(schemaName)
                                                             .name(tableName)
                                                             .build());
            }
            return tablesMetadataStreamBuilder.build();
        }
    }

    Stream<ColumnMetadata> getColumnsMetadataInternal(Connection connection, String schema, String table) throws SQLException {
        try (final var columnsRs = connection.getMetaData()
                                             .getColumns(null, schema, table, null)) {
            final var columnStreamBuilder = Stream.<ColumnMetadata>builder();
            while (columnsRs.next()) {
                final var schemaName = columnsRs.getString(TABLE_SCHEM_COLUMN);
                final var tableName = columnsRs.getString(TABLE_NAME_COLUMN);
                final var columnName = columnsRs.getString(COLUMN_NAME_COLUMN);
                final var typeName = columnsRs.getString(TYPE_NAME_COLUMN);
                final var ordinalPosition = columnsRs.getInt(ORDINAL_POSITION_COLUMN);
                final var nullable = columnsRs.getString(IS_NULLABLE_COLUMN);
                final var columnSize = columnsRs.getInt(COLUMN_SIZE_COLUMN);
                columnStreamBuilder.add(ColumnMetadata.builder()
                                                      .schema(schemaName)
                                                      .table(tableName)
                                                      .name(columnName)
                                                      .type(typeName)
                                                      .ordinal(ordinalPosition)
                                                      .nullable(nullable != null ? "YES".equalsIgnoreCase(nullable) : null)
                                                      .columnSize(columnSize)
                                                      .build());
            }
            return columnStreamBuilder.build();
        }
    }

    TableStatistic getTableStatisticInternal(Connection connection, String schema, String table) throws SQLException {
        final var countSql = String.format("select count(*) from %s", getFullTableName(schema, table));
        try (final var countPs = connection.prepareStatement(countSql)) {
            final var tableStatisticBuilder = TableStatistic.builder()
                                                            .schema(schema != null ? schema.toLowerCase(Locale.ROOT) : null)
                                                            .name(table.toLowerCase(Locale.ROOT));
            try (final var rowsCountRs = countPs.executeQuery()) {
                rowsCountRs.next();
                return tableStatisticBuilder.columnCount((int) getColumnsMetadataInternal(connection, schema, table).count())
                                            .rowCount(rowsCountRs.getInt(1))
                                            .build();
            } catch (SQLException e) {
                return tableStatisticBuilder.build();
            }
        }
    }

    abstract ColumnStatistic getColumnStatisticInternal(Connection connection, String schema, String table, String column) throws SQLException;

    static String getFullTableName(String schemaName, String tableName) {
        if (schemaName != null) {
            return String.format("%s.%s", schemaName, tableName);
        }
        return tableName;
    }
}

