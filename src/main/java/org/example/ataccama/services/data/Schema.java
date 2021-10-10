package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class Schema {
    @NonNull
    private final String name;
    @NonNull
    private final Long tableCount;
}
