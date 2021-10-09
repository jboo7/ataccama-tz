package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DatabaseConnectionByIdRouter {
    private static final String CONNECTION_BY_ID_PATH = "connection_by_id";

    private final DatabaseConnectionRepo databaseConnectionRepo;

    @GetMapping("/" + CONNECTION_BY_ID_PATH + "/{id}")
    public String get(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("connection", databaseConnectionRepo.findById(id));
        return CONNECTION_BY_ID_PATH;
    }
}
