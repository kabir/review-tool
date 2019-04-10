package org.overbaard.review.tool.util.entity.json;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SelectiveFieldStrategy<T> extends EntityJsonFieldStrategy<T> {
    private final Map<String, Consumer<T>> lazyFields;

    public SelectiveFieldStrategy(
            Map<String, Consumer<T>> lazyFields) {
        this.lazyFields = lazyFields;
    }

    @Override
    protected void initLazyFieldsToLoad(T entity) {
        for (Map.Entry<String, Consumer<T>> entry : lazyFields.entrySet()) {
            entry.getValue().accept(entity);
        }
    }

    @Override
    protected boolean isVisible(Field field) {
        if (lazyFields.containsKey(field.getName())) {
            return true;
        }
        return false;
    }
}
