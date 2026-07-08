package common.storages;

import api.models.BaseModel;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class EntityStorage {
    private static final ThreadLocal<EntityStorage> INSTANCE = ThreadLocal.withInitial(EntityStorage::new);

    private final Map<Class<?>, List<BaseModel>> storage = new LinkedHashMap<>();

    private EntityStorage() {
    }

    public static void add(BaseModel entity) {
        INSTANCE.get().storage
                .computeIfAbsent(entity.getClass(), k -> new ArrayList<>())
                .add(entity);
    }

    public static <T extends BaseModel> List<T> getEntities(Class<T> clazz) {
        return INSTANCE.get().storage.values().stream()
                .flatMap(List::stream)
                .filter(clazz::isInstance)
                .map(entity -> (T) entity)
                .collect(Collectors.toList());
    }

    public static void clear() {
        INSTANCE.get().storage.clear();
    }

    public static void clear(Class<?> clazz) {
        INSTANCE.get().storage.remove(clazz);
    }
}
