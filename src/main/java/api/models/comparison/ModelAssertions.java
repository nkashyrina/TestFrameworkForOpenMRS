package api.models.comparison;

import org.assertj.core.api.AbstractAssert;

public final class ModelAssertions extends AbstractAssert<ModelAssertions, Object> {

    private final Object request;
    private final Object response;

    private ModelAssertions(Object request, Object response) {
        super(request, ModelAssertions.class);
        this.request = request;
        this.response = response;
    }

    public static ModelAssertions assertThatModels(Object request, Object response) {
        return new ModelAssertions(request, response);
    }

    public ModelAssertions match() {
        ModelComparisonConfigLoader configLoader = new ModelComparisonConfigLoader("model-comparison.properties");
        ModelComparisonConfigLoader.ComparisonRule rule = configLoader.getRuleFor(request.getClass(), response.getClass());

        if (rule != null) {
            ModelComparator.ComparisonResult result = ModelComparator.compareFields(
                    request,
                    response,
                    rule.getFieldMappings(),
                    rule.getNestedRules()
            );

            if (!result.isSuccess()) {
                failWithMessage("Model comparison failed with mismatched fields:\n%s", result);
            }
        } else {
            failWithMessage("No comparison rule found for %s -> %s",
                    request.getClass().getSimpleName(), response.getClass().getSimpleName());
        }

        return this;
    }
}
