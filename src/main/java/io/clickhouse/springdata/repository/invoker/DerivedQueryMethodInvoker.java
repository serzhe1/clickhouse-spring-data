package io.clickhouse.springdata.repository.invoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.metadata.TableSchema;
import io.clickhouse.springdata.repository.core.ClickHouseMappingContext;

public class DerivedQueryMethodInvoker implements MethodInvoker {
    private static final String FIND_BY = "findBy";
    private static final String COUNT_BY = "countBy";
    private static final String AND = "And";

    private final Method method;
    private final Client client;
    private final ClickHouseMappingContext context;
    private final Class<?> entityClass;
    private final boolean countQuery;
    private final String tableName;
    private final List<String> props = new ArrayList<>();
    private final List<Operator> ops = new ArrayList<>();

    public DerivedQueryMethodInvoker(Method method,
                                     Class<?> repositoryInterface,
                                     Client client,
                                     ClickHouseMappingContext context) {
        this.method = method;
        this.client = client;
        this.context = context;
        this.entityClass = resolveEntityClass(repositoryInterface);
        this.tableName = context.getTableName(entityClass);

        String name = method.getName();
        if (name.startsWith(COUNT_BY)) {
            this.countQuery = true;
            name = name.substring(COUNT_BY.length());
        } else if (name.startsWith(FIND_BY)) {
            this.countQuery = false;
            name = name.substring(FIND_BY.length());
        } else {
            throw new IllegalArgumentException("Not a derived query: " + method);
        }

        parseName(name);
        if (method.getParameterCount() != props.size()) {
            throw new IllegalArgumentException(
                    "Parameters mismatch: method takes " + method.getParameterCount()
                            + ", but derived props count is " + props.size());
        }
    }

    @Override
    public boolean supports(Method m) {
        return method.equals(m);
    }

    @Override
    public Object invoke(Object[] args) {
        // 1) Собираем базовый SELECT
        String select = countQuery
                ? "SELECT count(*)"
                : "SELECT " + String.join(
                ", ",
                context.getAllColumns(entityClass).stream()
                        .map(this::toSnakeCase)
                        .toList()
        );

        // 2) WHERE c параметрами-литералами
        StringJoiner where = new StringJoiner(" AND ", " WHERE ", "");
        for (int i = 0; i < props.size(); i++) {
            String column = context.getColumnForField(entityClass, props.get(i));
            Operator op = ops.get(i);
            Object arg = args[i];
            String literal = toLiteral(arg);
            where.add(toSnakeCase(column) + " " + op.sql + " " + literal);
        }
        String sql = select + " FROM " + tableName + where;

        TableSchema schema = client.getTableSchema(tableName);

        if (countQuery) {
            List<Long> counts = client
                    .queryAll(sql, Long.class, schema);
            return counts.isEmpty() ? 0L : counts.get(0);
        }

        return client.queryAll(sql, entityClass, schema);
    }

    private String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Вставляем подчёркивание между строчными и заглавными буквами: "camelCase" → "camel_Case"
        String step1 = input.replaceAll("([a-z])([A-Z])", "$1_$2");
        // Вставляем подчёркивание между последовательностями заглавных букв и последующим строчным: "HTMLParser" → "HTML_Parser"
        String step2 = step1.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2");
        // Переводим всё в нижний регистр
        return step2.toLowerCase();
    }

    /**
     * Преобразует Java-объект в SQL-литерал
     */
    private String toLiteral(Object arg) {
        if (arg == null) {
            return "NULL";
        }
        if (arg instanceof String) {
            String s = (String) arg;
            // экранируем одиночные кавычки внутри строки
            s = s.replace("'", "\\'");
            return "'" + s + "'";
        }
        if (arg instanceof Number || arg instanceof Boolean) {
            return arg.toString();
        }
        // Для дат/UUID и прочих типов можно добавить дополнительные ветки
        // По умолчанию приводим к строке и оборачиваем в кавычки
        String s = arg.toString().replace("'", "\\'");
        return "'" + s + "'";
    }

    private void parseName(String remainder) {
        String[] parts = remainder.split(AND);
        for (String part : parts) {
            if (part.endsWith("GreaterThan")) {
                props.add(decapitalize(part.replaceFirst("GreaterThan$", "")));
                ops.add(Operator.GT);
            } else if (part.endsWith("LessThan")) {
                props.add(decapitalize(part.replaceFirst("LessThan$", "")));
                ops.add(Operator.LT);
            } else if (part.endsWith("Like")) {
                props.add(decapitalize(part.replaceFirst("Like$", "")));
                ops.add(Operator.LIKE);
            } else {
                props.add(decapitalize(part));
                ops.add(Operator.EQ);
            }
        }
    }

    private Class<?> resolveEntityClass(Class<?> repoInterface) {
        return (Class<?>) ((java.lang.reflect.ParameterizedType)
                repoInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private String decapitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private enum Operator {
        EQ("=", 1), GT(">", 1), LT("<", 1), LIKE("LIKE", 1);

        final String sql;
        final int paramsCount;

        Operator(String sql, int paramsCount) {
            this.sql = sql;
            this.paramsCount = paramsCount;
        }
    }
}