package org.overbaard.review.tool.security.github;

import java.util.UUID;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class AuthenticationRequest {

    private final UUID id;

    private final String requestedPath;

    private volatile String token;

    public AuthenticationRequest(String requestedPath) {
        this.id = UUID.randomUUID();
        this.requestedPath = requestedPath;
    }

    public UUID getId() {
        return id;
    }

    public String getRequestedPath() {
        return requestedPath;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
