package org.overbaard.review.tool.config.github;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/api/config")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ConfigResource {
    @Inject
    EntityManager entityManager;

    private static final Organisation[] EMPTY_ORGANISATIONS = new Organisation[0];

    @GET
    @Path("organisations")
    public Organisation[] getOrganisations() {
        return entityManager.createNamedQuery("Organisation.findAll", Organisation.class)
                .getResultList()
                .toArray(EMPTY_ORGANISATIONS);
    }

    @GET
    @Path("organisations/{id}")
    public Organisation getOrganisation(@PathParam("id") int orgId) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }
        return org;
    }

    @POST
    @Path("organisations")
    @Transactional
    public Response createOrganisation(Organisation organisation) {
        if (organisation.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        entityManager.persist(organisation);
        return Response.ok(organisation).status(201).build();
    }

    @PUT
    @Path("organisations/{id}")
    @Transactional
    public Organisation updateOrganisation(@PathParam("id") int orgId, Organisation organisation) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        org.setOrgName(organisation.getOrgName());
        org.setToolPrRepo(organisation.getToolPrRepo());

        return org;
    }

    @DELETE
    @Path("organisations/{id}")
    @Transactional
    public Response deleteOrganisation(@PathParam("id") int orgId) {
        Organisation organisation = entityManager.getReference(Organisation.class, orgId);
        if (organisation == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        entityManager.remove(organisation);

        return Response.status(204).build();
    }

}
