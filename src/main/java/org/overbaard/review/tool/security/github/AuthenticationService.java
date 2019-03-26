package org.overbaard.review.tool.security.github;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.overbaard.review.tool.rest.client.github.GitHubRestClient;
import org.overbaard.review.tool.rest.client.github.NotAuthorizedException;
import org.overbaard.review.tool.util.ExpiryCache;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class AuthenticationService {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    GitHubRestClient restClient;

    private final ExpiryCache<String, GitHubUser> tokenToUserMappings = new ExpiryCache<>(50, 2);



    @Transactional
    public AuthenticationRequest recordNewAuthenticationRequest(String path) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(path);
        entityManager.persist(authenticationRequest);
        return authenticationRequest;
    }

    @Transactional
    public AuthenticationRequest loadAuthenticationRequest(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        AuthenticationRequest request = entityManager.find(AuthenticationRequest.class, uuid);
        return request;
    }

    @Transactional
    public void deleteAuthenticationRequest(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        AuthenticationRequest request = entityManager.getReference(AuthenticationRequest.class, uuid);
        if (request != null) {
            entityManager.remove(request);
        }
    }

    @Transactional
    public void storeToken(String uuidString, String token) {
        UUID uuid = UUID.fromString(uuidString);
        AuthenticationRequest request = entityManager.find(AuthenticationRequest.class, uuid);
        if (request == null) {
            throw new NullPointerException("Could not find authentication request");
        }
        request.setToken(token);
    }

    public AccessTokenResponse getGitHubAccessToken(String code, String state) {
        try {
            return restClient.getAccessToken(
                    GitHubAuthConstants.CLIENT_ID,
                    GitHubAuthConstants.CLIENT_SECRET,
                    code,
                    state);
        } catch (NotAuthorizedException e) {
            return null;
        }
    }

    public GitHubUser validateGitHubToken(String authTokenHeader)  {
        try {
            return restClient.getUser(authTokenHeader);
        } catch (NotAuthorizedException e) {
            return null;
        }
    }

    @Transactional
    public GitHubUser exchangeApiUserForLocalUser(GitHubUser user) {
        if (user == null) {
            return null;
        }
        GitHubUser storedUser = null;
        try {
            storedUser = entityManager.createNamedQuery("GitHubUser.findByGhLogin", GitHubUser.class)
                    .setParameter("login", user.getLogin())
                    .getSingleResult();
        } catch (NoResultException ignore) {
        }

        if (storedUser == null) {
            //The id in the user will be the GitHub one so clear that here and let the id generator do its job
            user.setId(null);
            entityManager.persist(user);
        } else {
            //The id in the user will be the GitHub one so overwrite that with the local one
            user.setId(storedUser.getId());
        }

        return user;
    }
}
