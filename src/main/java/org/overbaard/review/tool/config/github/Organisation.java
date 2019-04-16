package org.overbaard.review.tool.config.github;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.overbaard.review.tool.review.ReviewRequest;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.util.EntitySerializer;
import org.overbaard.review.tool.util.MapBuilder;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_organisation")
@NamedQuery(name = Organisation.Q_FIND_ALL,
        query = "SELECT o FROM Organisation o ORDER BY o.name",
        hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@JsonbTypeSerializer(Organisation.Serializer.class)
public class Organisation {

    public static final String Q_FIND_ALL = "Organisation.findAll";

    @Id
    @SequenceGenerator(
            name = "ghOrganisationSequence",
            sequenceName = "gh_organisation_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ghOrganisationSequence")
    private Long id;

    @Column(unique = true, length = 255, nullable = false) // 255 should be long enough although GH doesn't seem to really have a limit
    private String name;

    @Column(name="tool_pr_repo", nullable = false)
    private String toolPrRepo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organisation")
    private List<MirroredRepository> mirroredRepositories = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "organisation_admin",
            joinColumns = { @JoinColumn(name = "organisation_id") },
            inverseJoinColumns = { @JoinColumn(name = "gh_user_id")})
    private Set<GitHubUser> admins = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organisation")
    private List<ReviewRequest> reviewRequests = new ArrayList<>();


    public Organisation() {
    }

    public Organisation(String name, String reviewRepo) {
        this.name = name;
        this.toolPrRepo = reviewRepo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Set<GitHubUser> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<GitHubUser> admins) {
        this.admins = admins;
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

    public List<ReviewRequest> getReviewRequests() {
        return reviewRequests;
    }

    public void setReviewRequests(List<ReviewRequest> reviewRequests) {
        this.reviewRequests = reviewRequests;
    }

    public void addAdmin(GitHubUser gitHubUser) {
        admins.add(gitHubUser);
        gitHubUser.getAdminOfOrganisations().add(this);
    }


    public void removeAdmin(GitHubUser gitHubUser) {
        admins.remove(gitHubUser);
        gitHubUser.getAdminOfOrganisations().remove(this);
    }

    public void addReviewRequest(ReviewRequest reviewRequest) {
        reviewRequests.add(reviewRequest);
        reviewRequest.setOrganisation(this);
    }

    public static class Serializer extends EntitySerializer<Organisation> {
        public Serializer() {
            super(
                    MapBuilder.<String, Function<Organisation, ?>>linkedHashMap()
                            .put("id", o -> o.getId())
                            .put("name", o -> o.getName())
                            .put("toolPrRepo", o -> o.getToolPrRepo())
                            .put("mirroredRepositories", o -> o.getMirroredRepositories())
                            .put("admins", o -> o.getAdmins())
                            .build()
            );
        }
    }

}
