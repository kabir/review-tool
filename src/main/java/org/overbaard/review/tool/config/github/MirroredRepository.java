package org.overbaard.review.tool.config.github;

import javax.json.bind.annotation.JsonbTransient;
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

    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Organisation organisation;

    @Column(name = "upstream_organisation", nullable = false)
    private String upstreamOrganisation;
    @Column(name = "upstream_repository", nullable = false)
    private String upstreamRepository;

    public MirroredRepository() {
    }

    public MirroredRepository(String upstreamOrganisation, String upstreamRepository) {
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

        if (id == null && that.id != null || !id.equals(that.id)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        return result;
    }
}
