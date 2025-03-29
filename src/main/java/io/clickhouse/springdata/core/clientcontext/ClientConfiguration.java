package io.clickhouse.springdata.core.clientcontext;

import com.clickhouse.client.api.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sergei Ladygin
 * Configuration {@link Client} class
 * @since 1.0.0
 */
@Configuration
public class ClientConfiguration {
    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client client(ClientFactory clientFactory) {
        return clientFactory.build();
    }
}
