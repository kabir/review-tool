package org.overbaard.review.tool.util;

import java.util.Map;
import java.util.function.Function;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.hibernate.LazyInitializationException;

/**
 * Base class for custom serializers to have the simplicity of automagic serialization
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public abstract class EntitySerializer<T> implements JsonbSerializer<T> {
    private final Map<String, Function<T, ?>> suppliers;

    protected EntitySerializer(Map<String, Function<T, ?>> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public final void serialize(T t, JsonGenerator generator, SerializationContext ctx) {
        if (t != null) {
            generator.writeStartObject();
            for (Map.Entry<String, Function<T, ?>> entry : suppliers.entrySet()) {
                try {
                    Object value = entry.getValue().apply(t);
                    if (value != null) {
                        ctx.serialize(entry.getKey(), value, generator);
                    }
                } catch (LazyInitializationException ignore) {
                }
            }
            generator.writeEnd();
        } else {
            ctx.serialize(null, generator);
        }
    }
}
