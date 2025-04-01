# ClickHouse Spring Data

Spring Boot auto-configuration and integration with [ClickHouse](https://clickhouse.com) using modern Java client.

## Usage Example

To start using **clickhouse-spring-data**, follow these simple steps:

---

### 1. Enable ClickHouse integration in your Spring Boot application

Annotate your main Spring Boot application class with `@EnableClickHouseData` to activate table scanning and schema registration.

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.clickhouse.springdata.annotation.EnableClickHouseData;

@SpringBootApplication
@EnableClickHouseData
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

### 2. Mark your ClickHouse entity classes

Use @Table annotation to mark your classes as ClickHouse tables.

```java
import io.clickhouse.springdata.annotation.Table;

@Table(name = "person")
public class PersonEntity {
    private Long id;
    private String name;

}
```

### 3. Inject and use the ClickHouse Client

```java
import com.clickhouse.client.api.Client;
import org.springframework.stereotype.Service;
import tables.PersonEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PersonService {

    private final Client client;

    public PersonService(Client client) {
        this.client = client;
    }

    public void listAll() throws ExecutionException, InterruptedException {
        String query = "SELECT * FROM person";
        List<PersonEntity> people = client.queryAll(query, PersonEntity.class, client.getTableSchema("person"));
        people.forEach(System.out::println);
    }
}
```
You can use methods like queryAll(...), queryOne(...), etc., to work with your registered entities.
## Spring application.yaml config
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

      # Timeouts (Duration: "5s", "1m", "10m", "30s", "60s")
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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.