package org.overbaard.review.tool.review;

import java.util.function.Function;

import javax.json.bind.annotation.JsonbTypeSerializer;
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

import org.hibernate.annotations.Type;
import org.overbaard.review.tool.config.github.MirroredRepository;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.util.EntitySerializer;
import org.overbaard.review.tool.util.MapBuilder;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "feature_branch_request")
@JsonbTypeSerializer(FeatureBranchRequest.Serializer.class)
public class FeatureBranchRequest {
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
    // This is essentially the github user of where the feature branch lives
    // TODO a team might be working on an organisation repository too (e.g. an incubator) so it is not necessarily a user
    // In that case we would need to ask for the owner differently, and reco
    private GitHubUser owner;

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
    @Type(type = "org.hibernate.type.TextType")
    @Column(nullable = false)
    private String description;

    public FeatureBranchRequest() {
    }

    public FeatureBranchRequest(Long id, GitHubUser owner, MirroredRepository mirroredRepository, String featureBranch, String targetBranch, String title) {
        this.id = id;
        this.owner = owner;
        this.mirroredRepository = mirroredRepository;
        this.featureBranch = featureBranch;
        this.targetBranch = targetBranch;
        this.title = title;
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


    public static class Serializer extends EntitySerializer<FeatureBranchRequest> {
        public Serializer() {
            super(
                    MapBuilder.<String, Function<FeatureBranchRequest, ?>>linkedHashMap()
                            .put("id", o -> o.getId())
                            .put("owner", o -> o.getOwner())
                            // .put("reviewRequest", o -> o.getReviewRequest())
                            .put("title", o -> o.getTitle())
                            //.put("description", o -> o.getDescription())
                            .put("mirroredRepository", o -> o.getMirroredRepository())
                            .put("featureBranch", o -> o.getFeatureBranch())
                            .put("targetBranch", o -> o.getTargetBranch())
                            .build()
            );

        }
    }
}
