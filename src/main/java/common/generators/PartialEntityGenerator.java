package common.generators;

import com.github.curiousoddman.rgxgen.RgxGen;
import common.annotations.GeneratingRule;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class PartialEntityGenerator {
    private static final Random RANDOM = new Random();

    private PartialEntityGenerator() {
    }

    public static <T> T generate(Class<T> clazz, Set<String> fieldsToGenerate) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : getAllFields(clazz)) {
                if (!fieldsToGenerate.contains(field.getName())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = generateValueForField(field);
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate partial entity", e);
        }
    }

    public static <T> T generate(Class<T> clazz, String... fields) {
        return generate(clazz, new HashSet<>(Arrays.asList(fields)));
    }

    private static Object generateValueForField(Field field) {
        Class<?> type = field.getType();

        GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
        if (rule != null) {
            return generateFromRegex(rule.regex(), type);
        }

        if (type.equals(List.class)) {
            return generateRandomList(field);
        }

        return generateRandomValue(type);
    }

    private static Object generateRandomValue(Class<?> type) {
        if (type.equals(String.class)) {
            return UUID.randomUUID().toString().substring(0, 8);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return RANDOM.nextInt(1000);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return RANDOM.nextLong();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return RANDOM.nextDouble() * 100;
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return RANDOM.nextBoolean();
        } else {
            return generate(type, getAllFieldNames(type));
        }
    }

    private static List<Object> generateRandomList(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type actualType = pt.getActualTypeArguments()[0];

            if (actualType instanceof Class<?> elementType) {
                List<Object> list = new ArrayList<>();

                list.add(generateRandomValue(elementType));

                return list;
            }
        }
        return Collections.emptyList();
    }

    private static Object generateFromRegex(String regex, Class<?> type) {
        RgxGen rgxGen = new RgxGen(regex);
        String result = rgxGen.generate();
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(result);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(result);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(result);
        }
        return result;
    }

    private static Set<String> getAllFieldNames(Class<?> clazz) {
        Set<String> names = new HashSet<>();
        for (Field field : getAllFields(clazz)) {
            names.add(field.getName());
        }
        return names;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
