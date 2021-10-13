package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ColumnMetadata {
    private final String schema;
    @NonNull
    private final String table;
    @NonNull
    private final String name;
    @NonNull
    private final String type;
    @NonNull
    private final Integer ordinal;
    private final Boolean nullable;
    private final Integer columnSize;
}
