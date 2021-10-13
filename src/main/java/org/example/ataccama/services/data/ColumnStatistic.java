package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ColumnStatistic {
    private final String schema;
    @NonNull
    private final String table;
    @NonNull
    private final String name;
    private final Object min;
    private final Object max;
    private final Object avg;
    private final Object median;
}
