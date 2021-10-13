package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.example.ataccama.services.DatabaseBrowserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.example.ataccama.routers.DatabaseConnectionApiRouter.CONNECTIONS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionApiRouter.ID_PATH_VAR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/" + CONNECTIONS_PATH + "/{" + ID_PATH_VAR + "}")
@RequiredArgsConstructor
public class DatabaseConnectionBrowseApiRouter {
    public static final String SCHEMAS_PATH = "schemas";
    public static final String TABLES_PATH = "tables";
    public static final String TABLE_PATH_VAR = "table";
    public static final String COLUMNS_PATH = "columns";
    public static final String DATA_PATH = "data";
    public static final String STATS_PATH = "stats";

    private final DatabaseConnectionRepo databaseConnectionRepo;
    private final DatabaseBrowserService databaseBrowserService;

    @GetMapping(value = "/" + SCHEMAS_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllSchemas(@PathVariable Long id) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var schemas = databaseBrowserService.getSchemasMetadata(databaseConnection.get());
        return ok(schemas);
    }

    @GetMapping(value = "/" + TABLES_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllTables(@PathVariable Long id, //
                                           @RequestParam(required = false) String schema) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var tables = databaseBrowserService.getTablesMetadata(databaseConnection.get(), schema);
        return ok(tables);
    }

    @GetMapping(value = "/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + COLUMNS_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllTableColumns(@PathVariable Long id, //
                                                 @RequestParam(required = false) String schema, //
                                                 @PathVariable String table) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var columns = databaseBrowserService.getColumnsMetadata(databaseConnection.get(), schema, table);
        return ok(columns);
    }

    @GetMapping(value = "/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + DATA_PATH,//
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readTableData(@PathVariable Long id, //
                                           @RequestParam(required = false) String schema, //
                                           @PathVariable String table) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var data = databaseBrowserService.getTableDataPreview(databaseConnection.get(), schema, table);
        return ok(data);
    }

    @GetMapping(value = "/" + TABLES_PATH + "/" + STATS_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readTablesStatistic(@PathVariable Long id, //
                                                 @RequestParam(required = false) String schema) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var tablesStatistic = databaseBrowserService.getTablesStatistic(databaseConnection.get(), schema);
        return ok(tablesStatistic);
    }

    @GetMapping(value = "/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + STATS_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readColumnsStatistic(@PathVariable Long id, //
                                                  @RequestParam(required = false) String schema, //
                                                  @PathVariable String table) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var columnsStatistic = databaseBrowserService.getColumnsStatistic(databaseConnection.get(), schema, table);
        return ok(columnsStatistic);
    }
}
