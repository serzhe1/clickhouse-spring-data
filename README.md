# clickhouse-spring-data
Java library for easier use of ClickHouse in Spring Framewokr

## application.yaml example
```yaml
spring:
  clickhouse-data:
    client:
      # General connection settings
      endpoint: http://localhost:8123
      username: "default"
      password: "secret"
      access-token: "123"

      # SSL and pool of connection
      use-ssl-authentication: false
      enable-connection-pool: true

      # Timeouts (Duration: строки типа "5s", "1m", "10m", "30s", "60s")
      connect-timeout: 5s
      connection-request-timeout: 1s
      socket-timeout: 30s
      connection-ttl: 10m
      keep-alive-timeout: 30s
      execution-timeout: 60s

      # Sockets settings
      socket-rcvbuf: 8192
      socket-sndbuf: 8192
      socket-keep-alive: true
      socket-tcp-no-delay: true
      socket-linger: 0

      # Compression settings
      compress-client-request: false
      compress-server-response: false
      use-http-compression: false
      lz4-uncompressed-buffer-size: 65536

      # Default database
      default-database: "default"

      # HTTP settings
      http-cookies-enabled: false
      http-header:
        Content-Type: "application/json"
        Accept: "application/json"

      # SSL settings
      ssl-trust-store-path: "/path/to/truststore.jks"
      ssl-trust-store-password: "truststorepassword"
      ssl-trust-store-type: "JKS"
      root-certificate: "123"
      client-certificate: "123"
      client-key: "123"

      # Timezone settings
      use-server-time-zone: false
      use-time-zone: "UTC"
      server-time-zone: "UTC"

      # Async and network buffers
      use-async-requests: true
      client-network-buffer-size: 8192

      # Retry settings
      max-retries: 3
      allow-binary-reader-to-reuse-buffers: false

      # Connection reuse strategy: (path to class)
      connection-reuse-strategy: "com.example.DefaultConnectionReuseStrategy"

      # Additional settings
      server-setting:
        max_execution_time: "60"
```