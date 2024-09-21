package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// UserController.java
@RestController
@RequestMapping("/api/user")
public class UserRestController {
    /**
     * Retrieves information about the currently authenticated user.
     *
     * @param principal The principal object containing user authentication details.
     * @return A ResponseEntity containing a map with user details (username, roles, authentication status).
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal == null) {
            response.put("authenticated", false);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            response.put("authenticated", true);
            response.put("username", principal.getName());
            response.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(response);
    }
}
