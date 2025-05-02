// ClickHouseRepositoryFactoryBean.java
package io.clickhouse.springdata.repository.factory;

import io.clickhouse.springdata.repository.core.ClickHouseMappingContext;
import io.clickhouse.springdata.repository.core.ClickHouseRepositoryMarker;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.clickhouse.client.api.Client;
import org.springframework.beans.BeansException;

/**
 * FactoryBean для создания прокси-реализаций ClickHouse-репозиториев.
 *
 * @param <T>  тип интерфейса репозитория
 * @param <ID> тип идентификатора (необязателен в базовом API)
 */
public class ClickHouseRepositoryFactoryBean<T extends ClickHouseRepositoryMarker, ID>
        implements FactoryBean<T>, ApplicationContextAware {

    private final Class<T> repositoryInterface;
    private Client clickHouseClient;
    private ClickHouseMappingContext mappingContext;
    private ApplicationContext applicationContext;

    public ClickHouseRepositoryFactoryBean(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Autowired
    public void setClickHouseClient(Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    @Autowired
    public void setMappingContext(ClickHouseMappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    @Override
    public T getObject() throws Exception {
        // Создание фабрики и получение прокси-репозитория
        ClickHouseRepositoryFactory factory =
                new ClickHouseRepositoryFactory(clickHouseClient, mappingContext);
        return factory.getRepository(repositoryInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}