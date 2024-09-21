package com.example.ecommerce;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
@Component
public class AuthHandler implements AuthenticationSuccessHandler {
    @Autowired
    private CustomerService customerService;
    /**
     * Handles successful authentication.
     * If the user is authenticated via OAuth2 (Google), it checks if the user exists in the system.
     * If the user does not exist, it creates a new user with the role of "USER".
     * If the user exists, redirects to the login page with an error message.
     * After successful authentication, the user is redirected based on their role.
     *
     * @param request       The HttpServletRequest object.
     * @param response      The HttpServletResponse object.
     * @param authentication The Authentication object, containing user details and authorities.
     * @throws IOException      If an input or output exception occurs.
     * @throws ServletException If a servlet exception occurs.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Check if the user is authenticated via Google OAuth2
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2User user = token.getPrincipal();
            Map<String, Object> attributes = user.getAttributes();
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            // Check if the user exists in the database
            if (!customerService.userExists(email)) {
                CustomerDTO newCustomer = new CustomerDTO();
                newCustomer.setEmail(email);
                newCustomer.setFullName(name);
                newCustomer.setRole(Role.USER);
                customerService.createCustomer(newCustomer);
            }
            else{
                response.sendRedirect("/login?error=User already exists"); //wanted to add a message that this user already exists
                return;
            }
        }

        // Role-based redirection
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/");
        }
    }
}
