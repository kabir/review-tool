package org.overbaard.review.tool.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.overbaard.review.tool.security.github.GitHubCredential;
import org.overbaard.review.tool.security.github.GitHubUser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Path("/api/auth")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class AuthResource {
    @Inject
    GitHubCredential gitHubCredential;

    @PersistenceContext
    EntityManager em;

    @GET
    @Path("admin")
    public Response isSiteAdmin() {
        boolean admin = false;
        try {
            GitHubUser user = em.createNamedQuery("GitHubUser.findByGhLogin", GitHubUser.class)
                    .setParameter("login", gitHubCredential.getLogin())
                    .getSingleResult();
            admin = user.isSiteAdmin();
        } catch (NoResultException ignore) {
        }
        return Response.ok()
                .entity(Json.createObjectBuilder().add("admin", admin).build())
                .build();

    }
}
