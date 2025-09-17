package com.security.apisecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProtectedController {

    @GetMapping("/protegido")
    public Map<String, String> protegido(Principal principal) {
        // retorna o username extraído do SecurityContext (definido pelo filtro JWT)
        return Map.of(
                "message", "Acesso autorizado",
                "user", principal != null ? principal.getName() : "anonymous"
        );
    }
}
