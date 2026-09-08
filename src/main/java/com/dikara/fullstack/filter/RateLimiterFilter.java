package com.dikara.fullstack.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final long LIMIT_INTERVAL = 5000; // 5 detik

    private final Map<String, Long> requestMap =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String method = request.getMethod();

        // hanya POST, PUT, DELETE
        if (!method.equals("POST")
                && !method.equals("PUT")
                && !method.equals("DELETE")) {

            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getRequestURI();

        Long lastRequestTime = requestMap.get(key);

        long currentTime = System.currentTimeMillis();

        if (lastRequestTime != null
                && (currentTime - lastRequestTime) < LIMIT_INTERVAL) {

            response.setStatus(429);
            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "statusCode": 429,
                    "message": "Request allowed only once every 5 seconds"
                }
                """);

            return;
        }

        requestMap.put(key, currentTime);

        filterChain.doFilter(request, response);
    }
}