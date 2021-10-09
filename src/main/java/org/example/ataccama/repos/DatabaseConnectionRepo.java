package org.example.ataccama.repos;

import org.example.ataccama.data.DatabaseConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseConnectionRepo extends JpaRepository<DatabaseConnection, Long> {}
