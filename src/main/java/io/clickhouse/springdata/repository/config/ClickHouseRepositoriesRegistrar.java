package io.clickhouse.springdata.repository.config;

import io.clickhouse.springdata.repository.annotation.EnableClickHouseRepositories;
import io.clickhouse.springdata.repository.core.ClickHouseRepositoryMarker;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.util.Map;
import java.util.Set;

public class ClickHouseRepositoriesRegistrar
        implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry) {
        // 1) Читаем basePackages из @EnableClickHouseRepositories
        Map<String, Object> attrs =
                metadata.getAnnotationAttributes(
                        EnableClickHouseRepositories.class.getName());
        String[] basePackages = (String[]) attrs.get("basePackages");

        // 2) Если не заданы — по умолчанию берём пакет класса с аннотацией
        if (basePackages == null || basePackages.length == 0) {
            String pkg = ((StandardAnnotationMetadata) metadata)
                    .getIntrospectedClass()
                    .getPackage()
                    .getName();
            basePackages = new String[]{ pkg };
        }

        // 3) Сканируем через Reflections
        //    Scanners.SubTypes ищет все подклассы/подинтерфейсы маркера
        Reflections reflections = new Reflections(
                basePackages,
                Scanners.SubTypes.filterResultsBy(s -> true)
        );

        Set<Class<? extends ClickHouseRepositoryMarker>> repos =
                reflections.getSubTypesOf(ClickHouseRepositoryMarker.class);

        // 4) Для каждого найденного интерфейса регистрируем FactoryBean
        for (Class<?> repoIface : repos) {
            String beanName = repoIface.getSimpleName() + "FactoryBean";
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(
                            "io.clickhouse.springdata.repository.factory.ClickHouseRepositoryFactoryBean"
                    );
            // Передаём fully-qualified имя интерфейса в конструктор
            builder.addConstructorArgValue(repoIface.getName());
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        }
    }
}