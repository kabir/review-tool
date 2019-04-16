package org.overbaard.review.tool.review;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.overbaard.review.tool.config.github.Organisation;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.util.EntitySerializer;
import org.overbaard.review.tool.util.MapBuilder;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "review_request")
@NamedQueries({
        @NamedQuery(name = ReviewRequest.Q_FIND_ALL,
                query = "SELECT r FROM ReviewRequest r JOIN FETCH r.owner",
                hints = @QueryHint(name = "org.hibernate.cacheable", value = "true")),
        @NamedQuery(name = ReviewRequest.Q_FIND_FOR_ORG,
                query = "SELECT r FROM ReviewRequest r JOIN r.organisation o WHERE o.id = :orgId",
                hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
})
@NamedEntityGraphs({
        @NamedEntityGraph(name = ReviewRequest.G_OWNER, attributeNodes = @NamedAttributeNode("owner"))
})
@JsonbTypeSerializer(ReviewRequest.Serializer.class)
public class ReviewRequest {

    public static final String Q_FIND_ALL = "ReviewRequest.findAll";
    public static final String Q_FIND_FOR_ORG = "ReviewRequest.findForOrg";

    public static final String G_OWNER = "ReviewRequest.owner";

    @Id
    @SequenceGenerator(
            name = "reviewRequestSequence",
            sequenceName = "gh_review_request_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviewRequestSequence")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private GitHubUser owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reviewRequest")
    private List<FeatureBranchRequest> featureBranchRequests = new ArrayList<>();


    @Column(nullable = false)
    private String title;

    @Column(name = "issue_tracker_link", nullable = false)
    private String issueTrackerLink;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private String description;

    // TODO publishedStatus (draft, published, ...)
    // TODO reviewStatus (pending, approved, ...)
    // TODO CI information and status (probably a separate table to take into account different CI systems)


    public ReviewRequest() {

    }

    public ReviewRequest(String title, String issueTrackerLink, String description) {
        this(null, title, issueTrackerLink, description);
    }

    public ReviewRequest(Long id, String title, String issueTrackerLink, String description) {
        this.id = id;
        this.title = title;
        this.issueTrackerLink = issueTrackerLink;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GitHubUser getOwner() {
        return owner;
    }

    public void setOwner(GitHubUser owner) {
        this.owner = owner;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssueTrackerLink() {
        return issueTrackerLink;
    }

    public void setIssueTrackerLink(String issueTrackerLink) {
        this.issueTrackerLink = issueTrackerLink;
    }

    public List<FeatureBranchRequest> getFeatureBranchRequests() {
        return featureBranchRequests;
    }

    public void setFeatureBranchRequests(List<FeatureBranchRequest> featureBranchRequests) {
        this.featureBranchRequests = featureBranchRequests;
    }

    public static class Serializer extends EntitySerializer<ReviewRequest> {
        public Serializer() {
            super(
                    MapBuilder.<String, Function<ReviewRequest, ?>>linkedHashMap()
                            .put("id", o -> o.getId())
                            .put("owner", o -> o.getOwner())
                            .put("organisation", o -> o.getOrganisation())
                            .put("title", o -> o.getTitle())
                            .put("description", o -> o.getDescription())
                            .put("issueTrackerLink", o -> o.getIssueTrackerLink())
                            .put("featureBranchRequests", o -> o.getFeatureBranchRequests())
                            .build());
        }

    }
}
