package api.models.comparison;

import api.models.comparison.ModelComparisonConfigLoader.ComparisonRule;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ModelComparator {
    private ModelComparator() { }

    public static <A, B> ComparisonResult compareFields(A request, B response,
                                                        Map<String, String> fieldMappings,
                                                        Map<String, ComparisonRule> nestedRules) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
            String requestField = entry.getKey();
            String responseField = entry.getValue();

            Object value1 = getFieldValue(request, requestField);
            Object value2 = getFieldValue(response, responseField);

            if (!Objects.equals(String.valueOf(value1), String.valueOf(value2))) {
                mismatches.add(new Mismatch(requestField + " -> " + responseField, value1, value2));
            }
        }

        if (nestedRules != null) {
            for (Map.Entry<String, ComparisonRule> nestedEntry : nestedRules.entrySet()) {
                String path = nestedEntry.getKey();
                ComparisonRule nestedRule = nestedEntry.getValue();

                Object nestedRequest = getFieldValue(request, path);

                Object nestedResponse;
                String responsePath = nestedRule.getNestedResponsePath();
                if (responsePath != null && !responsePath.isEmpty()) {
                    nestedResponse = getFieldValue(response, responsePath);
                } else {
                    nestedResponse = response;
                }

                if (nestedRequest == null && nestedResponse == null) {
                    continue;
                }

                if (nestedRequest == null || nestedResponse == null) {
                    mismatches.add(new Mismatch(path, nestedRequest, nestedResponse));
                    continue;
                }

                ComparisonResult nestedResult = compareFields(nestedRequest, nestedResponse,
                        nestedRule.getFieldMappings(),
                        nestedRule.getNestedRules());

                for (Mismatch mismatch : nestedResult.getMismatches()) {
                    mismatches.add(new Mismatch(path + "." + mismatch.fieldName,
                            mismatch.expected,
                            mismatch.actual));
                }
            }
        }

        return new ComparisonResult(mismatches);
    }

    public static <A, B> ComparisonResult compareFields(A request, B response, Map<String, String> fieldMappings) {
        return compareFields(request, response, fieldMappings, null);
    }

    private static Object getFieldValue(Object obj, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Object current = obj;

        for (String fieldName : parts) {
            if (current == null) return null;

            if (current instanceof List) {
                List<?> list = (List<?>) current;
                if (list.isEmpty()) {
                    return list;
                }
                current = list.getFirst();
            }

            Class<?> clazz = current.getClass();
            boolean found = false;

            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    current = field.get(current);
                    found = true;
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access field: " + fieldName, e);
                }
            }

            if (!found) {
                throw new RuntimeException("Field not found: " + fieldName + " in class " + current.getClass().getName());
            }
        }

        return current;
    }

    public static class ComparisonResult {
        private final List<Mismatch> mismatches;

        public ComparisonResult(List<Mismatch> mismatches) {
            this.mismatches = mismatches;
        }

        public boolean isSuccess() {
            return mismatches.isEmpty();
        }

        public List<Mismatch> getMismatches() {
            return mismatches;
        }

        @Override
        public String toString() {
            if (isSuccess()) {
                return "All fields match.";
            }
            StringBuilder sb = new StringBuilder("Mismatched fields:\n");
            for (Mismatch m : mismatches) {
                sb.append("- ").append(m.fieldName)
                        .append(": expected=").append(m.expected)
                        .append(", actual=").append(m.actual).append("\n");
            }
            return sb.toString();
        }
    }

    public static class Mismatch {
        public final String fieldName;
        public final Object expected;
        public final Object actual;

        public Mismatch(String fieldName, Object expected, Object actual) {
            this.fieldName = fieldName;
            this.expected = expected;
            this.actual = actual;
        }
    }
}
