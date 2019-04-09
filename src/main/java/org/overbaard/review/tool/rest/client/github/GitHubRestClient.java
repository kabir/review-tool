package org.overbaard.review.tool.rest.client.github;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.overbaard.review.tool.security.github.AccessTokenResponse;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class GitHubRestClient {
    @Inject
    @RestClient
    GitHubApiService apiService;

    @Inject
    @RestClient
    GitHubAuthService authService;

    public GitHubUser getUser(String token) throws NotAuthorizedException {
        return apiService.getUser(token);
    }

    public GitHubUser getUserByName(String token, String userName) throws NotAuthorizedException, NotFoundException {
        return apiService.getUser(token, userName);
    }

    public AccessTokenResponse getAccessToken(String clientId, String clientSecret, String code, String state) throws NotAuthorizedException {
        return authService.getAccessToken(clientId, clientSecret, code, state);
    }
}
