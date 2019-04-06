package org.overbaard.review.tool.security.github;

import java.io.IOException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.overbaard.review.tool.servlet.PathUtil;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@WebServlet(urlPatterns = {"/auth/*"})
public class GitHubAuthServlet extends HttpServlet {

    @Inject
    AuthenticationService authenticationService;

    @Inject
    PathUtil pathUtil;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = pathUtil.getPathInfo(req).getPath();
        if (path.equals("/auth/login")) {

            AuthenticationRequest authenticationRequest =
                    authenticationService.recordNewAuthenticationRequest(
                            req.getParameter("path"),
                            req.getParameter("proxy.url"));

            resp.sendRedirect(

                    "https://github.com/login/oauth/authorize"
                            + "?client_id=" + GitHubAuthConstants.CLIENT_ID
                            + "&redirect_uri=http://localhost:8080/auth/token"
                            + "&state=" + authenticationRequest.getId());
            // TODO scopes
        } else if (path.equals("/auth/token")) {

            final String state = req.getParameter("state");
            AuthenticationRequest authenticationRequest = authenticationService.loadAuthenticationRequest(state);
            if (authenticationRequest == null) {
                throw new IllegalStateException("No ongoing request could be found");
            }

            final String code = req.getParameter("code");

            AccessTokenResponse token = authenticationService.getGitHubAccessToken(code, state);
            if (token.getAccessToken() == null) {
                resp.sendError(401, "Could not authenticate you");
            } else {
                authenticationService.storeToken(state, token);
                String toPath = "/token?uuid=" + state;
                if (authenticationRequest.getRequestedPath() != null) {
                    toPath += "&path=" + URLEncoder.encode(authenticationRequest.getRequestedPath(), "UTF-8");
                }
                if (authenticationRequest.getProxyUrl() != null) {
                    toPath = authenticationRequest.getProxyUrl() + toPath;
                }
                resp.sendRedirect(toPath);
            }
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = pathUtil.getPathInfo(req).getPath();
        if (path.equals("/auth/exchange")) {

            String uuid;
            try (JsonReader jsonReader = Json.createReader(req.getReader())){
                JsonObject jsonObject = jsonReader.readObject();
                uuid = jsonObject.getString("uuid");
            }

            AuthenticationRequest authenticationRequest = authenticationService.loadAuthenticationRequest(uuid);
            authenticationService.deleteAuthenticationRequest(uuid);
            if (authenticationRequest == null || authenticationRequest.getToken() == null) {
                resp.sendError(401, "No authentication in progress");
            } else {
                resp.setContentType(MediaType.APPLICATION_JSON);
                resp.setCharacterEncoding("UTF-8");
                final AccessTokenResponse token = authenticationRequest.getToken();
                final String tokenHeader = token.getTokenType() + " " + token.getAccessToken();

                GitHubUser user = authenticationService.validateGitHubToken(tokenHeader);
                user = authenticationService.exchangeApiUserForLocalUser(user);

                final JsonObject json = Json.createObjectBuilder()
                        .add("tokenHeader", tokenHeader)
                         .add("siteAdmin", user.getSiteAdmin() != null)
                        .add("user", user.convertToJsonObject())
                        .build();

                resp.getWriter().write(json.toString());
            }
        } else {
            resp.sendError(404);
        }
    }
}
