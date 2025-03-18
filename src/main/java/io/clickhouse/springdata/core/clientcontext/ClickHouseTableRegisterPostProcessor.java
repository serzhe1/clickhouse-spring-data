package io.clickhouse.springdata.core.clientcontext;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.metadata.TableSchema;
import io.clickhouse.springdata.core.annotation.Table;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Post-processor that scans the application classpath for classes annotated with {@link Table}
 * and registers them with a ClickHouse {@link Client} instance.
 *
 * <p>
 * This BeanPostProcessor performs two main tasks:
 * <ol>
 *   <li>
 *     In {@code postProcessBeforeInitialization}, it searches for a bean annotated with
 *     {@link SpringBootApplication}. Upon finding such a bean, it extracts the base package from its class
 *     and initializes a {@link Reflections} scanner configured to detect all types annotated with {@code @Table}.
 *   </li>
 *   <li>
 *     In {@code postProcessAfterInitialization}, if the processed bean is an instance of {@link Client},
 *     it uses the pre-configured {@link Reflections} instance to obtain all classes marked with {@code @Table}.
 *     For each discovered table entity, it retrieves the table name from the annotation (or defaults to the simple class name)
 *     and calls {@link Client#register(Class, TableSchema)} to register the table schema with the client.
 *   </li>
 * </ol>
 * </p>
 *
 * <p>
 * This mechanism automates the registration of ClickHouse table schemas during application startup,
 * eliminating the need for manual configuration. It leverages the {@link Reflections} library for runtime
 * discovery of table entities.
 * </p>
 *
 * @author
 *         Sergei Ladygin
 * @since 1.0.0
 */
@Slf4j
@Component
public class ClickHouseTableRegisterPostProcessor implements BeanPostProcessor {
    private Reflections reflections;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(bean.getClass().getAnnotation(SpringBootApplication.class) != null) {
            String basePackage = bean.getClass().getPackageName();
            reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(basePackage))
                    .setScanners(Scanners.TypesAnnotated)
            );
        }

        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(reflections == null) {
            return bean;
        }

        if (bean instanceof Client client) {
            log.debug("Start register clickhouse tables");

            Set<Class<?>> tableClasses = reflections.getTypesAnnotatedWith(Table.class);

            for (Class<?> table : tableClasses) {
                Table tableAnnotation = table.getAnnotation(Table.class);
                String tableName = tableAnnotation.name() == null ? table.getSimpleName() : tableAnnotation.name();
                log.debug("Registering clickhouse table {}", tableName);
                client.register(table, client.getTableSchema(tableName));
            }
            log.debug("End register clickhouse tables");
        }
        return bean;
    }
}
