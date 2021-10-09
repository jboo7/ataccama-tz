package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.example.ataccama.services.DatabaseBrowserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DatabaseConnectionBrowseRouter {
    private static final String CONNECTIONS_PATH = "connections";
    private static final String SCHEMAS_PATH = "schemas";

    private final DatabaseConnectionRepo databaseConnectionRepo;
    private final DatabaseBrowserService databaseBrowserService;

    @GetMapping("/" + CONNECTIONS_PATH + "/{id}/" + SCHEMAS_PATH)
    public String getSchemas(@PathVariable Long id, Model model) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isPresent()) {
            model.addAttribute("connection", databaseConnection.get());
            model.addAttribute("schemas", databaseBrowserService.getSchemas(databaseConnection.get()));
            return SCHEMAS_PATH;
        }
        return CONNECTIONS_PATH;
    }
}
