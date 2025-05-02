// Directory: src/main/java/io/clickhouse/springdata/repository/core

package io.clickhouse.springdata.repository.core;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.metadata.TableSchema;
import io.clickhouse.springdata.core.annotation.Table;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Контекст маппинга сущностей ClickHouse: отвечает за сканирование и хранение метаданных POJO ↔ таблицы.
 */
public class ClickHouseMappingContext implements ResourceLoaderAware {

    private final List<String> basePackages;
    private final Map<Class<?>, EntityMetadata> entityMetadataMap = new ConcurrentHashMap<>();
    private ResourceLoader resourceLoader;
    private volatile boolean initialized = false;
    private final Client client;

    /**
     * @param basePackages пакеты для поиска сущностей с аннотацией @Table
     */
    public ClickHouseMappingContext(List<String> basePackages, Client client) {
        this.basePackages = basePackages;
        this.client = client;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Инициализация: сканирует классы с @Table и строит EntityMetadata.
     */
    /**
     * Сканируем @Table-классы, собираем EntityMetadata и сразу регистрируем
     * их TableSchema в ClickHouse Client.
     */
    private synchronized void initialize() {
        if (initialized) {
            return;
        }

        // 1 Сканируем пакеты на классы с @Table
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackages.toArray(new String[0]))
                .setScanners(Scanners.TypesAnnotated)
        );
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Table.class);

        for (Class<?> entityClass : entities) {
            // Собираем EntityMetadata
            EntityMetadata metadata = buildEntityMetadata(entityClass);
            entityMetadataMap.put(entityClass, metadata);

            // Берём из клиента схему по имени таблицы и регистрируем
            String tableName = metadata.getTableName();
            TableSchema schema = client.getTableSchema(tableName);
            client.register(entityClass, schema);

        }

        initialized = true;
    }

    /**
     * Построить EntityMetadata для класса-сущности.
     */
    private EntityMetadata buildEntityMetadata(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        String tableName = table != null && !table.name().isEmpty()
                ? table.name()
                : entityClass.getSimpleName();

        Map<String, String> fieldToColumn = new LinkedHashMap<>();
        List<String> columnNames = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            String column = field.getName();
            // Если нужна аннотация @Column, можно добавить проверку
            fieldToColumn.put(field.getName(), column);
            columnNames.add(column);
        }

        return new EntityMetadata(entityClass, tableName, Collections.unmodifiableList(columnNames),
                Collections.unmodifiableMap(fieldToColumn));
    }

    /**
     * Получить метаданные сущности, кидает, если не найден.
     */
    public EntityMetadata getEntityMetadata(Class<?> entityClass) {
        if (!initialized) {
            initialize();
        }
        EntityMetadata md = entityMetadataMap.get(entityClass);
        if (md == null) {
            throw new IllegalArgumentException("Класс не зарегистрирован как сущность ClickHouse: " + entityClass);
        }
        return md;
    }

    /**
     * Проверить, зарегистрирован ли класс-сущность.
     */
    public boolean hasEntity(Class<?> entityClass) {
        if (!initialized) {
            initialize();
        }
        return entityMetadataMap.containsKey(entityClass);
    }

    /**
     * Удобные методы-фасады
     */
    public String getTableName(Class<?> entityClass) {
        return getEntityMetadata(entityClass).getTableName();
    }

    public List<String> getAllColumns(Class<?> entityClass) {
        return getEntityMetadata(entityClass).getColumnNames();
    }

    public Map<String, String> getFieldToColumnMap(Class<?> entityClass) {
        return getEntityMetadata(entityClass).getFieldToColumn();
    }

    public String getColumnForField(Class<?> entityClass, String fieldName) {
        String col = getFieldToColumnMap(entityClass).get(fieldName);
        if (col == null) {
            throw new IllegalArgumentException(
                    "Поле не найдено в метаданных сущности: " + fieldName);
        }
        return col;
    }
}