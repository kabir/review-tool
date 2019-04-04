package org.overbaard.review.tool.security.github;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.overbaard.review.tool.rest.client.github.GitHubRestClient;
import org.overbaard.review.tool.rest.client.github.NotAuthorizedException;
import org.overbaard.review.tool.util.ExpiryCache;

import io.quarkus.scheduler.Scheduled;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class AuthenticationService {

    ExpiryCache<UUID, AuthenticationRequest> authenticationRequests = new ExpiryCache<>(5 * 60);

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    GitHubRestClient restClient;

    @Scheduled(every = "30s", delay = 30, delayUnit = TimeUnit.SECONDS)
    public void evictExpiredAuthenticationRequests() {
        authenticationRequests.evictExpiredEntries();
    }


    public AuthenticationRequest recordNewAuthenticationRequest(String path, String proxyUrl) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(path, proxyUrl);
        authenticationRequests.add(authenticationRequest.getId(), authenticationRequest);
        return authenticationRequest;
    }

    public AuthenticationRequest loadAuthenticationRequest(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        AuthenticationRequest request = authenticationRequests.get(uuid);
        return request;
    }

    public void deleteAuthenticationRequest(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        authenticationRequests.remove(uuid);
    }

    public void storeToken(String uuidString, AccessTokenResponse token) {
        UUID uuid = UUID.fromString(uuidString);
        AuthenticationRequest request = authenticationRequests.get(uuid);
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
            return user;
        } else {
            // Update the user fields that could be refreshed from GH
            storedUser.setName(user.getName());
            storedUser.setAvatarUrl(user.getAvatarUrl());

            return storedUser;
        }
    }

}
