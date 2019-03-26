package org.overbaard.review.tool.security.github;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class AccessTokenResponse {
    @JsonbProperty("access_token")
    String accessToken;

    String scope;

    @JsonbProperty("token_type")
    String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
