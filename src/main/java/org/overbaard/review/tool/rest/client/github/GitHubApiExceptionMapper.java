package org.overbaard.review.tool.rest.client.github;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GitHubApiExceptionMapper implements ResponseExceptionMapper<BaseClientException> {
    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status == 401 || status == 404;
    }

    @Override
    public BaseClientException toThrowable(Response response) {
        switch (response.getStatus()) {
            case 401: return new NotAuthorizedException();
            case 404: return new NotFoundException();
        }
        return null;
    }
}
