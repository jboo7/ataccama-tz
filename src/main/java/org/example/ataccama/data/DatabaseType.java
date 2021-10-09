package org.example.ataccama.data;

import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public enum DatabaseType {
    MYSQL(false), POSTGRES(true);

    private final boolean schemasSupported;
}
