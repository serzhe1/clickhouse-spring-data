package io.clickhouse.springdata.core.clientcontext;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.ConnectionReuseStrategy;
import io.clickhouse.springdata.autoconfigure.SpringClickHouseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@ConditionalOnMissingBean
@RequiredArgsConstructor
public class ClientFactory {

    private final SpringClickHouseProperties properties;

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

    public Client build() {
        log.info("Initializing client");
        log.debug("client properties: {}", properties.toString());
        Client client =  builder().build();
        log.info("Successfully initialized client");
        return client;
    }
}