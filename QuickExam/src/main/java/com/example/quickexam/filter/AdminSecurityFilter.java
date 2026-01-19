package com.example.quickexam.filter;

import com.example.quickexam.controller.UserController;
import com.example.quickexam.session.UserSession;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter({
        "/views/admin/*",
        "/faces/views/admin/*",
        "/admin-dashboard",
        "/admin-dashboard/*"
})
public class AdminSecurityFilter implements Filter {
    @Inject
    private UserSession userSession;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (userSession.getCurrentUser() == null ||
                !userSession.isLoggedIn()) {
            String redirectURL = contextPath + "/views/auth/login.xhtml?redirect=" +
                    java.net.URLEncoder.encode(path, "UTF-8");
            httpResponse.sendRedirect(redirectURL);
            return;
        }

        if (!userSession.isValidated()) {
            httpResponse.sendRedirect(contextPath + "/views/auth/verify.xhtml");
            return;
        }

        if (!userSession.isAdmin()) {
            System.out.println("Access denied: This section is reserved for administrators.");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied: This section is reserved for administrators.");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicResource(String path) {
        return path.startsWith("/javax.faces.resource") ||
                path.startsWith("/resources") ||
                path.contains(".css") ||
                path.contains(".js") ||
                path.contains(".png") ||
                path.contains(".jpg") ||
                path.contains(".gif") ||
                path.contains(".ico");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
