package org.overbaard.review.tool.util.entity.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 * Utility to be able to choose more fine-grained, per use case what gets
 * serialized and not.
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class EntityJsonSerializer {

    public static <T> String toJson(List<T> list, EntityJsonFieldStrategy<T> strategy) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (T entry : list) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(toJson(entry, strategy));
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> String toJson(T entity, EntityJsonFieldStrategy<T> strategy) {
        JsonbConfig config = createConfig(entity, strategy);
        String s = toJson(entity, config);
        return s;
    }

    private static <T> JsonbConfig createConfig(T entity, EntityJsonFieldStrategy<T> strategy) {
        return new JsonbConfig()
                .withPropertyVisibilityStrategy(
                        new LazyEntityFieldsStrategy(entity, strategy));
    }

    private static <T> String toJson(T entity, JsonbConfig config) {
        String s = JsonbBuilder.newBuilder()
                .withConfig(config)
                .build()
                .toJson(entity);
        return s;
    }

    private static class LazyEntityFieldsStrategy<T> implements PropertyVisibilityStrategy {

        private final EntityJsonFieldStrategy<T> fieldStrategy;

        public LazyEntityFieldsStrategy(T entity, EntityJsonFieldStrategy<T> fieldStrategy) {
            this.fieldStrategy = fieldStrategy;
            fieldStrategy.initLazyFieldsToLoad(entity);
        }

        @Override
        public boolean isVisible(Field field) {
            if (!field.isAnnotationPresent(EntityJsonMaybeLazy.class)) {
                return true;
            }
            return fieldStrategy.isVisible(field);
        }

        @Override
        public boolean isVisible(Method method) {
            return false;
        }
    }

}
