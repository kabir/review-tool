package org.overbaard.review.tool.rest.client.github;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class NotFoundException extends BaseClientException {
    public NotFoundException() {
        super(404);
    }
}
