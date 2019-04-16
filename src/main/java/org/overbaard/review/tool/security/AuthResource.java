package org.overbaard.review.tool.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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
import org.overbaard.review.tool.rest.client.github.GitHubRestClient;
import org.overbaard.review.tool.security.github.GitHubCredential;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.security.github.GitHubUserDto;
import org.overbaard.review.tool.security.github.SiteAdmin;
import org.overbaard.review.tool.util.SimpleJsonValue;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/api/auth")
@ApplicationScoped
@Produces({"application/json"})
@Consumes("application/json")
public class AuthResource {
    @Inject
    GitHubCredential gitHubCredential;

    @PersistenceContext
    EntityManager em;

    @Inject
    GitHubRestClient gitHubClient;

    @GET
    @Path("siteAdmin")
    @Transactional
    public List<GitHubUserDto> getAllSiteAdmins() {
        EntityGraph graph = em.getEntityGraph(GitHubUser.G_SITE_ADMIN);
        List<GitHubUser> users = em.createNamedQuery(GitHubUser.Q_FIND_ALL_SITE_ADMINS, GitHubUser.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
        List<GitHubUserDto> dtos = new ArrayList<>();
        for (GitHubUser user : users) {
            dtos.add(GitHubUserDto.summary(user));
        }
        return dtos;
    }

    @GET
    @Path("siteAdmin/{userName}")
    @Transactional
    public SimpleJsonValue<Boolean> isSiteAdmin(@PathParam("userName") String userName) {
        try {
            GitHubUser user = findGitHubUserByLogin(userName);
            return new SimpleJsonValue<>(user.getSiteAdmin() != null);
        } catch (NoResultException ignore) {
            throw new WebApplicationException("No user found with login: " + userName, 404);
        }
    }

    @PUT
    @Path("siteAdmin/{userName}")
    @Transactional
    public void setSiteAdmin(@PathParam("userName") String userName, SimpleJsonValue<Boolean> admin) {
        GitHubUser caller = em.find(GitHubUser.class, gitHubCredential.getId());
        if (caller.getSiteAdmin() == null) {
            throw new WebApplicationException("You are not an admin", 403);
        }

        GitHubUser gitHubUser = findGitHubUserOrPullFromGitHub(userName);

        if (caller.getId() == gitHubUser.getId()) {
            throw new WebApplicationException("You cannot change your own site administrator privileges", 403);
        }

        SiteAdmin siteAdmin = gitHubUser.getSiteAdmin();
        if (admin.getValue()) {
            if (siteAdmin == null) {
                siteAdmin = new SiteAdmin();
                gitHubUser.setSiteAdmin(siteAdmin);
                siteAdmin.setUser(gitHubUser);
                em.persist(siteAdmin);
            }
        } else {
            if (siteAdmin != null) {
                siteAdmin.setUser(null);
                gitHubUser.setSiteAdmin(null);
                em.remove(siteAdmin);
            }
        }
    }

    @GET
    @Path("organisation/{orgId}/admin")
    @Transactional
    public Response getAllOrganisationAdmins(@PathParam("orgId") long orgId) {
        Organisation organisation = em.find(Organisation.class, orgId);
        if (organisation == null) {
            throw new WebApplicationException("No organisation found with Id: " + orgId, 404);
        }

        List<GitHubUser> users = em.createNamedQuery(GitHubUser.Q_FIND_ORG_ADMINS, GitHubUser.class)
                .setParameter("org_id", orgId)
                .getResultList();
        List<GitHubUserDto> dtos = new ArrayList<>();
        for (GitHubUser user : users) {
            dtos.add(GitHubUserDto.summary(user));
        }

        return Response.ok(users).build();
    }

    @POST
    @Path("organisation/{orgId}/admin/{userName}")
    @Transactional
    public void makeOrganisationAdmin(@PathParam("orgId") long orgId, @PathParam("userName") String userName) {
        GitHubUser gitHubUser = findGitHubUserOrPullFromGitHub(userName);
        checkCanUpdateOrganisationAdmins(orgId, gitHubUser);

        Organisation organisation = em.find(Organisation.class, orgId);
        if (organisation == null) {
            throw new WebApplicationException("No organisation found with Id: " + orgId, 404);
        }

        organisation.addAdmin(gitHubUser);
    }

    @DELETE
    @Path("organisation/{orgId}/admin/{userName}")
    @Transactional
    public Response deleteOrganisationAdmin(@PathParam("orgId") long orgId, @PathParam("userName") String userName) {
        GitHubUser gitHubUser = findGitHubUserOrPullFromGitHub(userName);
        checkCanUpdateOrganisationAdmins(orgId, gitHubUser);

        Organisation organisation = em.find(Organisation.class, orgId);
        if (organisation == null) {
            throw new WebApplicationException("No organisation found with Id: " + orgId, 404);
        }

        organisation.removeAdmin(gitHubUser);

        return Response.status(204).build();
    }



    private GitHubUser findGitHubUserByLogin(String userName) {
        return em
                .createNamedQuery(GitHubUser.Q_FIND_BY_LOGIN, GitHubUser.class)
                .setParameter("login", userName)
                .getSingleResult();
    }

    private GitHubUser findGitHubUserOrPullFromGitHub(String userName) {
        GitHubUser gitHubUser = null;
        try {
            gitHubUser = findGitHubUserByLogin(userName);
        } catch (NoResultException | EntityNotFoundException e) {
            // Pull in the user from GitHub, this will throw a WebApplicationException if something goes wrong
            gitHubUser = gitHubClient.getUserByName(gitHubCredential.getTokenHeader(), userName);
            //The id in the user will be the GitHub one so clear that here and let the id generator do its job
            gitHubUser.setId(null);
            em.persist(gitHubUser);
        }
        return gitHubUser;
    }

    private void checkCanUpdateOrganisationAdmins(long orgId, GitHubUser gitHubUser) {

        GitHubUser caller =
                em.find(
                        GitHubUser.class,
                        gitHubCredential.getId(),
                        Collections.singletonMap("javax.persistence.fetchgraph", em.getEntityGraph(GitHubUser.G_ADMIN_OF_ORGS)));
        if (caller.getSiteAdmin() == null) {
            Set<Organisation> organisations = caller.getAdminOfOrganisations();
            boolean admin = false;
            for (Organisation org : organisations) {
                if (org.getId() == orgId) {
                    admin = true;
                    break;
                }
            }
            if (!admin) {
                throw new WebApplicationException("You are not an admin", 403);
            }

            if (caller.getId() == gitHubUser.getId()) {
                throw new WebApplicationException("You cannot change your own site administrator privileges", 403);
            }
        }
    }
}
