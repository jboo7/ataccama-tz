package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.example.ataccama.data.DatabaseConnection.DATABASE_COLUMN;
import static org.example.ataccama.data.DatabaseConnection.NAME_COLUMN;
import static org.example.ataccama.routers.DatabaseConnectionApiRouter.CONNECTIONS_PATH;
import static org.example.ataccama.routers.DatabaseConnectionApiRouter.ID_PATH_VAR;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/" + CONNECTIONS_PATH)
@Slf4j
@RequiredArgsConstructor
public class DatabaseConnectionMvcRouter {
    private static final String CONNECTION_PATH = "connection";

    private final DatabaseConnectionRepo databaseConnectionRepo;

    @GetMapping
    public String readAll(Model model) {
        model.addAttribute("connections", databaseConnectionRepo.findAll(Sort.by(ASC, NAME_COLUMN, DATABASE_COLUMN)));
        model.addAttribute("connection", new DatabaseConnection());
        return CONNECTIONS_PATH;
    }

    @GetMapping("/{" + ID_PATH_VAR + "}")
    public String readOne(@PathVariable Long id, Model model) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isPresent()) {
            model.addAttribute("connection", databaseConnection.get());
            return CONNECTION_PATH;
        }
        return "redirect:/" + CONNECTIONS_PATH;
    }

    @PostMapping
    public String create(@ModelAttribute DatabaseConnection connection) {
        databaseConnectionRepo.save(connection);
        return "redirect:/" + CONNECTIONS_PATH;
    }

    @PostMapping("/{" + ID_PATH_VAR + "}")
    public String update(@PathVariable Long id, //
                         @ModelAttribute DatabaseConnection connection, //
                         Model model) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return "redirect:/" + CONNECTIONS_PATH;
        }
        model.addAttribute("connection", databaseConnectionRepo.save(connection));
        return CONNECTION_PATH;
    }

    @DeleteMapping("/{" + ID_PATH_VAR + "}")
    @ResponseStatus(OK)
    public void delete(@PathVariable Long id) {
        if (databaseConnectionRepo.existsById(id)) {
            databaseConnectionRepo.deleteById(id);
        }
    }
}
