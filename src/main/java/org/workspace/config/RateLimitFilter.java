package org.workspace.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMIT = 30;
    private static final ConcurrentHashMap<String, AtomicInteger> COUNTER = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/v1/chat")) {
            COUNTER.putIfAbsent(request.getRemoteAddr(), new AtomicInteger(0));
            int count = COUNTER.get(request.getRemoteAddr()).incrementAndGet();

            if (count > LIMIT) {
                response.setStatus(429);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
