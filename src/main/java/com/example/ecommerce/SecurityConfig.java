package com.example.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthHandler authHandler;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->{
                    authorize.requestMatchers("/", "/products", "/product", "/login", "/register", "/api/user/info", "/home/**", "/register-customer").permitAll();
                            authorize.requestMatchers("/css/**","/js/**", "/static/**", "/**.html", "/images/**", "/favicon.ico").permitAll();
                            authorize.requestMatchers("/admin/**").hasRole("ADMIN");
                            authorize.requestMatchers("/customer/**").hasAnyRole("USER");
                            authorize.anyRequest().authenticated();
                        }
                )
                .formLogin(formlogin -> formlogin
                        .loginPage("/login")
                        .loginProcessingUrl("/j_spring_security_check")
                        .successHandler(authHandler)
                        .failureUrl("/login?error")
                        .usernameParameter("j_login")
                        .passwordParameter("j_password")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // Specify that you want to use OAuth2 login on this page
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(authorities -> {
                                    // Creating a new collection for the authority
                                    List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authorities);
                                    // Add a new role
                                    updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                                    // Return the updated collection
                                    return updatedAuthorities;
                                }))
                        .successHandler(authHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout"))
                .exceptionHandling(exceptionHandling -> {
                    // Return 401 for API requests only (e.g. /customer/**)
                    // /*"If the request comes to a URL that starts with /customer/**,
                    // and if the user is unauthenticated, then don't redirect to the login page,
                    // just return 401 Unauthorized. "*/
                    exceptionHandling
                            .defaultAuthenticationEntryPointFor(
                                    (request, response, authException) -> {
                                        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                                    },
                                    new AntPathRequestMatcher("/customer/**")
                            )
                            .accessDeniedHandler(accessDeniedHandler);
                });


        return http.build();

    }
}