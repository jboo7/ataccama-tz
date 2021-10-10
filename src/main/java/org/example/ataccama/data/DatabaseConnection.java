package org.example.ataccama.data;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DatabaseConnection {
    public static final String NAME_COLUMN = "name";
    public static final String HOST_COLUMN = "host";
    public static final String DATABASE_COLUMN = "database";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, name = NAME_COLUMN)
    private String name;

    @Column(nullable = false)
    private DatabaseType type;

    @Column(nullable = false, name = HOST_COLUMN)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false, name = DATABASE_COLUMN)
    private String database;

    private String username;

    private String password;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
