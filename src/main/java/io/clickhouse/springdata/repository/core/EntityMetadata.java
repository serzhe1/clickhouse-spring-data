package io.clickhouse.springdata.repository.core;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Метаданные сущности ClickHouse: имя таблицы, список столбцов и маппинг полей.
 */
@Getter
public class EntityMetadata {
    private final Class<?> entityClass;
    private final String tableName;
    private final List<String> columnNames;
    private final Map<String, String> fieldToColumn;

    public EntityMetadata(Class<?> entityClass, String tableName,
                          List<String> columnNames, Map<String, String> fieldToColumn) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.fieldToColumn = fieldToColumn;
    }

}
