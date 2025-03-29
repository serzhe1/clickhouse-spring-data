package io.clickhouse.springdata.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation to mark an entity as representing a ClickHouse table.
 * <p>
 * This annotation is used to specify the name of the table that corresponds to the annotated class.
 * It is processed by the ClickHouse table registration mechanism (for example, by a BeanPostProcessor)
 * to automatically register the table schema with the ClickHouse client.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @Table(name = "person")
 * public class PersonEntity {
 *     private Long id;
 *     private String firstName;
 *     private String lastName;
 * }
 * }
 * </pre>
 * </p>
 *
 * @author
 *         Sergei Ladygin
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * The name of the ClickHouse table associated with the annotated entity.
     *
     * @return the table name
     */
    String name();
}