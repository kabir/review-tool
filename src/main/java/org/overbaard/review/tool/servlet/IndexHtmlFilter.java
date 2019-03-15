package org.overbaard.review.tool.servlet;

import java.io.IOException;
import java.util.regex.Pattern;

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
@WebFilter("/*")
public class IndexHtmlFilter extends HttpFilter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");

        Pattern pattern = Pattern.compile(".*[.][a-zA-Z\\d]+");

        if (path.equals("") || pattern.matcher(path).matches()
            || path.startsWith("/api/")) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/index.html").forward(request, response);
        }
    }
}
