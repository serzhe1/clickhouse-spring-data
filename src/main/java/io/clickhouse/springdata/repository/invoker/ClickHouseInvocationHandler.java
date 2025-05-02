package io.clickhouse.springdata.repository.invoker;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ClickHouseInvocationHandler implements InvocationHandler {
    private final Map<Method, MethodInvoker> invokers;

    public ClickHouseInvocationHandler(Map<Method, MethodInvoker> invokers) {
        this.invokers = invokers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInvoker inv = invokers.get(method);
        if (inv == null) {
            throw new UnsupportedOperationException("Method not supported: " + method);
        }
        return inv.invoke(args);
    }
}