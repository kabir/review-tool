package org.overbaard.review.tool.config.github;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<OrganisationDto> getOrganisations() {
        return entityManager.createNamedQuery(Organisation.Q_FIND_ALL, Organisation.class)
                .getResultList()
                .stream()
                .map(o -> OrganisationDto.summary(o))
                .collect(Collectors.toList());
    }

    @GET
    @Path("organisations/{id}")
    @Transactional
    public OrganisationDto getOrganisation(@PathParam("id") long orgId) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        return OrganisationDto.detail(org);
    }

    @POST
    @Path("organisations")
    @Transactional
    public Response createOrganisation(Organisation organisation) {
        if (organisation.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        entityManager.persist(organisation);
        OrganisationDto dto =
                OrganisationDto.detail(organisation);
        return Response.ok(dto).status(201).build();
    }

    @PUT
    @Path("organisations/{id}")
    @Transactional
    public Response updateOrganisation(@PathParam("id") long orgId, Organisation organisation) {
        Organisation org = entityManager.find(Organisation.class, orgId);
        if (org == null) {
            throw new WebApplicationException("No organisation found with id: " + orgId, 404);
        }

        org.setName(organisation.getName());
        org.setToolPrRepo(organisation.getToolPrRepo());

        OrganisationDto dto =
                OrganisationDto.detail(org);

        return Response.ok(dto).build();
    }

    @DELETE
    @Path("organisations/{id}")
    @Transactional
    public Response deleteOrganisation(@PathParam("id") long orgId) {
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
    public Response addMirroredRepository(@PathParam("orgId") long orgId, MirroredRepository mirroredRepository) {
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
    public Response updateMirroredRepository(@PathParam("orgId") long orgId, @PathParam("repoId") long repoId, MirroredRepository mirroredRepository) {
        MirroredRepository repository = entityManager.find(MirroredRepository.class, repoId);
        if (repository == null) {
            throw new WebApplicationException("No mirrored repository found with id: " + repoId, 404);
        }

        repository.setUpstreamOrganisation(mirroredRepository.getUpstreamOrganisation());
        repository.setUpstreamRepository(mirroredRepository.getUpstreamRepository());

        MirroredRepositoryDto dto = MirroredRepositoryDto.summary(repository);

        return Response.ok(dto).build();
    }

    @DELETE
    @Path("organisations/{orgId}/repositories/{repoId}")
    @Transactional
    public Response deleteMirroredRepository(@PathParam("orgId") long orgId, @PathParam("repoId") long repoId) {
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
