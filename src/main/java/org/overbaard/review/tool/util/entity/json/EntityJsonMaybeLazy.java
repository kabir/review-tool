package org.overbaard.review.tool.util.entity.json;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface EntityJsonMaybeLazy {
}
