package org.example.ataccama;

import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.example.ataccama.datasources.impl.DelegatingCloseableDatasourceFactory;
import org.example.ataccama.datasources.impl.MySQLCloseableDatasourceFactory;
import org.example.ataccama.datasources.impl.PostgreSQLCloseableDatasourceFactory;
import org.example.ataccama.services.DatabaseBrowserService;
import org.example.ataccama.services.DatabaseConnectionService;
import org.example.ataccama.services.impl.DatabaseConnectionServiceImpl;
import org.example.ataccama.services.impl.DelegatingDatabaseConnectionBrowseService;
import org.example.ataccama.services.impl.MySQLDatabaseBrowseService;
import org.example.ataccama.services.impl.PostgreSQLDatabaseBrowseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

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
    public DatabaseBrowserService databaseBrowserService(PostgreSQLDatabaseBrowseService postgreSQLDatabaseBrowseService, //
                                                         MySQLDatabaseBrowseService mySQLDatabaseBrowseService) {
        return new DelegatingDatabaseConnectionBrowseService(Map.of(POSTGRES, postgreSQLDatabaseBrowseService, MYSQL, mySQLDatabaseBrowseService));
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                                                      .apis(RequestHandlerSelectors.any())
                                                      .paths(PathSelectors.any())
                                                      .build();
    }
}