package org.overbaard.review.tool.review;

import org.overbaard.review.tool.config.github.MirroredRepositoryDto;
import org.overbaard.review.tool.security.github.GitHubUserDto;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class FeatureBranchRequestDto {
    private final Long id;
    private final GitHubUserDto owner;
    private final ReviewRequestDto reviewRequest;
    private final MirroredRepositoryDto mirroredRepository; // Think this can be left as uni-directional
    private final String featureBranch;
    private final String targetBranch;
    private final String title;
    private final String description;

    private FeatureBranchRequestDto(
            Long id,
            String title,
            GitHubUserDto owner,
            String description,
            String featureBranch,
            String targetBranch,
            MirroredRepositoryDto mirroredRepository,
            ReviewRequestDto reviewRequest) {
        this.id = id;
        this.owner = owner;
        this.reviewRequest = reviewRequest;
        this.mirroredRepository = mirroredRepository;
        this.featureBranch = featureBranch;
        this.targetBranch = targetBranch;
        this.title = title;
        this.description = description;
    }

    public static FeatureBranchRequestDto summary(FeatureBranchRequest f) {
        return create(f, Level.SUMMARY);
    }

    public static FeatureBranchRequestDto detail(FeatureBranchRequest f) {
        return create(f, Level.SUMMARY);
    }

    private static FeatureBranchRequestDto create(FeatureBranchRequest f, Level level) {
        return new FeatureBranchRequestDto(
                f.getId(),
                f.getTitle(),
                GitHubUserDto.summary(f.getOwner()),
                level == Level.DETAIL ? f.getDescription() : null,
                level == Level.DETAIL ? f.getFeatureBranch() : null,
                level == Level.DETAIL ? f.getTargetBranch() : null,
                level == Level.DETAIL ? MirroredRepositoryDto.summary(f.getMirroredRepository()) : null,
                level == Level.DETAIL ? ReviewRequestDto.summary(f.getReviewRequest()) : null
        );
    }


    public Long getId() {
        return id;
    }

    public GitHubUserDto getOwner() {
        return owner;
    }

    public ReviewRequestDto getReviewRequest() {
        return reviewRequest;
    }

    public MirroredRepositoryDto getMirroredRepository() {
        return mirroredRepository;
    }

    public String getFeatureBranch() {
        return featureBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    private enum Level {
        SUMMARY,
        DETAIL
    }
}