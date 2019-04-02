package org.overbaard.review.tool.rest.client.github;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * Hidden behind {@link GitHubRestClient} to be able to swap out with a mock for testing. Mocking @RegisterRestClient
 * classes does not seem possible
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/")
@RegisterRestClient
@RegisterProvider(GitHubApiExceptionMapper.class)
public interface GitHubApiService {

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    GitHubUser getUser  (
            @HeaderParam("Authorization") String token) throws NotAuthorizedException;
}
