package io.clickhouse.springdata.core.clientcontext;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.ConnectionReuseStrategy;
import io.clickhouse.springdata.autoconfigure.SpringClickClientHouseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

/**
 * Factory for creating and configuring instances of {@link Client} for connecting to ClickHouse.
 * <p>
 * This factory reads settings from {@link SpringClickClientHouseProperties}, which can be defined via
 * {@code application.yaml} or other Spring Boot configuration sources.
 * </p>
 * <p>
 * During client creation, the builder is configured by calling various methods to set parameters such as:
 * <ul>
 *   <li>Basic connection parameters (endpoint, username, password, and access token)</li>
 *   <li>Timeouts for connection, connection request, socket, connection TTL, keep-alive, and execution</li>
 *   <li>Socket settings (buffer sizes, keep-alive, TCP no delay, and linger)</li>
 *   <li>Compression settings for client requests, server responses, and HTTP compression</li>
 *   <li>Default database selection</li>
 *   <li>Connection reuse strategy (instantiated via reflection if a fully-qualified class name is provided)</li>
 *   <li>SSL authentication and connection pooling options</li>
 *   <li>HTTP cookies, SSL trust store settings, and certificates</li>
 *   <li>Time zone configuration</li>
 *   <li>Asynchronous request handling and client network buffer size</li>
 *   <li>Retry settings and buffer reuse</li>
 *   <li>Additional server settings and HTTP headers</li>
 * </ul>
 * </p>
 * <p>
 * If certain properties are not set, default values provided by the underlying client will be used.
 * </p>
 *
 * @author Sergei Ladygin
 * @since 1.0.0
 */
@Slf4j
@Component
public class ClientFactory {

    private final SpringClickClientHouseProperties properties;

    @Autowired
    public ClientFactory(SpringClickClientHouseProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates and configures a {@link Client.Builder} instance based on the properties defined in
     * {@link SpringClickClientHouseProperties}.
     * <p>
     * The method sets:
     * <ul>
     *   <li>Basic connection parameters: endpoint, username, password, and optionally access token.</li>
     *   <li>Timeouts: connect timeout, connection request timeout, socket timeout, and connection TTL, all converted to nanoseconds.</li>
     *   <li>Socket configurations: receive buffer size, send buffer size, keep-alive, TCP no delay, and linger settings.</li>
     *   <li>Compression settings: client request compression, server response compression, HTTP compression, and LZ4 uncompressed buffer size.</li>
     *   <li>Default database configuration.</li>
     *   <li>Connection reuse strategy: if provided, it is instantiated via reflection.</li>
     *   <li>SSL authentication and connection pooling options.</li>
     *   <li>Keep-alive and execution timeouts.</li>
     *   <li>HTTP and SSL trust store settings.</li>
     *   <li>Certificate settings for root, client certificate, and client key.</li>
     *   <li>Time zone configurations: use server time zone, use time zone, and server time zone.</li>
     *   <li>Asynchronous request flag and client network buffer size.</li>
     *   <li>Retry settings and buffer reuse option.</li>
     *   <li>Additional server settings (the first found entry is applied) and HTTP headers.</li>
     * </ul>
     * </p>
     *
     * @return a fully configured {@link Client.Builder} ready to build a {@link Client} instance.
     */
    public Client.Builder builder() {
        log.debug("Initializing client builder");
        Client.Builder builder = new Client.Builder();

        builder.addEndpoint(properties.getEndpoint());
        builder.setUsername(properties.getUsername());
        builder.setPassword(properties.getPassword());

        if (properties.getAccessToken() != null && !properties.getAccessToken().isEmpty()) {
            builder.setAccessToken(properties.getAccessToken());
        }

        if (properties.getConnectTimeout() != null) {
            builder.setConnectTimeout(properties.getConnectTimeout().getNano());
        }
        if (properties.getConnectionRequestTimeout() != null) {
            builder.setConnectionRequestTimeout(properties.getConnectionRequestTimeout().getNano(), ChronoUnit.NANOS);
        }
        if (properties.getSocketTimeout() != null) {
            builder.setSocketTimeout(properties.getSocketTimeout().getNano(), ChronoUnit.NANOS);
        }
        if (properties.getConnectionTTL() != null) {
            builder.setConnectionTTL(properties.getConnectionTTL().getNano(), ChronoUnit.NANOS);
        }

        if (properties.getSocketRcvbuf() != null) {
            builder.setSocketRcvbuf(properties.getSocketRcvbuf());
        }
        if (properties.getSocketSndbuf() != null) {
            builder.setSocketSndbuf(properties.getSocketSndbuf());
        }
        if (properties.getSocketKeepAlive() != null) {
            builder.setSocketKeepAlive(properties.getSocketKeepAlive());
        }
        if (properties.getSocketTcpNoDelay() != null) {
            builder.setSocketTcpNodelay(properties.getSocketTcpNoDelay());
        }
        if (properties.getSocketLinger() != null) {
            builder.setSocketLinger(properties.getSocketLinger());
        }

        if (properties.getCompressClientRequest() != null) {
            builder.compressClientRequest(properties.getCompressClientRequest());
        }
        if (properties.getCompressServerResponse() != null) {
            builder.compressServerResponse(properties.getCompressServerResponse());
        }
        if (properties.getUseHttpCompression() != null) {
            builder.useHttpCompression(properties.getUseHttpCompression());
        }
        if (properties.getLz4UncompressedBufferSize() != null) {
            builder.setLZ4UncompressedBufferSize(properties.getLz4UncompressedBufferSize());
        }

        if (properties.getDefaultDatabase() != null) {
            builder.setDefaultDatabase(properties.getDefaultDatabase());
        }

        if (properties.getConnectionReuseStrategy() != null && !properties.getConnectionReuseStrategy().isEmpty()) {
            try {
                Class<?> clazz = Class.forName(properties.getConnectionReuseStrategy());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                builder.setConnectionReuseStrategy((ConnectionReuseStrategy) instance);
            } catch (Exception e) {
                log.error("Failed to set connectionReuseStrategy, using default. Exception: {}", e.getMessage());
            }
        }

        if (properties.getUseSSLAuthentication() != null) {
            builder.useSSLAuthentication(properties.getUseSSLAuthentication());
        }
        if (properties.getEnableConnectionPool() != null) {
            builder.enableConnectionPool(properties.getEnableConnectionPool());
        }

        if (properties.getKeepAliveTimeout() != null) {
            builder.setKeepAliveTimeout(properties.getKeepAliveTimeout().getNano(), ChronoUnit.NANOS);
        }
        if (properties.getExecutionTimeout() != null) {
            builder.setExecutionTimeout(properties.getExecutionTimeout().getNano(), ChronoUnit.NANOS);
        }

        if (properties.getHttpCookiesEnabled() != null) {
            builder.setHttpCookiesEnabled(properties.getHttpCookiesEnabled());
        }
        if (properties.getSslTrustStorePath() != null) {
            builder.setSSLTrustStore(properties.getSslTrustStorePath());
        }
        if (properties.getSslTrustStorePassword() != null) {
            builder.setSSLTrustStorePassword(properties.getSslTrustStorePassword());
        }
        if (properties.getSslTrustStoreType() != null) {
            builder.setSSLTrustStoreType(properties.getSslTrustStoreType());
        }
        if (properties.getRootCertificate() != null && !properties.getRootCertificate().isEmpty()) {
            builder.setRootCertificate(properties.getRootCertificate());
        }
        if (properties.getClientCertificate() != null && !properties.getClientCertificate().isEmpty()) {
            builder.setClientCertificate(properties.getClientCertificate());
        }
        if (properties.getClientKey() != null && !properties.getClientKey().isEmpty()) {
            builder.setClientKey(properties.getClientKey());
        }

        if (properties.getUseServerTimeZone() != null) {
            builder.useServerTimeZone(properties.getUseServerTimeZone());
        }
        if (properties.getUseTimeZone() != null) {
            builder.useTimeZone(properties.getUseTimeZone());
        }
        if (properties.getServerTimeZone() != null) {
            builder.useTimeZone(properties.getServerTimeZone());
        }

        if (properties.getUseAsyncRequests() != null) {
            builder.useAsyncRequests(properties.getUseAsyncRequests());
        }
        if (properties.getClientNetworkBufferSize() != null) {
            builder.setClientNetworkBufferSize(properties.getClientNetworkBufferSize());
        }

        if (properties.getMaxRetries() != null) {
            builder.setMaxRetries(properties.getMaxRetries());
        }
        if (properties.getAllowBinaryReaderToReuseBuffers() != null) {
            builder.allowBinaryReaderToReuseBuffers(properties.getAllowBinaryReaderToReuseBuffers());
        }

        if (properties.getServerSetting() != null && !properties.getServerSetting().isEmpty()) {
            properties.getServerSetting().keySet().stream()
                    .findFirst()
                    .ifPresent(k -> builder.serverSetting(k, properties.getServerSetting().get(k)));
        }

        if (properties.getHttpHeaders() != null && !properties.getHttpHeaders().isEmpty()) {
            builder.httpHeaders(properties.getHttpHeaders());
        }

        return builder;
    }

    /**
     * Builds and initializes a {@link Client} instance using the settings defined in {@link SpringClickClientHouseProperties}.
     * <p>
     * This method first obtains a configured {@link Client.Builder} by calling {@link #builder()}, then builds
     * the final {@link Client} instance. It also logs the properties used for configuration for debugging purposes.
     * </p>
     *
     * @return a fully initialized {@link Client} ready for connecting to ClickHouse.
     */
    public Client build() {
        log.info("Initializing client");
        log.debug("Client properties: {}", properties.toString());
        Client client = builder().build();
        log.info("Successfully initialized client");
        return client;
    }
}