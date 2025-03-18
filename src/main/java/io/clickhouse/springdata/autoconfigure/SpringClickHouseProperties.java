package io.clickhouse.springdata.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * @author Sergei Ladygin
 * General settings and parameters for {@link com.clickhouse.client.api.Client} configuration
 * @see <a href="https://clickhouse.com/docs/integrations/language-clients/java/client#configuration">official documentation</a>
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.clickhouse-data")
public class SpringClickHouseProperties {
    /**
     * General connection settings
     */
    private String endpoint;
    private String username;
    private String password;
    private String accessToken;

    /**
     * SSL and pool connections
     */
    private Boolean useSSLAuthentication;
    private Boolean enableConnectionPool;

    /**
     * Timeouts
     * examples: "5s", "1m"
     */
    private Duration connectTimeout;
    private Duration connectionRequestTimeout;
    private Duration socketTimeout;
    private Duration connectionTTL;
    private Duration keepAliveTimeout;
    private Duration executionTimeout;

    // Параметры сокетов
    /**
     * Sockets parameters
     */
    private Long socketRcvbuf;
    private Long socketSndbuf;
    private Boolean socketKeepAlive;
    private Boolean socketTcpNoDelay;
    private Integer socketLinger;

    /**
     * Compression settings
     */
    private Boolean compressClientRequest;
    private Boolean compressServerResponse;
    private Boolean useHttpCompression;

    /**
     * Other
     */
    private Integer lz4UncompressedBufferSize;
    private String defaultDatabase;

    /**
     * HTTP settings
     */
    private Boolean httpCookiesEnabled;
    private Map<String, String> httpHeaders;

    /**
     * SSL settings
     */
    private String sslTrustStorePath;
    private String sslTrustStorePassword;
    private String sslTrustStoreType;
    private String rootCertificate;
    private String clientCertificate;
    private String clientKey;

    /**
     * Timezone settings
     */
    private Boolean useServerTimeZone;
    private String useTimeZone;
    private String serverTimeZone;

    /**
     * Async and buffers
     */
    private Boolean useAsyncRequests;
    private Integer clientNetworkBufferSize;

    /**
     * Retry settings
     */
    private Integer maxRetries;
    private Boolean allowBinaryReaderToReuseBuffers;

    /**
     * Enum strategy name
     */
    private String connectionReuseStrategy;

    /**
     * Server settings (Only first key and value will be used)
     */
    private Map<String, String> serverSetting;
}
