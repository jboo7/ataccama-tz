package org.example.ataccama.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@Getter
public enum DatabaseType {
    MYSQL(false), POSTGRES(true);

    private final boolean schemasSupported;
}
