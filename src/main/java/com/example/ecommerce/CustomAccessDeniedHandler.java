package com.example.ecommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * Handles access denied exceptions by sending a 403 status code or redirecting the user based on their role and request path.
     *
     * @param request               The HttpServletRequest object.
     * @param response              The HttpServletResponse object.
     * @param accessDeniedException  The AccessDeniedException that triggered this handler.
     * @throws IOException           If an input or output exception occurs.
     * @throws ServletException      If a servlet exception occurs.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Set 403 Forbidden status
        Authentication authentication = (Authentication) request.getUserPrincipal();

        // Get the requested URL
        String uri = request.getRequestURI();
        String method = request.getMethod(); // Get the request method (GET, POST, etc.)
        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && method.equalsIgnoreCase("POST") && uri.startsWith("/customer/cart/items")) {
            // If the administrator tries to make a POST-request to /customer/cart/items, return Forbidden
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        else{
            response.sendRedirect("/");
        }
    }
}
