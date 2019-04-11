package org.overbaard.review.tool.security.github;

import javax.enterprise.context.RequestScoped;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@RequestScoped
public class GitHubCredential {
    private GitHubUser gitHubUser;
    private String tokenHeader;

    public void setGitHubUser(GitHubUser gitHubUser) {
        this.gitHubUser = gitHubUser;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public Long getId() {
        return gitHubUser.getId();
    }

    public String getLogin() {
        return gitHubUser.getLogin();
    }

    public String getName() {
        return gitHubUser.getName();
    }

    public String getAvatarUrl() {
        return gitHubUser.getAvatarUrl();
    }
}
