package org.overbaard.review.tool.security.github;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.overbaard.review.tool.util.ExpiryCache;

import io.quarkus.scheduler.Scheduled;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@ApplicationScoped
public class GitHubUserCache {

    private final ExpiryCache<String, GitHubUser> tokenToUserMappings = new ExpiryCache<>(120);

    public void cacheUser(String token, GitHubUser user) {
        tokenToUserMappings.add(token, user);
    }

    public GitHubUser getUser(String token) {
        return tokenToUserMappings.get(token);
    }

    @Scheduled(every = "30s", delay = 30, delayUnit = TimeUnit.SECONDS)
    public void evictExpired() {
        tokenToUserMappings.evictExpiredEntries();
    }
}
