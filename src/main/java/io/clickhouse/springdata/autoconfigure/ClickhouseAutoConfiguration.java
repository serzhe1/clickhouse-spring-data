package io.clickhouse.springdata.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SpringClickClientHouseProperties.class)
@ConditionalOnProperty(prefix = "clickhouse-data", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "io.clickhouse.springdata")
public class ClickhouseAutoConfiguration {
}
