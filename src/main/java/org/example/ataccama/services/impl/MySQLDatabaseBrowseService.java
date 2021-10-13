package org.example.ataccama.services.impl;

import org.example.ataccama.services.DatabaseConnectionService;
import org.example.ataccama.services.data.ColumnStatistic;
import org.example.ataccama.services.data.SchemaMetadata;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class MySQLDatabaseBrowseService extends CommonDatabaseBrowseService {
    public MySQLDatabaseBrowseService(DatabaseConnectionService databaseConnectionService) {
        super(databaseConnectionService);
    }

    @Override
    Stream<SchemaMetadata> getSchemasMetadataInternal(Connection connection) {
        return Stream.empty();
    }

    @Override
    ColumnStatistic getColumnStatisticInternal(Connection connection, String schema, String table, String column) throws SQLException {
        final var sql = String.format("select min(%2$s), max(%2$s), avg(%2$s) from %1$s", getFullTableName(schema, table), column);
        final var medianSql = String.format("" //
                                                    + " SELECT AVG(%2$s)                                                              "//
                                                    + " FROM (                                                                        "//
                                                    + " SELECT %2$s, @rownum:=@rownum+1 as `row_number`, @total_rows:=@rownum         "//
                                                    + "   FROM %1$s, (SELECT @rownum:=0) r                                            "//
                                                    + "   WHERE %2$s is NOT NULL                                                      "//
                                                    + "   ORDER BY %2$s                                                               "//
                                                    + " ) as dd                                                                       "//
                                                    + " WHERE dd.row_number IN ( FLOOR((@total_rows+1)/2), FLOOR((@total_rows+2)/2) ) ",//
                                            getFullTableName(schema, table), column);
        try (final var ps = connection.prepareStatement(sql); //
             final var medianPs = connection.prepareStatement(medianSql)) {
            final var columnStatisticBuilder = ColumnStatistic.builder()
                                                              .schema(schema != null ? schema.toLowerCase(Locale.ROOT) : null)
                                                              .table(table.toLowerCase(Locale.ROOT))
                                                              .name(column.toLowerCase(Locale.ROOT));
            try (final var rs = ps.executeQuery(); //
                 final var medianRs = medianPs.executeQuery()) {
                rs.next();
                medianRs.next();
                return columnStatisticBuilder.min(rs.getObject(1))
                                             .max(rs.getObject(2))
                                             .avg(rs.getObject(3))
                                             .median(medianRs.getObject(1))
                                             .build();
            } catch (SQLException e) {
                return columnStatisticBuilder.build();
            }
        }
    }
}
