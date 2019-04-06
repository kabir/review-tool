package org.overbaard.review.tool.util;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SimpleJsonValue<T extends Object> {
    private T value;

    public SimpleJsonValue() {
    }

    public SimpleJsonValue(T siteAdmin) {
        this.value = siteAdmin;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
