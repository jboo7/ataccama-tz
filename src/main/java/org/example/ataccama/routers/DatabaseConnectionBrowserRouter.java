package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.example.ataccama.services.DatabaseBrowserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.example.ataccama.routers.DatabaseConnectionRouter.CONNECTIONS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionRouter.ID_PATH_VAR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/" + CONNECTIONS_PATH + "/{" + ID_PATH_VAR + "}")
@RequiredArgsConstructor
public class DatabaseConnectionBrowserRouter {
    private static final String SCHEMAS_PATH = "schemas";
    private static final String TABLES_PATH = "tables";
    private static final String TABLE_PATH_VAR = "table";
    private static final String COLUMNS_PATH = "columns";
    private static final String DATA = "data";

    private final DatabaseConnectionRepo databaseConnectionRepo;
    private final DatabaseBrowserService databaseBrowserService;

    @GetMapping(value = "/" + SCHEMAS_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllSchemas(@PathVariable Long id) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var schemas = databaseBrowserService.getSchemas(databaseConnection.get());
        return ok(schemas);
    }

    @GetMapping(value = "/" + TABLES_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllTables(@PathVariable Long id, //
                                           @RequestParam String schema) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var tables = databaseBrowserService.getTables(databaseConnection.get(), schema);
        return ok(tables);
    }

    @GetMapping(value = "/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + COLUMNS_PATH, //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllTableColumns(@PathVariable Long id, //
                                                 @RequestParam String schema, //
                                                 @PathVariable String table) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var columns = databaseBrowserService.getColumns(databaseConnection.get(), schema, table);
        return ok(columns);
    }

    @GetMapping(value = "/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + DATA,//
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readTableData(@PathVariable Long id, //
                                           @RequestParam String schema, //
                                           @PathVariable String table) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        final var rows = databaseBrowserService.getRows(databaseConnection.get(), schema, table);
        return ok(rows);
    }
}
