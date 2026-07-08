package api.models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModelComparisonConfigLoader {

    private final Map<String, ComparisonRule> rules = new HashMap<>();

    public ModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found: " + configFile);
            }
            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                String[] target = props.getProperty(key).split(":", 2);
                if (target.length != 2) {
                    continue;
                }

                String responseClassName = target[0].trim();
                List<String> fields = Arrays.asList(target[1].split(";"));

                rules.put(key.trim(), new ComparisonRule(responseClassName, fields));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DTO comparison config", e);
        }
    }

    public ComparisonRule getRuleFor(Class<?> requestClass) {
        return rules.get(requestClass.getSimpleName());
    }

    public ComparisonRule getRuleFor(Class<?> requestClass, Class<?> responseClass) {
        String key = requestClass.getSimpleName() + "->" + responseClass.getSimpleName();
        ComparisonRule rule = rules.get(key);
        if (rule != null) {
            return rule;
        }

        return rules.get(requestClass.getSimpleName());
    }

    public static class ComparisonRule {
        private final String responseClassSimpleName;
        private final Map<String, String> fieldMappings;
        private final Map<String, ComparisonRule> nestedRules;
        private String nestedResponsePath;

        public ComparisonRule(String responseClassSimpleName, List<String> fieldPairs) {
            this.responseClassSimpleName = responseClassSimpleName;
            this.fieldMappings = new HashMap<>();
            this.nestedRules = new HashMap<>();

            for (String pair : fieldPairs) {
                if (pair.startsWith("@")) {
                    String[] nestedParts = pair.substring(1).split(":", 2);
                    if (nestedParts.length == 2) {
                        String[] pathParts = nestedParts[0].trim().split("=", 2);
                        String requestPath = pathParts[0].trim();
                        String responsePath = pathParts.length > 1 ? pathParts[1].trim() : "";

                        String[] nestedFieldPairs = nestedParts[1].split(",");
                        ComparisonRule nestedRule = new ComparisonRule(null, Arrays.asList(nestedFieldPairs));
                        nestedRule.nestedResponsePath = responsePath;
                        nestedRules.put(requestPath, nestedRule);
                    }
                } else {
                    String[] parts = pair.split("=");
                    if (parts.length == 2) {
                        fieldMappings.put(parts[0].trim(), parts[1].trim());
                    } else {
                        fieldMappings.put(pair.trim(), pair.trim());
                    }
                }
            }
        }

        public String getNestedResponsePath() {
            return nestedResponsePath;
        }

        public Map<String, ComparisonRule> getNestedRules() {
            return nestedRules;
        }

        public String getResponseClassSimpleName() {
            return responseClassSimpleName;
        }

        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }
    }

}
