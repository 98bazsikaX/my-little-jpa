package com.example.databasemanager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rewrites RFC 10008 {@code QUERY /api/users} requests to {@code POST /api/users/search}
 * so they are handled by the standard search endpoint transparently.
 */
public class QueryMethodFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if ("QUERY".equalsIgnoreCase(request.getMethod()) && "/api/users".equals(request.getRequestURI())) {
            request = new HttpServletRequestWrapper(request) {
                @Override
                public String getMethod() {
                    return "POST";
                }

                @Override
                public String getRequestURI() {
                    return "/api/users/search";
                }

                @Override
                public String getServletPath() {
                    return "/api/users/search";
                }
            };
        }

        filterChain.doFilter(request, response);
    }
}
