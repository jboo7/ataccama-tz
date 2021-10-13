package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class IndexedColumn {
    @NonNull
    private final String name;
    @NonNull
    private final Integer ordinal;
}
