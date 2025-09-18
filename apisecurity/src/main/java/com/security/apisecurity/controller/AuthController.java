package com.security.apisecurity.controller;

import com.security.apisecurity.model.AppUser;
import com.security.apisecurity.repository.UserRepository;
import com.security.apisecurity.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // chave secreta para criar admins (apenas para teste, usar variável de ambiente)
    @Value("${app.adminKey}")
    private String adminKey;

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // sempre ROLE_USER
        AppUser saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<AppUser> registerAdmin(@RequestBody Map<String, String> request) {
        String key = request.get("adminKey");
        if (key == null || !key.equals(adminKey)) {
            return ResponseEntity.status(403).build(); // Forbidden se chave errada
        }

        String username = request.get("username");
        String password = request.get("password");

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");

        AppUser saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AppUser user) {
        AppUser found = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(user.getPassword(), found.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        // Gera token incluindo a role
        String token = jwtUtil.generateToken(found.getUsername(), List.of(found.getRole()));
        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/validate")
    public Map<String, Object> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || !jwtUtil.validateToken(token)) {
            return Map.of("valid", false);
        }

        String username = jwtUtil.extractUsername(token);
        List<String> roles = jwtUtil.extractRoles(token);

        return Map.of(
                "valid", true,
                "username", username,
                "roles", roles
        );
    }
}

