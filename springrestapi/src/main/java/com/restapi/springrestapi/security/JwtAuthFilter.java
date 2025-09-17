package com.restapi.springrestapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = header.substring(7);

        // 🔥 Chama o Auth Service
        URL url = new URL("http://localhost:8081/auth/validate"); // depois configuramos no K8s
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        String body = objectMapper.writeValueAsString(Map.of("token", token));
        conn.getOutputStream().write(body.getBytes());

        if (conn.getResponseCode() != 200) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<?, ?> result = objectMapper.readValue(conn.getInputStream(), Map.class);

        if (!(Boolean) result.get("valid")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // ✅ Token válido → segue a request
        filterChain.doFilter(request, response);
    }
}
