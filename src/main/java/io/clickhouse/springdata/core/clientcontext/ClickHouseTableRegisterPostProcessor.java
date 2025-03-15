package io.clickhouse.springdata.core.clientcontext;

import com.clickhouse.client.api.Client;
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
