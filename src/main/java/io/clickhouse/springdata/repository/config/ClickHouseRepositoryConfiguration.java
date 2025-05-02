package io.clickhouse.springdata.repository.config;

import com.clickhouse.client.api.Client;
import io.clickhouse.springdata.repository.core.ClickHouseMappingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ClickHouseRepositoryConfiguration implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * @param basePackages сюда передаём пакеты, в которых сканить @Table.
     *                     Можно получить их из @EnableClickHouseRepositories attrs,
     *                     либо из application.properties (например,
     *                     spring.clickhouse.entity-packages).
     */
    @Bean
    public ClickHouseMappingContext clickHouseMappingContext(
            Client client,
            ResourceLoader resourceLoader,
            @Value("${spring.clickhouse-data.entity-packages}") List<String> basePackages
    ) {
        ClickHouseMappingContext ctx = new ClickHouseMappingContext(basePackages, client);
        ctx.setResourceLoader(resourceLoader);
        return ctx;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}