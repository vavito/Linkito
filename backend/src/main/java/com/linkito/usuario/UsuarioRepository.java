package com.linkito.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query("select u from Usuario u where u.email = :email")
    Optional<Usuario> buscarPorEmail(String email);
}
