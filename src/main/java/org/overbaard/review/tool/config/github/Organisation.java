package org.overbaard.review.tool.config.github;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

import org.overbaard.review.tool.util.entity.json.EntityJsonMaybeLazy;
import org.overbaard.review.tool.util.entity.json.SelectiveFieldStrategy;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_organisation")
@NamedQuery(name = "Organisation.findAll",
        query = "SELECT o FROM Organisation o ORDER BY o.name",
        hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
public class Organisation {

    private static final String MIRRORED_REPOSITORIES_FIELD = "mirroredRepositories";

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

    @EntityJsonMaybeLazy
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

    public List<MirroredRepository> getMirroredRepositories() {
        return mirroredRepositories;
    }

    public void setMirroredRepositories(List<MirroredRepository> mirroredRepositories) {
        this.mirroredRepositories = mirroredRepositories;
    }

    SelectiveFieldStrategyBuilder selectiveFieldStrategyBuilder() {
        return new SelectiveFieldStrategyBuilder();
    }

    public static class SelectiveFieldStrategyBuilder {
        private Map<String, Consumer<Organisation>> includedFields = new HashMap<>();

        private SelectiveFieldStrategyBuilder() {
        }

        public SelectiveFieldStrategyBuilder addMirroredRepositories() {
            this.includedFields.put(MIRRORED_REPOSITORIES_FIELD, (org) -> org.getMirroredRepositories());
            return this;
        }

        SelectiveFieldStrategy<Organisation> build() {
            return new SelectiveFieldStrategy(includedFields) {
            };
        }
    }
}
