package org.example.ataccama;

import org.example.ataccama.datasources.CloseableDatasourceFactory;
import org.example.ataccama.datasources.DelegatingCloseableDatasourceFactory;
import org.example.ataccama.datasources.MySQLCloseableDatasourceFactory;
import org.example.ataccama.datasources.PostgresCloseableDatasourceFactory;
import org.example.ataccama.services.impl.DatabaseConnectionServiceImpl;
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
    public DatabaseConnectionServiceImpl databaseConnectionService(CloseableDatasourceFactory closeableDatasourceFactory) {
        return new DatabaseConnectionServiceImpl(new ConcurrentHashMap<>(), closeableDatasourceFactory);
    }

    @Bean
    public CloseableDatasourceFactory closeableDatasourceFactory(PostgresCloseableDatasourceFactory postgresCloseableDatasourceFactory, //
                                                                 MySQLCloseableDatasourceFactory mySQLCloseableDatasourceFactory) {
        return new DelegatingCloseableDatasourceFactory(Map.of(POSTGRES, postgresCloseableDatasourceFactory, //
                                                               MYSQL, mySQLCloseableDatasourceFactory));
    }
}