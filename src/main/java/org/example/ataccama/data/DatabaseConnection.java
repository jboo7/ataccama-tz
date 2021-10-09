package org.example.ataccama.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DatabaseConnection implements Patchable<DatabaseConnection>, Serializable {
    public static final String NAME_COLUMN = "name";
    public static final String HOST_COLUMN = "host";
    public static final String DATABASE_COLUMN = "database";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, name = NAME_COLUMN)
    private String name;

    @Column(nullable = false, name = HOST_COLUMN)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false, name = DATABASE_COLUMN)
    private String database;

    private String username;

    private String password;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @Override
    public DatabaseConnection patch(DatabaseConnection that) {
        final DatabaseConnection merged = SerializationUtils.clone(this);
        merged.name = that.name;
        merged.host = that.host;
        merged.port = that.port;
        merged.database = that.database;
        merged.username = that.username;
        merged.password = that.password;
        return merged;
    }
}
