package org.overbaard.review.tool.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.overbaard.review.tool.rest.client.github.GitHubRestClient;
import org.overbaard.review.tool.security.github.AuthenticationService;
import org.overbaard.review.tool.security.github.GitHubCredential;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.security.github.GitHubUserCache;

import io.undertow.util.StatusCodes;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@WebFilter(filterName = "authFilter", asyncSupported = true)
// url-pattern: "/*" set in web.xml
public class AuthFilter extends HttpFilter {

    @Inject
    GitHubCredential credential;

    @Inject
    PathUtil pathUtil;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    GitHubRestClient restClient;

    @Inject
    GitHubUserCache gitHubUserCache;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        PathUtil.PathInfo pathInfo = pathUtil.getPathInfo(req);


        if (pathInfo.isRestCall()) {
            // If accessing the resources under /api/ we don't redirect but return a 401 if we don't have the token,
            // and it cannot be verified
            String authTokenHeader = request.getHeader("Authorization");
            GitHubUser user = getUserFromToken(authTokenHeader);
            if (user == null) {
                response.sendError(StatusCodes.UNAUTHORIZED);
                return;
            }
            // Store the credentials so they are useable later
            credential.setGitHubUser(user);
            credential.setTokenHeader(authTokenHeader);
        } else if (pathInfo.isAuthCall()) {
            // This is not secured, so we just go ahead with the chain
        } else if (pathInfo.isDirectoryResource() || pathInfo.isIndexHtmlRequest()) {
            // We handle this in the client
        }


        // If loading up the root resource, any resource without an extension or the index.html, check we are logged in
        // Redirect to /auth/login/ if not





        chain.doFilter(request, response);
    }

    private GitHubUser getUserFromToken(String authTokenHeader) {
        // Avoid making a REST call to the GitHub API to authenticate the user on every
        // single request as there are rate limits.
        // We cache the users for a few minutes
        GitHubUser user = gitHubUserCache.getUser(authTokenHeader);
        if (user != null) {
            return user;
        }

        user = authenticationService.validateGitHubToken(authTokenHeader);
        user = authenticationService.exchangeApiUserForLocalUser(user);

        if (user != null) {
            gitHubUserCache.cacheUser(authTokenHeader, user);
        }

        return user;
    }

}
