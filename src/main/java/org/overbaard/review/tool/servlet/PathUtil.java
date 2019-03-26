package org.overbaard.review.tool.servlet;

import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@RequestScoped
public class PathUtil {
    private boolean initialised;

    private PathInfo pathInfo;
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(".*[.][a-zA-Z\\d]+");

    public PathInfo getPathInfo(ServletRequest req) {
        if (pathInfo == null) {
            HttpServletRequest request = (HttpServletRequest) req;
            String path = request.getRequestURI().substring(
                    request.getContextPath().length()).replaceAll("[/]+$", "");

            pathInfo = new PathInfo(
                    path,
                    FILE_NAME_PATTERN.matcher(path).matches());

        }
        return pathInfo;
    }

    public static class PathInfo {
        private final String path;
        private final boolean file;

        public PathInfo(String path, boolean file) {
            this.path = path;
            this.file = file;
        }

        public String getPath() {
            return path;
        }

        boolean isDirectoryResource() {
            return !file;
        }

        boolean isIndexHtmlRequest() {
            return !(path.equals("") ||
                    file ||
                    isRestCall() ||
                    isAuthCall());
        }

        boolean isRestCall() {
            return path.startsWith("/api/");
        }

        boolean isAuthCall() {
            return path.startsWith("/auth/");
        }
    }
}
