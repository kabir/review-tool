package org.overbaard.review.tool.config.github;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class MirroredRepositoryDto {
    private final Long id;
    private final String upstreamOrganisation;
    private final String upstreamRepository;

    private MirroredRepositoryDto(Long id, String upstreamOrganisation, String upstreamRepository) {
        this.id = id;
        this.upstreamOrganisation = upstreamOrganisation;
        this.upstreamRepository = upstreamRepository;
    }

    public static MirroredRepositoryDto summary(MirroredRepository repository) {
        return create(repository, Level.SUMMARY);
    }

    private static MirroredRepositoryDto create(MirroredRepository repository, Level level) {
        return new MirroredRepositoryDto(
                repository.getId(),
                repository.getUpstreamOrganisation(),
                repository.getUpstreamRepository());
    }

    public Long getId() {
        return id;
    }

    public String getUpstreamOrganisation() {
        return upstreamOrganisation;
    }

    public String getUpstreamRepository() {
        return upstreamRepository;
    }

    private enum Level {
        SUMMARY
    }
}
