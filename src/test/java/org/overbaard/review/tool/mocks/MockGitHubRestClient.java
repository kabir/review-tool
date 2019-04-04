package org.overbaard.review.tool.mocks;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.overbaard.review.tool.rest.client.github.GitHubRestClient;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class MockGitHubRestClient extends GitHubRestClient {
    @Override
    public GitHubUser getUser(String token) {
        return new GitHubUser(
                10000,
                "test_user",
                "Test User",
                "test@example.com",
                "http://example.com/avatar/test-user.jpg");
    }
}
