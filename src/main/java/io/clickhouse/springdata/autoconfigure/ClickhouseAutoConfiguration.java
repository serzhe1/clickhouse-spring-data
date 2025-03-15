package io.clickhouse.springdata.autoconfigure;

import io.clickhouse.springdata.core.clientcontext.ClientConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(SpringClickHouseProperties.class)
@ConditionalOnProperty(prefix = "clickhouse-data", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(ClientConfiguration.class)
@ComponentScan(basePackages = "io.clickhouse.springdata")
public class ClickhouseAutoConfiguration {
}
