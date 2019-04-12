package org.overbaard.review.tool.review;

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
    public List<ReviewRequest> getAllReviews(@PathParam("orgId") Long orgId) {
        List<ReviewRequest> reviewRequests = em.createNamedQuery(ReviewRequest.Q_FIND_ALL, ReviewRequest.class)
                .setHint("javax.persistence.fetchgraph", ReviewRequest.G_OWNER)
                .getResultList();
        //TODO - this pulls in the user and org anyway despite my efforts. We only really want to serialize the owner (also the query/hints should be optimised)
        reviewRequests.forEach(r -> {
            r.getOwner();
        });
        return reviewRequests;
    }

    @GET
    @Path("organisation/{orgId}")
    @Transactional
    public List<ReviewRequest> getReviewRequestsForOrg(@PathParam("orgId") Long orgId) {
        List<ReviewRequest> reviewRequests = em.createNamedQuery(ReviewRequest.Q_FIND_FOR_ORG, ReviewRequest.class)
                .setHint("javax.persistence.fetchgraph", ReviewRequest.G_OWNER)
                .setParameter("orgId", orgId)
                .getResultList();
        //TODO - this pulls in the user and org anyway despite my efforts. We only really want to serialize the owner (also the query/hints should be optimised)
        reviewRequests.forEach(r -> {
            r.getOwner();
        });
        return reviewRequests;
    }

    @GET
    @Path("{reviewId}")
    @Transactional
    public ReviewRequest getReviewRequestDetail(@PathParam("reviewId") Long reviewId) {
        ReviewRequest reviewRequest = em.find(ReviewRequest.class, reviewId);

        // TODO revisit serialization and how to eager load this more optimally
        // Load lazy fields needed
        reviewRequest.getDescription();
        reviewRequest.getFeatureBranchReviewRequests();
        reviewRequest.getOwner();

        return reviewRequest;
    }

    @POST
    @Path("organisation/{orgId}")
    @Transactional
    public Response createReviewRequest(@PathParam("orgId") Long orgId, ReviewRequest reviewRequest) {
        GitHubUser owner = em.find(GitHubUser.class, gitHubCredential.getId());
        Organisation org = em.find(Organisation.class, orgId);

        owner.getReviewRequests();
        owner.addReviewRequest(reviewRequest);
        org.addReviewRequest(reviewRequest);
        em.persist(reviewRequest);


        return Response.ok(reviewRequest).status(201).build();
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
    @Path("organisation/{orgId}/review/{reviewId}/branch")
    @Transactional
    public Response addReviewRequestFeatureBranch(
            @PathParam("orgId") Long orgId, @PathParam("reviewId") Long reviewId, FeatureBranchReviewRequest branchReviewRequest) {
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
            @PathParam("branchId") Long branchId, FeatureBranchReviewRequest branchReviewRequest) {
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
