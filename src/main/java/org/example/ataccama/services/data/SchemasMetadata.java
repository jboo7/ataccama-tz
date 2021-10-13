package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class SchemasMetadata {
    @NonNull
    private final List<SchemaMetadata> schemaMetadata;
}
