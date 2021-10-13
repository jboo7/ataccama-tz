package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class PrimaryKeyMetadata {
    private final String name;
    @NonNull
    private final List<IndexedColumn> columns;
}
