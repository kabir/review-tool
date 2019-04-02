package org.overbaard.review.tool.config.github;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_mirrored_repository")
public class MirroredRepository {
    @Id
    @SequenceGenerator(
            name = "ghMirroredRepositorySequence",
            sequenceName = "gh_mirrored_repository_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ghMirroredRepositorySequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gh_organisation_id")
    Organisation organisation;

    @Column(name = "upstream_organisation", nullable = false)
    String upstreamOrganisation;
    @Column(name = "upstream_repository", nullable = false)
    String upstreamRepository;

    public MirroredRepository() {
    }

    public MirroredRepository(Organisation organisation, String upstreamOrganisation, String upstreamRepository) {
        this.organisation = organisation;
        this.upstreamOrganisation = upstreamOrganisation;
        this.upstreamRepository = upstreamRepository;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public String getUpstreamOrganisation() {
        return upstreamOrganisation;
    }

    public void setUpstreamOrganisation(String upstreamOrganisation) {
        this.upstreamOrganisation = upstreamOrganisation;
    }

    public String getUpstreamRepository() {
        return upstreamRepository;
    }

    public void setUpstreamRepository(String upstreamRepository) {
        this.upstreamRepository = upstreamRepository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MirroredRepository that = (MirroredRepository) o;

        if (!id.equals(that.id)) return false;
        if (!organisation.equals(that.organisation)) return false;
        if (!upstreamOrganisation.equals(that.upstreamOrganisation)) return false;
        return upstreamRepository.equals(that.upstreamRepository);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + organisation.hashCode();
        result = 31 * result + upstreamOrganisation.hashCode();
        result = 31 * result + upstreamRepository.hashCode();
        return result;
    }
}
