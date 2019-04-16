package org.overbaard.review.tool.security.github;

import java.util.List;
import java.util.Set;

import org.overbaard.review.tool.config.github.OrganisationDto;
import org.overbaard.review.tool.review.FeatureBranchRequest;
import org.overbaard.review.tool.review.ReviewRequest;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GitHubUserDto {
    private final Long id;
    private final String login;
    private final String name;
    private final String email;
    private final String avatarUrl;
    private final Set<OrganisationDto> adminOfOrganisations;
    private final List<ReviewRequest> reviewRequests;
    private final List<FeatureBranchRequest> featureBranchRequests;

    private GitHubUserDto(
            Long id,
            String login,
            String name,
            String email,
            String avatarUrl,
            Set<OrganisationDto> adminOfOrganisations,
            List<ReviewRequest> reviewRequests,
            List<FeatureBranchRequest> featureBranchRequests) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.adminOfOrganisations = adminOfOrganisations;
        this.reviewRequests = reviewRequests;
        this.featureBranchRequests = featureBranchRequests;
    }

    public static GitHubUserDto summary(GitHubUser user) {
        return create(user, Level.SUMMARY);
    }

    private static GitHubUserDto create(GitHubUser user, Level level) {
        return new GitHubUserDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                null,
                null,
                null);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Set<OrganisationDto> getAdminOfOrganisations() {
        return adminOfOrganisations;
    }

    public List<ReviewRequest> getReviewRequests() {
        return reviewRequests;
    }

    public List<FeatureBranchRequest> getFeatureBranchRequests() {
        return featureBranchRequests;
    }

    private enum Level {
        SUMMARY
    }
}
