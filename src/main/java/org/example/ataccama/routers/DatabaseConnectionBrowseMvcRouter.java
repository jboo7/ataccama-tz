package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.exceptions.DatabaseBrowseException;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.example.ataccama.services.DatabaseBrowserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import static org.example.ataccama.routers.DatabaseConnectionApiRouter.CONNECTIONS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionApiRouter.ID_PATH_VAR;
import static org.example.ataccama.routers.DatabaseConnectionBrowseApiRouter.COLUMNS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionBrowseApiRouter.DATA_PATH;
import static org.example.ataccama.routers.DatabaseConnectionBrowseApiRouter.SCHEMAS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionBrowseApiRouter.STATS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionBrowseApiRouter.TABLES_PATH;
import static org.example.ataccama.routers.DatabaseConnectionBrowseApiRouter.TABLE_PATH_VAR;

@Controller
@RequestMapping("/" + CONNECTIONS_PATH + "/{" + ID_PATH_VAR + "}")
@Slf4j
@RequiredArgsConstructor
public class DatabaseConnectionBrowseMvcRouter {
    private static final String TABLE_DATA_PATH = "table_data";
    private static final String TABLES_STATS_PATH = "tables_stats";
    private static final String COLUMNS_STATS_PATH = "columns_stats";

    private final DatabaseConnectionRepo databaseConnectionRepo;
    private final DatabaseBrowserService databaseBrowserService;

    @GetMapping("/" + SCHEMAS_PATH)
    public String readAllSchemas(@PathVariable Long id, //
                                 Model model) throws DatabaseBrowseException {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty() || !databaseConnection.get()
                                                               .getType()
                                                               .isSchemasSupported()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnection.get());
        model.addAttribute("schemas", databaseBrowserService.getSchemasMetadata(databaseConnection.get())
                                                            .getSchemaMetadata());
        return SCHEMAS_PATH;
    }

    @GetMapping("/" + TABLES_PATH)
    public String readAllTables(@PathVariable Long id, //
                                @RequestParam(required = false) String schema, //
                                Model model) throws DatabaseBrowseException {
        final var databaseConnection = getDatabaseConnection(id, schema);
        if (databaseConnection.isEmpty()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnection.get());
        model.addAttribute("schema", schema);
        model.addAttribute("tables", databaseBrowserService.getTablesMetadata(databaseConnection.get(), schema)
                                                           .getTableMetadata());
        return TABLES_PATH;
    }

    @GetMapping("/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + COLUMNS_PATH)
    public String readAllTableColumns(@PathVariable Long id, //
                                      @RequestParam(required = false) String schema, //
                                      @PathVariable String table, //
                                      Model model) throws DatabaseBrowseException {
        final var databaseConnection = getDatabaseConnection(id, schema);
        if (databaseConnection.isEmpty()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnection.get());
        model.addAttribute("schema", schema);
        model.addAttribute("table", table);
        model.addAttribute("columns", databaseBrowserService.getColumnsMetadata(databaseConnection.get(), schema, table));
        return COLUMNS_PATH;
    }

    @GetMapping("/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + DATA_PATH)
    public String readTableData(@PathVariable Long id, //
                                @RequestParam(required = false) String schema, //
                                @PathVariable String table, //
                                Model model) throws DatabaseBrowseException {
        final var databaseConnection = getDatabaseConnection(id, schema);
        if (databaseConnection.isEmpty()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnection.get());
        model.addAttribute("schema", schema);
        model.addAttribute("table", table);
        model.addAttribute("data", databaseBrowserService.getTableDataPreview(databaseConnection.get(), schema, table));
        return TABLE_DATA_PATH;
    }

    @GetMapping("/" + TABLES_PATH + "/" + STATS_PATH)
    public String readTablesStatistic(@PathVariable Long id, //
                                      @RequestParam(required = false) String schema, //
                                      Model model) throws DatabaseBrowseException {
        final var databaseConnection = getDatabaseConnection(id, schema);
        if (databaseConnection.isEmpty()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnection.get());
        model.addAttribute("schema", schema);
        model.addAttribute("tablesStats", databaseBrowserService.getTablesStatistic(databaseConnection.get(), schema));
        return TABLES_STATS_PATH;
    }

    @GetMapping("/" + TABLES_PATH + "/{" + TABLE_PATH_VAR + "}/" + STATS_PATH)
    public String readColumnsStatistic(@PathVariable Long id, //
                                       @RequestParam(required = false) String schema, //
                                       @PathVariable String table, //
                                       Model model) throws DatabaseBrowseException {
        final var databaseConnection = getDatabaseConnection(id, schema);
        if (databaseConnection.isEmpty()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnection.get());
        model.addAttribute("schema", schema);
        model.addAttribute("table", table);
        model.addAttribute("columnsStats", databaseBrowserService.getColumnsStatistic(databaseConnection.get(), schema, table));
        return COLUMNS_STATS_PATH;
    }

    private Optional<DatabaseConnection> getDatabaseConnection(Long id, String schema) {
        return databaseConnectionRepo.findById(id)
                                     .filter(dbcon -> dbcon.getType()
                                                           .isSchemasSupported() && schema != null //
                                             || !dbcon.getType()
                                                      .isSchemasSupported() && schema == null);
    }
}
