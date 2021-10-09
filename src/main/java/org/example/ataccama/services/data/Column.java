package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class Column {
    @NonNull
    private final String name;
    @NonNull
    private final String type;
    @NonNull
    private final Boolean primaryKey;
}
