package org.example.ataccama.routers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ataccama.data.DatabaseConnection;
import org.example.ataccama.repos.DatabaseConnectionRepo;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.example.ataccama.data.DatabaseConnection.DATABASE_COLUMN;
import static org.example.ataccama.data.DatabaseConnection.NAME_COLUMN;
import static org.example.ataccama.routers.DatabaseConnectionApiRouter.CONNECTIONS_PATH;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/" + CONNECTIONS_PATH)
@Slf4j
@RequiredArgsConstructor
public class DatabaseConnectionApiRouter {
    public static final String CONNECTIONS_PATH = "connections";
    public static final String ID_PATH_VAR = "id";

    private final DatabaseConnectionRepo databaseConnectionRepo;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAll() {
        return ok(databaseConnectionRepo.findAll(Sort.by(ASC, NAME_COLUMN, DATABASE_COLUMN)));
    }

    @GetMapping(value = "/{" + ID_PATH_VAR + "}", //
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readOne(@PathVariable Long id) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        return databaseConnection.map(ResponseEntity::ok)
                                 .orElseGet(notFound()::build);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody DatabaseConnection connection) {
        if (connection != null) {
            final var databaseConnection = databaseConnectionRepo.save(connection);
            return created(URI.create("/" + CONNECTIONS_PATH + "/" + databaseConnection.getId())).build();
        }
        return badRequest().build();
    }

    @PostMapping(value = "/{" + ID_PATH_VAR + "}", //
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, //
                                    @RequestBody DatabaseConnection connection) {
        final var databaseConnection = databaseConnectionRepo.findById(id);
        if (databaseConnection.isEmpty()) {
            return notFound().build();
        }
        if (connection == null) {
            return badRequest().build();
        }
        databaseConnectionRepo.save(connection);
        return ok().build();
    }

    @DeleteMapping("/{" + ID_PATH_VAR + "}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (databaseConnectionRepo.existsById(id)) {
            databaseConnectionRepo.deleteById(id);
            return ok().build();
        }
        return notFound().build();
    }
}
