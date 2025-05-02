package io.clickhouse.springdata.repository.annotation;

import io.clickhouse.springdata.repository.config.ClickHouseRepositoriesRegistrar;
import io.clickhouse.springdata.repository.config.ClickHouseRepositoryConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Активирует сканирование интерфейсов-репозиториев ClickHouse.
 * basePackages - пакеты для поиска репозиториев.
 */

@Retention(RUNTIME)
@Target(TYPE)
@Import({
        ClickHouseRepositoriesRegistrar.class,
        ClickHouseRepositoryConfiguration.class   // <-- подключаем бин MappingContext
})
public @interface EnableClickHouseRepositories {
    String[] basePackages() default {};
}