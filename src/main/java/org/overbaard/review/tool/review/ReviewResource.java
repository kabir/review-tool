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
    public List<ReviewRequestDto> getAllReviews(@PathParam("orgId") Long orgId) {
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
        ReviewRequest reviewRequest = em.find(ReviewRequest.class, reviewId);
        // TODO optimise query/eager loading

        ReviewRequestDto dto = ReviewRequestDto.detail(reviewRequest);
        return dto;
    }

    @POST
    @Path("organisation/{orgId}")
    @Transactional
    public Response createReviewRequest(@PathParam("orgId") Long orgId, ReviewRequest reviewRequest) {
        GitHubUser owner = em.find(GitHubUser.class, gitHubCredential.getId());
        Organisation org = em.find(Organisation.class, orgId);

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
        ReviewRequest rr = em.find(ReviewRequest.class, reviewId);

        rr.setTitle(reviewRequest.getTitle());
        rr.setIssueTrackerLink(reviewRequest.getIssueTrackerLink());
        rr.setDescription(reviewRequest.getDescription());

        return Response.status(204).build();
    }

    @DELETE
    @Path("{reviewId}")
    @Transactional
    public Response deleteReviewRequest(@PathParam("reviewId") Long reviewId) {
        ReviewRequest rr = em.getReference(ReviewRequest.class, reviewId);
        em.remove(rr);
        return Response.status(204).build();
    }

    @POST
    @Path("review/{reviewId}/branch")
    @Transactional
    public Response addReviewRequestFeatureBranch(
            @PathParam("orgId") Long orgId, @PathParam("reviewId") Long reviewId, FeatureBranchRequest branchReviewRequest) {
        return Response.status(201).build();
    }

    @GET
    @Path("organisation/{orgId}/review/{reviewId}/branch/{branchId}")
    @Transactional
    public Response getReviewRequestFeatureBranchDetail(
            @PathParam("orgId") Long orgId, @PathParam("reviewId") Long reviewId, @PathParam("branchId") Long branchId) {
        return Response.status(204).build();
    }

    @PUT
    @Path("organisation/{orgId}/review/{reviewId}/branch/{branchId}")
    @Transactional
    public Response updateReviewRequestFeatureBranch(
            @PathParam("orgId") Long orgId, @PathParam("reviewId") Long reviewId,
            @PathParam("branchId") Long branchId, FeatureBranchRequest branchReviewRequest) {
        return Response.status(204).build();
    }

    @DELETE
    @Path("organisation/{orgId}/review/{reviewId}/branch/{branchId}")
    @Transactional
    public Response deleteReviewRequestFeatureBranch(
            @PathParam("orgId") Long orgId, @PathParam("reviewId") Long reviewId, @PathParam("branchId") Long branchId) {
        return Response.status(204).build();
    }
}
