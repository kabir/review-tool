package org.overbaard.review.tool.review;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.overbaard.review.tool.config.github.Organisation;
import org.overbaard.review.tool.security.github.GitHubCredential;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/api/review")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ReviewResource {

    @PersistenceContext
    EntityManager em;

    @Inject
    GitHubCredential gitHubCredential;

    @GET
    @Transactional
    public List<ReviewRequestDto> getAllReviews() {
        List<ReviewRequest> reviewRequests = em.createNamedQuery(ReviewRequest.Q_FIND_ALL, ReviewRequest.class)
                .setHint("javax.persistence.fetchgraph", ReviewRequest.G_OWNER)
                .getResultList();
        //TODO - The query/hints should be optimised for this use case
        List<ReviewRequestDto> dtos = new ArrayList<>();
        reviewRequests.forEach(r -> dtos.add(ReviewRequestDto.summary(r)));
        return dtos;
    }

    @GET
    @Path("organisation/{orgId}")
    @Transactional
    public List<ReviewRequestDto> getReviewRequestsForOrg(@PathParam("orgId") Long orgId) {
        List<ReviewRequest> reviewRequests = em.createNamedQuery(ReviewRequest.Q_FIND_FOR_ORG, ReviewRequest.class)
                .setHint("javax.persistence.fetchgraph", ReviewRequest.G_OWNER)
                .setParameter("orgId", orgId)
                .getResultList();
        //TODO - The query/hints should be optimised for this use case
        List<ReviewRequestDto> dtos = new ArrayList<>();
        reviewRequests.forEach(r -> dtos.add(ReviewRequestDto.summary(r)));
        return dtos;
    }

    @GET
    @Path("{reviewId}")
    @Transactional
    public ReviewRequestDto getReviewRequestDetail(@PathParam("reviewId") Long reviewId) {
        ReviewRequest rr = loadReviewRequest(reviewId);

        // TODO optimise query/eager loading

        ReviewRequestDto dto = ReviewRequestDto.detail(rr);
        return dto;
    }

    @POST
    @Path("organisation/{orgId}")
    @Transactional
    public Response createReviewRequest(@PathParam("orgId") Long orgId, ReviewRequest reviewRequest) {
        GitHubUser owner = em.find(GitHubUser.class, gitHubCredential.getId());
        Organisation org = em.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id:" + orgId, 404);
        }

        owner.addReviewRequest(reviewRequest);
        org.addReviewRequest(reviewRequest);
        em.persist(reviewRequest);

        ReviewRequestDto dto = ReviewRequestDto.detail(reviewRequest);

        return Response.ok(dto).status(201).build();
    }

    @PUT
    @Path("{reviewId}")
    @Transactional
    public Response updateReviewRequest(@PathParam("reviewId") Long reviewId, ReviewRequest reviewRequest) {
        ReviewRequest rr = loadReviewRequest(reviewId);

        rr.setTitle(reviewRequest.getTitle());
        rr.setIssueTrackerLink(reviewRequest.getIssueTrackerLink());
        rr.setDescription(reviewRequest.getDescription());

        return Response.status(204).build();
    }

    @DELETE
    @Path("{reviewId}")
    @Transactional
    public Response deleteReviewRequest(@PathParam("reviewId") Long reviewId) {
        // In practice we probably won't use this, but rather close a request. Still it is good for putting
        // the unit tests back to their original state
        ReviewRequest rr = loadReviewRequest(reviewId);
        em.remove(rr);
        return Response.status(204).build();
    }

    @POST
    @Path("{reviewId}/branch")
    @Transactional
    public Response addReviewRequestFeatureBranch(@PathParam("reviewId") Long reviewId, FeatureBranchRequest featureBranchRequest) {
        ReviewRequest rr = loadReviewRequest(reviewId);

        // TODO figure out the owner field here and the mirrored repository here
        rr.addFeatureBranchRequest(featureBranchRequest);
        em.persist(featureBranchRequest);

        FeatureBranchRequestDto dto = FeatureBranchRequestDto.summary(featureBranchRequest);

        return Response.ok(dto).status(201).build();
    }

    @GET
    @Path("{reviewId}/branch/{featureBranchId}")
    @Transactional
    public FeatureBranchRequestDto getReviewRequestFeatureBranchDetail(
            @PathParam("reviewId") Long reviewId, @PathParam("featureBranchId") Long branchId) {
        ReviewRequest rr = loadReviewRequest(reviewId);
        FeatureBranchRequest fbr = loadFeatureBranchRequest(branchId);

        return FeatureBranchRequestDto.detail(fbr);
    }

    @PUT
    @Path("{reviewId}/branch/{featureBranchId}")
    @Transactional
    public Response updateReviewRequestFeatureBranch(
            @PathParam("reviewId") Long reviewId, @PathParam("featureBranchId") Long branchId, FeatureBranchRequest featureBranchRequest) {
        ReviewRequest rr = loadReviewRequest(reviewId);
        FeatureBranchRequest fbr = loadFeatureBranchRequest(branchId);

        fbr.setDescription(featureBranchRequest.getDescription());
        fbr.setFeatureBranch(featureBranchRequest.getFeatureBranch());
        fbr.setTargetBranch(featureBranchRequest.getTargetBranch());
        fbr.setTitle(featureBranchRequest.getTitle());

        return Response.status(204).build();
    }

    @DELETE
    @Path("{reviewId}/branch/{featureBranchId}")
    @Transactional
    public Response deleteReviewRequestFeatureBranch(@PathParam("reviewId") Long reviewId, @PathParam("featureBranchId") Long branchId) {
        ReviewRequest rr = loadReviewRequest(reviewId);
        FeatureBranchRequest fbr = loadFeatureBranchRequest(branchId);

        rr.removeFeatureBranchRequest(fbr);

        em.remove(fbr);

        return Response.status(204).build();
    }

    private ReviewRequest loadReviewRequest(Long reviewId) {
        ReviewRequest rr = em.find(ReviewRequest.class, reviewId);
        if (rr == null) {
            throw new WebApplicationException("No review found with id:" + reviewId, 404);
        }
        return rr;
    }

    private FeatureBranchRequest loadFeatureBranchRequest(Long branchId) {
        FeatureBranchRequest fbr = em.find(FeatureBranchRequest.class, branchId);
        if (fbr == null) {
            throw new WebApplicationException("No feature branch found with id:" + branchId, 404);
        }
        return fbr;
    }
}
