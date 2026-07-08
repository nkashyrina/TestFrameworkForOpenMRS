package api.utils;

import api.configs.Config;
import api.models.BaseModel;
import api.models.Link;

import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("unchecked")
public final class EndpointResolverUtils {

    private EndpointResolverUtils() {
    }

    public static List<Link> getLinks(BaseModel entity) {
        try {
            Method getLinks = entity.getClass().getMethod("getLinks");
            return (List<Link>) getLinks.invoke(entity);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get links from " + entity.getClass().getSimpleName(), e);
        }
    }

    public static String resolveDeletePath(List<Link> links) {
        if (links == null || links.isEmpty()) {
            throw new IllegalStateException("Entity has no links");
        }

        String selfLink = links.stream()
                .filter(link -> "self".equals(link.getRel()))
                .findFirst()
                .map(Link::getUri)
                .orElseThrow(() -> new IllegalStateException("No self link found"));

        String prefix = Config.getProperty(Config.API_FULL_PREFIX_CONST);
        if (!selfLink.startsWith(prefix)) {
            throw new IllegalStateException("Self link doesn't start with expected prefix: " + selfLink);
        }

        return selfLink.substring(prefix.length());
    }
}
