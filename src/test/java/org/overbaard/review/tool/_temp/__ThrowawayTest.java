package org.overbaard.review.tool._temp;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@QuarkusTest
public class __ThrowawayTest {
    @Test
    public void testSimpleEntity() {
        Organisation organisation = new Organisation("My org");
        String s = JsonbBuilder.create().toJson(organisation);
        System.out.println(s);
    }

    @JsonbTypeSerializer(MySerializer.class)
    public static class Organisation {
        private String name;

        public Organisation(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MySerializer implements JsonbSerializer<Organisation> {
        @Override
        public void serialize(Organisation obj, JsonGenerator generator, SerializationContext ctx) {
            generator.writeStartObject();
            generator.write("name", obj.getName());
            generator.writeEnd();
        }
    }
}
