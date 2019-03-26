package org.overbaard.review.tool.security.github;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GitHubAuthConstants {
    public static final String CLIENT_ID;
    public static final String CLIENT_SECRET;

    static {
        CLIENT_ID = System.getenv("GITHUB_CLIENT_ID");
        CLIENT_SECRET = System.getenv("GITHUB_CLIENT_SECRET");

        if (CLIENT_ID == null || CLIENT_ID.length() == 0) {
            throw new RuntimeException("No GITHUB_CLIENT_ID system property set");
        }
        if (CLIENT_SECRET == null || CLIENT_SECRET.length() == 0) {
            throw new RuntimeException("No GITHUB_CLIENT_SECRET system property set");
        }
    }

}
