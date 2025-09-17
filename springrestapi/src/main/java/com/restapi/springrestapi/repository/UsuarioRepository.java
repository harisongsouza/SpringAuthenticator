package com.restapi.springrestapi.repository;

import com.restapi.springrestapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}

