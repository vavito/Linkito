package com.linkito.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "name", nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String senhaHash;

    @Column(name = "role")
    private String perfil = "USUARIO";

    @Column(name = "created_at")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm = LocalDateTime.now();
}
