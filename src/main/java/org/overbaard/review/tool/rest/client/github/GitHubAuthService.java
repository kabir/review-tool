package org.overbaard.review.tool.rest.client.github;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.overbaard.review.tool.security.github.AccessTokenResponse;

/**
 * Hidden behind {@link GitHubRestClient} to be able to swap out with a mock for testing. Mocking @RegisterRestClient
 * classes does not seem possible
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/login")
@RegisterRestClient
@RegisterProvider(GitHubApiExceptionMapper.class)
public interface GitHubAuthService {

    @POST
    @Path("/oauth/access_token")
    @Produces(MediaType.APPLICATION_JSON)
    AccessTokenResponse getAccessToken(
            @QueryParam("client_id") String clientId,
            @QueryParam("client_secret") String clientSecret,
            @QueryParam("code") String code,
            @QueryParam("state") String state) throws NotAuthorizedException;


}
