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

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@WebFilter(filterName = "indexFilter")
// url-pattern: "/*" set in web.xml
public class IndexHtmlFilter extends HttpFilter {

    @Inject
    PathUtil pathUtil;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        PathUtil.PathInfo pathInfo = pathUtil.getPathInfo(req);

        if (!pathUtil.getPathInfo(req).isIndexHtmlRequest()) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/").forward(request, response);
        }
    }
}
