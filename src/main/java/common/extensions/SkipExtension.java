package common.extensions;

import common.annotations.Skip;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SkipExtension implements ExecutionCondition {

    private static final String SKIP_PROPERTY = "skip.tests";

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        boolean hasMethodAnnotation = context.getTestMethod()
                .map(method -> method.isAnnotationPresent(Skip.class))
                .orElse(false);

        boolean hasClassAnnotation = context.getTestClass()
                .map(clazz -> clazz.isAnnotationPresent(Skip.class))
                .orElse(false);

        if (!hasMethodAnnotation && !hasClassAnnotation) {
            return ConditionEvaluationResult.enabled("@Skip not present");
        }

        boolean shouldSkip = Boolean.parseBoolean(System.getProperty(SKIP_PROPERTY, "true"));

        if (!shouldSkip) {
            return ConditionEvaluationResult.enabled(
                    "Override via -D" + SKIP_PROPERTY + "=false"
            );
        }

        String reason = getSkipReason(context);
        return ConditionEvaluationResult.disabled(
                reason.isEmpty() ? "Skipped via @Skip" : "Skipped via @Skip: " + reason
        );
    }

    private String getSkipReason(ExtensionContext context) {
        return context.getTestMethod()
                .map(method -> method.getAnnotation(Skip.class))
                .filter(ann -> !ann.reason().isEmpty())
                .map(Skip::reason)
                .or(() -> context.getTestClass()
                        .map(clazz -> clazz.getAnnotation(Skip.class))
                        .map(Skip::reason))
                .orElse("");
    }
}
