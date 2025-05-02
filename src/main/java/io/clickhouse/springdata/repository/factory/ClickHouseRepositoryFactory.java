package io.clickhouse.springdata.repository.factory;

import com.clickhouse.client.api.Client;
import io.clickhouse.springdata.repository.core.ClickHouseMappingContext;
import io.clickhouse.springdata.repository.invoker.ClickHouseInvocationHandler;
import io.clickhouse.springdata.repository.invoker.MethodInvoker;
import io.clickhouse.springdata.repository.invoker.MethodInvokerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Фабрика для создания прокси-реализаций интерфейсов ClickHouse-репозиториев.
 */
public class ClickHouseRepositoryFactory {

    private final Client clickHouseClient;
    private final ClickHouseMappingContext mappingContext;

    public ClickHouseRepositoryFactory(Client clickHouseClient,
                                       ClickHouseMappingContext mappingContext) {
        this.clickHouseClient = clickHouseClient;
        this.mappingContext = mappingContext;
    }

    /**
     * Возвращает реализацию интерфейса-репозитория через JDK Proxy.
     *
     * @param repositoryInterface класс-обзор интерфейса репозитория
     * @param <T>                 тип репозитория
     * @return прокси-реализация интерфейса
     */
    @SuppressWarnings("unchecked")
    public <T> T getRepository(Class<T> repositoryInterface) {
        // 1) Собираем все методы интерфейса
        Method[] methods = repositoryInterface.getMethods();

        // 2) Создаём MethodInvoker-ы, передавая repositoryInterface
        Map<Method, MethodInvoker> invokers = MethodInvokerFactory.createInvokers(
                repositoryInterface,
                methods,
                clickHouseClient,
                mappingContext);

        // 3) Генерируем JDK-прокси с нашим обработчиком
        return (T) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class[]{repositoryInterface},
                new ClickHouseInvocationHandler(invokers)
        );
    }
}