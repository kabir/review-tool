package org.overbaard.review.tool.security.github;

import javax.enterprise.context.ApplicationScoped;

import org.overbaard.review.tool.util.ExpiryCache;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class GitHubUserCache {

    private final ExpiryCache<String, GitHubUser> tokenToUserMappings = new ExpiryCache<>(50, 2);

    public void cacheUser(String token, GitHubUser user) {
        tokenToUserMappings.add(token, user);
    }

    public GitHubUser getUser(String token) {
        return tokenToUserMappings.get(token);
    }
}
