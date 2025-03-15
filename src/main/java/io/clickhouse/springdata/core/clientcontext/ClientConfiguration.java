package io.clickhouse.springdata.core.clientcontext;

import com.clickhouse.client.api.Client;
import io.clickhouse.springdata.autoconfigure.SpringClickHouseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Client client(SpringClickHouseProperties properties) {
        return new Client.Builder()
                .addEndpoint(properties.getClient().getEndpoint())
                .setUsername(properties.getClient().getUsername().orElse("default"))
                .setPassword(properties.getClient().getPassword().orElse(""))
                .build();
    }
}
