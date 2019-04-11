package org.overbaard.review.tool.review;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.overbaard.review.tool.config.github.MirroredRepository;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "feature_branch_review_request")
public class FeatureBranchReviewRequest {
    @Id
    @SequenceGenerator(
            name = "featureBranchReviewRequestSequence",
            sequenceName = "feature_branch_review_request_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "featureBranchReviewRequestSequence")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private GitHubUser owner; // This is essentially the github user of where the feature branch lives

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_request_id", nullable = false)
    private ReviewRequest reviewRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mirrored_repository_id", nullable = false)
    private MirroredRepository mirroredRepository; // Think this can be left as uni-directional

    @Column(name = "feature_branch", nullable = false)
    private String featureBranch;

    @Column(name = "target_branch", nullable = false)
    private String targetBranch;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

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

    public ReviewRequest getReviewRequest() {
        return reviewRequest;
    }

    public void setReviewRequest(ReviewRequest reviewRequest) {
        this.reviewRequest = reviewRequest;
    }

    public MirroredRepository getMirroredRepository() {
        return mirroredRepository;
    }

    public void setMirroredRepository(MirroredRepository mirroredRepository) {
        this.mirroredRepository = mirroredRepository;
    }

    public String getFeatureBranch() {
        return featureBranch;
    }

    public void setFeatureBranch(String featureBranch) {
        this.featureBranch = featureBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
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
}
