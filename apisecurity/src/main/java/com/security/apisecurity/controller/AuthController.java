package com.security.apisecurity.controller;

import com.security.apisecurity.model.AppUser;
import com.security.apisecurity.repository.UserRepository;
import com.security.apisecurity.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser user) {
        // validações básicas omitted (ex: username único)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        AppUser saved = userRepository.save(user);
        // não retorna a senha por conta do @JsonProperty(WRITE_ONLY)
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/validate")
    public Map<String, Object> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || !jwtUtil.validateToken(token)) {
            return Map.of("valid", false);
        }

        String username = jwtUtil.extractUsername(token);

        return Map.of(
                "valid", true,
                "username", username
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AppUser user) {
        AppUser found = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(user.getPassword(), found.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtUtil.generateToken(found.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
