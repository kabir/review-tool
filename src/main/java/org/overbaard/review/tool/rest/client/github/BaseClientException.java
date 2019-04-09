package org.overbaard.review.tool.rest.client.github;

import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class BaseClientException extends WebApplicationException {

    BaseClientException(int status) {
        super(status);
    }

}
