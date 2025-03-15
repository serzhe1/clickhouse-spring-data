package io.clickhouse.springdata.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@Data
@ConfigurationProperties(prefix = "spring.clickhouse-data")
public class SpringClickHouseProperties {
    private ClientProperties client;

    @Data
    public static class ClientProperties {
        private String endpoint;
        private Optional<String> username = Optional.empty();
        private Optional<String> password = Optional.empty();
    }
}
