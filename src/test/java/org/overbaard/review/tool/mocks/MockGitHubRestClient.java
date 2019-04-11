package org.overbaard.review.tool.mocks;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.overbaard.review.tool.rest.client.github.GitHubRestClient;
import org.overbaard.review.tool.rest.client.github.NotFoundException;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class MockGitHubRestClient extends GitHubRestClient {
    public static Map<String, GitHubUser> usersByName = new HashMap<>();
    @Override
    public GitHubUser getUser(String token) {
        return new GitHubUser(
                10000L,
                "test_user",
                "Mock Test User",
                "test@example.com",
                "http://example.com/avatar/test-user.jpg");
    }

    @Override
    public GitHubUser getUserByName(String token, String userName) {
        GitHubUser user =  usersByName.get(userName);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }
}
