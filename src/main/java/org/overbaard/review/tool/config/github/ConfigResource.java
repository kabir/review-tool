package org.overbaard.review.tool.config.github;

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

import org.overbaard.review.tool.security.github.AuthenticationService;
import org.overbaard.review.tool.util.entity.json.EntityJsonSerializer;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/api/config")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ConfigResource {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    AuthenticationService authenticationService;

    private static final Organisation[] EMPTY_ORGANISATIONS = new Organisation[0];

    @GET
    @Path("organisations")
    @Transactional
    public Response getOrganisations() {
        List<Organisation> organisations = entityManager.createNamedQuery(Organisation.Q_FIND_ALL, Organisation.class)
                .getResultList();

        String json =
                EntityJsonSerializer.toJson(
                        organisations,
                        Organisation.selectiveFieldStrategyBuilder()
                                .build());
        return Response.ok(json).build();
    }

    @GET
    @Path("organisations/{id}")
    @Transactional
    public Response getOrganisation(@PathParam("id") int orgId) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        String json =
                EntityJsonSerializer.toJson(
                        org,
                        org.selectiveFieldStrategyBuilder()
                                .addMirroredRepositories()
                                .addAdmins()
                                .build());
        System.out.println(json);
        return Response.ok(json).build();
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
    public Response updateOrganisation(@PathParam("id") int orgId, Organisation organisation) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        org.setName(organisation.getName());
        org.setToolPrRepo(organisation.getToolPrRepo());

        return Response.ok(
                EntityJsonSerializer.toJson(org,
                        org.selectiveFieldStrategyBuilder()
                        .addMirroredRepositories()
                        .build()))
                .build();
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

    @POST
    @Path("organisations/{orgId}/repositories")
    @Transactional
    public Response addMirroredRepository(@PathParam("orgId") int orgId, MirroredRepository mirroredRepository) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        org.addMirroredRepository(mirroredRepository);
        entityManager.persist(mirroredRepository);

        return Response.status(201).build();
    }

    @PUT
    @Path("organisations/{orgId}/repositories/{repoId}")
    @Transactional
    public Response updateMirroredRepository(@PathParam("orgId") int orgId, @PathParam("repoId") int repoId, MirroredRepository mirroredRepository) {
        MirroredRepository repository = entityManager.find(MirroredRepository.class, repoId);
        if (repository == null) {
            throw new WebApplicationException("No mirrored repository found with id: " + repoId, 404);
        }

        repository.setUpstreamOrganisation(mirroredRepository.getUpstreamOrganisation());
        repository.setUpstreamRepository(mirroredRepository.getUpstreamRepository());

        return Response.ok(repository).build();
    }

    @DELETE
    @Path("organisations/{orgId}/repositories/{repoId}")
    @Transactional
    public Response deleteMirroredRepository(@PathParam("orgId") int orgId, @PathParam("repoId") int repoId) {
        MirroredRepository repository = entityManager.find(MirroredRepository.class, repoId);
        if (repository == null) {
            throw new WebApplicationException("No mirrored repository found with id: " + repoId, 404);
        }

        Organisation org = repository.getOrganisation();
        if (org.getId() != orgId) {
            throw new WebApplicationException("Repository " + repoId + " does not belong to organisation: " + orgId, 404);
        }
        org.removeMirroredRepository(repository);
        repository.setOrganisation(null);

        return Response.status(204).build();
    }
}
