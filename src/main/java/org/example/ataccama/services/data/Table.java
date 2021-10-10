package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class Table {
    private final String schema;
    @NonNull
    private final String name;
    @NonNull
    private final Long columnCount;
}
