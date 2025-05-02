package io.clickhouse.springdata.repository.invoker;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.clickhouse.client.api.Client;
import io.clickhouse.springdata.repository.annotation.Query;
import io.clickhouse.springdata.repository.core.ClickHouseMappingContext;

/**
 * Фабрика для создания MethodInvoker-ов для всех методов интерфейса.
 */
public class MethodInvokerFactory {

    /**
     * @param repositoryInterface – интерфейс-репозиторий, нужен для DerivedQueryMethodInvoker
     * @param methods             – все методы этого интерфейса
     * @param client              – автоконфигурированный ClickHouse-клиент
     * @param context             – MappingContext с метаданными сущностей
     */
    public static <T> Map<Method, MethodInvoker> createInvokers(
            Class<T> repositoryInterface,
            Method[] methods,
            Client client,
            ClickHouseMappingContext context) {

        Map<Method, MethodInvoker> map = new HashMap<>();

        for (Method m : methods) {
            MethodInvoker inv = null;

            if (m.isAnnotationPresent(
                    Query.class)) {
                String sql = m.getAnnotation(
                                Query.class)
                        .value();
//                inv = new QueryAnnotationMethodInvoker(m, sql, client, context);

            } else if (isDerivedQuery(m)) {
                // Передаём repositoryInterface, чтобы извлечь T
                inv = new DerivedQueryMethodInvoker(m, repositoryInterface, client, context);

            } else {
                //TODO заменить на дефолтный инвокер
                inv = new DerivedQueryMethodInvoker(m, repositoryInterface, client, context);
            }

            map.put(m, inv);
        }

        return map;
    }

    private static boolean isDerivedQuery(Method method) {
        String name = method.getName();
        return name.startsWith("findBy") || name.startsWith("countBy");
    }
}