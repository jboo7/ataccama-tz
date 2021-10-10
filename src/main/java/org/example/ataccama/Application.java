package org.example.ataccama;

import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.example.ataccama.datasources.impl.DelegatingCloseableDatasourceFactory;
import org.example.ataccama.datasources.impl.MySQLCloseableDatasourceFactory;
import org.example.ataccama.datasources.impl.PostgreSQLCloseableDatasourceFactory;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.DatabaseConnectionService;
import org.example.ataccama.services.impl.DatabaseConnectionServiceImpl;
import org.example.ataccama.services.impl.DelegatingDatabaseConnectionBrowseService;
import org.example.ataccama.services.impl.PostgreSQLDatabaseBrowseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.ataccama.data.DatabaseType.MYSQL;
import static org.example.ataccama.data.DatabaseType.POSTGRES;

@SpringBootApplication
@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DatabaseConnectionService databaseConnectionService(CloseableDatasourceFactory closeableDatasourceFactory) {
        return new DatabaseConnectionServiceImpl(new ConcurrentHashMap<>(), closeableDatasourceFactory);
    }

    @Bean
    public CloseableDatasourceFactory closeableDatasourceFactory(PostgreSQLCloseableDatasourceFactory postgreSQLCloseableDatasourceFactory, //
                                                                 MySQLCloseableDatasourceFactory mySQLCloseableDatasourceFactory) {
        return new DelegatingCloseableDatasourceFactory(Map.of(POSTGRES, postgreSQLCloseableDatasourceFactory, //
                                                               MYSQL, mySQLCloseableDatasourceFactory));
    }

    @Bean
    public DatabaseBrowserService databaseBrowserService(PostgreSQLDatabaseBrowseService postgreSQLDatabaseBrowseService) {
        return new DelegatingDatabaseConnectionBrowseService(Map.of(POSTGRES, postgreSQLDatabaseBrowseService));
    }
}