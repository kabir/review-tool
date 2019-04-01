package org.overbaard.review.tool.security.github;

import java.util.UUID;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class AuthenticationRequest {

    private final UUID id;

    private final String requestedPath;

    private final String proxyUrl;

    private volatile String token;

    public AuthenticationRequest(String requestedPath, String proxyUrl) {
        this.id = UUID.randomUUID();
        this.requestedPath = requestedPath;
        this.proxyUrl = proxyUrl;
    }

    public UUID getId() {
        return id;
    }

    public String getRequestedPath() {
        return requestedPath;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
