package org.overbaard.review.tool.util.entity.json;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public abstract class EntityJsonFieldStrategy<T> {
    protected void initLazyFieldsToLoad(T entity) {
    }

    protected abstract boolean isVisible(Field field);
}
