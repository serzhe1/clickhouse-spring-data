// ClickHouseRepository.java
package io.clickhouse.springdata.repository.core;

import java.util.List;

/**
 * Базовый интерфейс ClickHouse-репозитория с минимальным CRUD API.
 * @param <T>  тип сущности
 * @param <ID> тип идентификатора (необязателен в базовом API)
 */
public interface ClickHouseRepository<T, ID> extends ClickHouseRepositoryMarker {
//    /**
//     * Выбрать все записи из таблицы.
//     */
//    List<T> findAll();
//
//    /**
//     * Вернуть количество записей.
//     */
//    long count();
//
//    /**
//     * Сохранить (bulk) набор сущностей.
//     */
//    <S extends T> List<S> saveAll(Iterable<S> entities);
}