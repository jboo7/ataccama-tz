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
    @Builder.Default
    private final List<Column> columns = List.of();

    public int getColumnCount() {
        return columns.size();
    }
}
