package common.extensions;

import api.models.BaseModel;
import api.models.Link;
import api.requests.skeleton.requesters.DeleteRequester;
import api.requests.specs.RequestSpecs;
import api.utils.EndpointResolverUtils;
import common.annotations.AutoCleanup;
import common.annotations.SkipAutoCleanup;
import common.storages.EntityStorage;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;

import static api.requests.specs.ResponseSpecs.requestReturnsNoContent;

public class CleanupExtension implements AfterEachCallback {
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        boolean hasMethodAnnotation = context.getTestMethod()
                .map(method -> method.isAnnotationPresent(AutoCleanup.class))
                .orElse(false);

        boolean hasClassAnnotation = context.getTestClass()
                .map(clazz -> clazz.isAnnotationPresent(AutoCleanup.class))
                .orElse(false);

        boolean hasMethodAnnotationSkipAutoCleanup = context.getTestMethod()
                .map(method -> method.isAnnotationPresent(SkipAutoCleanup.class))
                .orElse(false);

        if ((hasClassAnnotation || hasMethodAnnotation) && !hasMethodAnnotationSkipAutoCleanup) {
            cleanup();
        } else {
            EntityStorage.clear();
        }
    }

    private void cleanup() {
        DeleteRequester deleter = new DeleteRequester(
                RequestSpecs.adminSpec(),
                requestReturnsNoContent()
        );

        EntityStorage.getEntities(BaseModel.class).forEach(entity -> {
            List<Link> links = EndpointResolverUtils.getLinks(entity);
            if (links != null) {
                String path = EndpointResolverUtils.resolveDeletePath(links);
                deleter.delete(path);
            }
        });

        EntityStorage.clear();
    }
}
