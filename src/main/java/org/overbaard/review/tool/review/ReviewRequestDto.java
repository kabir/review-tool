package org.overbaard.review.tool.review;

import java.util.ArrayList;
import java.util.List;

import org.overbaard.review.tool.config.github.OrganisationDto;
import org.overbaard.review.tool.security.github.GitHubUserDto;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ReviewRequestDto {
    private final Long id;
    private final GitHubUserDto owner;
    private final OrganisationDto organisation;
    private final List<FeatureBranchRequestDto> featureBranchRequests;
    private final String title;
    private final String issueTrackerLink;
    private final String description;

    private ReviewRequestDto(
            Long id,
            String title,
            String issueTrackerLink,
            String description,
            GitHubUserDto owner,
            OrganisationDto organisation,
            List<FeatureBranchRequestDto> featureBranchRequests) {
        this.id = id;
        this.owner = owner;
        this.organisation = organisation;
        this.featureBranchRequests = featureBranchRequests;
        this.title = title;
        this.issueTrackerLink = issueTrackerLink;
        this.description = description;
    }

    public static ReviewRequestDto summary(ReviewRequest r) {
        return create(r, Level.SUMMARY);
    }

    public static ReviewRequestDto detail(ReviewRequest r) {
        return create(r, Level.DETAIL);
    }

    private static ReviewRequestDto create(ReviewRequest r, Level level) {
        List<FeatureBranchRequestDto> fbrDtos = null;
        if (level == Level.DETAIL && r.getFeatureBranchRequests() != null) {
            fbrDtos = new ArrayList<>();
            for (FeatureBranchRequest fbr : r.getFeatureBranchRequests()) {
                fbrDtos.add(FeatureBranchRequestDto.summary(fbr));
            }
        }
        return new ReviewRequestDto(
                r.getId(),
                r.getTitle(),
                r.getIssueTrackerLink(),
                level == Level.DETAIL ? r.getDescription() : null,
                GitHubUserDto.summary(r.getOwner()),
                OrganisationDto.summary(r.getOrganisation()),
                null
        );
    }

    public Long getId() {
        return id;
    }

    public GitHubUserDto getOwner() {
        return owner;
    }

    public OrganisationDto getOrganisation() {
        return organisation;
    }

    public List<FeatureBranchRequestDto> getFeatureBranchRequests() {
        return featureBranchRequests;
    }

    public String getTitle() {
        return title;
    }

    public String getIssueTrackerLink() {
        return issueTrackerLink;
    }

    public String getDescription() {
        return description;
    }

    private enum Level {
        SUMMARY,
        DETAIL
    }
}
