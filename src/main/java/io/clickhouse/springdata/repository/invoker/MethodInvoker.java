package io.clickhouse.springdata.repository.invoker;

import java.lang.reflect.Method;

/**
 * Метод-инвокер — отвечает за выполнение одного метода репозитория.
 */
public interface MethodInvoker {
    /**
     * Поддерживает ли этот инвокер заданный метод интерфейса.
     */
    boolean supports(Method method);

    /**
     * Выполнить метод с переданными аргументами.
     * @param args аргументы вызова
     * @return результат выполнения
     */
    Object invoke(Object[] args);
}
