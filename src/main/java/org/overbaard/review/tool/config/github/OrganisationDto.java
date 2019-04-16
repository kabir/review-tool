package org.overbaard.review.tool.config.github;

import java.util.ArrayList;
import java.util.List;

import org.overbaard.review.tool.review.ReviewRequest;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.security.github.GitHubUserDto;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class OrganisationDto {
    private final Long id;
    private final String name;
    private final String toolPrRepo;
    private final List<MirroredRepositoryDto> mirroredRepositories;
    private final List<GitHubUserDto> admins;
    private final List<ReviewRequest> reviewRequests;

    private OrganisationDto(
            Long id,
            String name,
            String toolPrRepo,
            List<MirroredRepositoryDto> mirroredRepositories,
            List<GitHubUserDto> admins,
            List<ReviewRequest> reviewRequests) {
        this.id = id;
        this.name = name;
        this.toolPrRepo = toolPrRepo;
        this.mirroredRepositories = mirroredRepositories;
        this.admins = admins;
        this.reviewRequests = reviewRequests;
    }

    public static OrganisationDto summary(Organisation org) {
        return create(org, Level.SUMMARY);
    }

    public static OrganisationDto detail(Organisation org) {
        return create(org, Level.DETAIL);
    }

    private static OrganisationDto create(Organisation org, Level level) {
        List<MirroredRepositoryDto> mirroredRepositoryDtos = null;
        if (level == Level.DETAIL && org.getMirroredRepositories() != null) {
            mirroredRepositoryDtos = new ArrayList<>();
            for (MirroredRepository m : org.getMirroredRepositories()) {
                mirroredRepositoryDtos.add(MirroredRepositoryDto.summary(m));
            }
        }
        List<GitHubUserDto> adminDtos = null;
        if (level == Level.DETAIL && org.getAdmins() != null) {
            adminDtos = new ArrayList<>();
            for (GitHubUser u : org.getAdmins()) {
                adminDtos.add(GitHubUserDto.summary(u));
            }
        }
        return new OrganisationDto(
                org.getId(),
                org.getName(),
                level == Level.DETAIL ? org.getToolPrRepo() : null,
                mirroredRepositoryDtos,
                adminDtos,
                null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToolPrRepo() {
        return toolPrRepo;
    }

    public List<MirroredRepositoryDto> getMirroredRepositories() {
        return mirroredRepositories;
    }

    public List<GitHubUserDto> getAdmins() {
        return admins;
    }

    public List<ReviewRequest> getReviewRequests() {
        return reviewRequests;
    }

    private enum Level {
        SUMMARY,
        DETAIL
    }
}
