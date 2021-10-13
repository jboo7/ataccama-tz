package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class ColumnsMetadata {
    @NonNull
    private final List<ColumnMetadata> columnMetadata;
    @NonNull
    private final List<PrimaryKeyMetadata> primaryKeyMetadata;
    @NonNull
    private final List<IndexMetadata> indexMetadata;
}
