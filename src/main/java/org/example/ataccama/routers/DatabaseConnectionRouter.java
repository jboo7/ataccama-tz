package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.example.ataccama.data.DatabaseConnection.NAME_COLUMN;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DatabaseConnectionRouter {
    private static final String CONNECTION_PATH = "connection";

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_MIN_PAGE_SIZE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final DatabaseConnectionRepo databaseConnectionRepo;

    @GetMapping("/" + CONNECTION_PATH)
    public String get(//
                      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page, //
                      @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int pageSize, //

                      Model model) {
        model.addAttribute("connection", new DatabaseConnection());
        model.addAttribute("connections", databaseConnectionRepo.findAll(PageRequest.of(Math.max(DEFAULT_PAGE, page) - 1, //
                                                                                        Math.max(DEFAULT_MIN_PAGE_SIZE, pageSize), //
                                                                                        ASC, //
                                                                                        NAME_COLUMN))
                                                                .getContent());
        return CONNECTION_PATH;
    }

    @PostMapping("/" + CONNECTION_PATH)
    public String post(@ModelAttribute DatabaseConnection connection) {
        databaseConnectionRepo.save(connection);
        return "redirect:" + CONNECTION_PATH;
    }

    @DeleteMapping("/" + CONNECTION_PATH + "/{id}")
    public String delete(@PathVariable Long id) {
        databaseConnectionRepo.deleteById(id);
        return "redirect:" + CONNECTION_PATH;
    }
}
