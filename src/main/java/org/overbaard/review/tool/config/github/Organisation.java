package org.overbaard.review.tool.config.github;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
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

import org.overbaard.review.tool.util.LazyEntityFieldsStrategy;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_organisation")
@NamedQuery(name = "Organisation.findAll",
        query = "SELECT o FROM Organisation o ORDER BY o.name",
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

    @Column(unique = true, length = 255, nullable = false) // 255 should be long enough although GH doesn't seem to really have a limit
    private String name;

    @Column(name="tool_pr_repo", nullable = false)
    private String toolPrRepo;

    @OneToMany(mappedBy = "organisation")
    private List<MirroredRepository> mirroredRepositories = new ArrayList<>();

    public Organisation() {
    }

    public Organisation(String name, String reviewRepo) {
        this.name = name;
        this.toolPrRepo = reviewRepo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToolPrRepo() {
        return toolPrRepo;
    }

    public void setToolPrRepo(String toolPrRepo) {
        this.toolPrRepo = toolPrRepo;
    }

    void addMirroredRepository(MirroredRepository mirroredRepository) {
        mirroredRepositories.add(mirroredRepository);
        mirroredRepository.setOrganisation(this);
    }

    void removeMirroredRepository(MirroredRepository mirroredRepository) {
        if (mirroredRepositories.remove(mirroredRepository)) {
            mirroredRepository.setOrganisation(null);
        }
    }

    // Don't expose this as a public property to avoid auto-JSON-B serializing when lazy loaded
    List<MirroredRepository> getMirroredRepositories() {
        return mirroredRepositories;
    }

    // Don't expose this as a public property to avoid auto-JSON-B serializing when lazy loaded
    void setMirroredRepositories(List<MirroredRepository> mirroredRepositories) {
        this.mirroredRepositories = mirroredRepositories;
    }

    public String toJson(boolean detail) {
        // Take control over the serialization for paths where we choose whether to pull
        // in the lazy loaded stuff or not
        if (detail) {
            // Eagerly load the lazy fields if they are wanted
            getMirroredRepositories();
        }
        JsonbConfig config = new JsonbConfig()
                .withPropertyVisibilityStrategy(
                        new LazyEntityFieldsStrategy(detail,"mirroredRepositories"));

        String s = JsonbBuilder.newBuilder()
                .withConfig(config)
                .build()
                .toJson(this);
        return s;
    }
}
