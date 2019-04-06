package org.overbaard.review.tool.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.overbaard.review.tool.util.SimpleJsonValue;
import org.overbaard.review.tool.security.github.GitHubCredential;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.security.github.SiteAdmin;

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

    @GET
    @Path("siteAdmin")
    @Transactional
    public SimpleJsonValue<Boolean> isSiteAdmin() {
        return isSiteAdmin(gitHubCredential.getLogin());
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
        GitHubUser gitHubUser;
        try {
            gitHubUser = findGitHubUserByLogin(userName);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException("No user found", 404);
        }

        GitHubUser caller = em.find(GitHubUser.class, gitHubCredential.getId());
        if (caller.getSiteAdmin() == null) {
            throw new WebApplicationException("You are not an admin", 403);
        }
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


    private GitHubUser findGitHubUserByLogin(String userName) {
        return em
                .createNamedQuery("GitHubUser.findByGhLogin", GitHubUser.class)
                .setParameter("login", userName)
                .getSingleResult();
    }

}
