package org.overbaard.review.tool.review;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.overbaard.review.tool.config.github.Organisation;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "review_request")
public class ReviewRequest {
    @Id
    @SequenceGenerator(
            name = "reviewRequestSequence",
            sequenceName = "gh_review_request_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviewRequestSequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private GitHubUser owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reviewRequest")
    private List<FeatureBranchReviewRequest> featureBranchReviewRequests = new ArrayList<>();


    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    // TODO publishedStatus (draft, published, ...)
    // TODO reviewStatus (pending, approved, ...)
    // TODO CI information and status (probably a separate table to take into account different CI systems)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public List<FeatureBranchReviewRequest> getFeatureBranchReviewRequests() {
        return featureBranchReviewRequests;
    }

    public void setFeatureBranchReviewRequests(List<FeatureBranchReviewRequest> featureBranchReviewRequests) {
        this.featureBranchReviewRequests = featureBranchReviewRequests;
    }
}
