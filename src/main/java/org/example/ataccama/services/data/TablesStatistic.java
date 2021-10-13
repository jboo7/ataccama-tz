package org.example.ataccama.services.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
public class TablesStatistic {
    @NonNull
    private final List<TableStatistic> tablesStatistic;
}
