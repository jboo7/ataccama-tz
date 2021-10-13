package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class TableDataPreview {
    @NonNull
    private final List<String> columnNames;
    @NonNull
    private final List<TableRow> rows;
}
