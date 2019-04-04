package org.overbaard.review.tool.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class LazyEntityFieldsStrategy implements PropertyVisibilityStrategy {

    private final boolean eagerLoaded;
    private Set<String> lazyFields;

    public LazyEntityFieldsStrategy(boolean eagerLoaded, String...lazyFields) {
        this.eagerLoaded = eagerLoaded;
        this.lazyFields = new HashSet<>(Arrays.asList(lazyFields));
    }

    @Override
    public boolean isVisible(Field field) {
        if (eagerLoaded) {
            return true;
        } else {
            return !lazyFields.contains(field.getName());
        }

    }

    @Override
    public boolean isVisible(Method method) {
        return false;
    }

}