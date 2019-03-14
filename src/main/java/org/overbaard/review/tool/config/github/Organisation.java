package org.overbaard.review.tool.config.github;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_organisation")
@NamedQuery(name = "Organisation.findAll",
        query = "SELECT o FROM Organisation o ORDER BY o.orgName",
        hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
public class Organisation {

    @Id
    @SequenceGenerator(
            name = "ghOrganisationSequence",
            sequenceName = "gh_organisation_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ghOrganisationSequence")
    private Integer id;

    @Column(name="org_name", unique = true, length = 255, nullable = false) // 255 should be long enough although GH doesn't seem to really have a limit
    private String orgName;

    @Column(name="tool_repo", nullable = false)
    private String toolPrRepo;

    @OneToMany
    List<MirroredRepository> mirroredRepositories;

    public Organisation() {
    }

    public Organisation(String orgName, String reviewRepo) {
        this.orgName = orgName;
        this.toolPrRepo = reviewRepo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getToolPrRepo() {
        return toolPrRepo;
    }

    public void setToolPrRepo(String toolPrRepo) {
        this.toolPrRepo = toolPrRepo;
    }

    public void addMirroredRepository(MirroredRepository mirroredRepository) {
        mirroredRepositories.add(mirroredRepository);
        mirroredRepository.setOrganisation(this);
    }

    public void removeMirroredRepository(MirroredRepository mirroredRepository) {
        mirroredRepositories.remove(mirroredRepository);
        mirroredRepository.setOrganisation(null);
    }
}
