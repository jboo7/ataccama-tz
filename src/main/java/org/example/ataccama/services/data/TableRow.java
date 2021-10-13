package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class TableRow {
    @NonNull
    private final Integer index;
    @NonNull
    private final List<Object> values;
}
