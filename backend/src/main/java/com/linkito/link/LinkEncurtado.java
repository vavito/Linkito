package com.linkito.link;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "short_link")
@Data
@NoArgsConstructor
public class LinkEncurtado {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "title", length = 150)
    private String titulo;

    @Column(name = "original_url", columnDefinition = "TEXT", nullable = false)
    private String urlOriginal;

    @Column(name = "short_code", unique = true, nullable = false, length = 10)
    private String codigoCurto;

    @Column(name = "click_count")
    private long totalCliques = 0L;

    @Column(name = "active")
    private boolean ativo = true;

    @Column(name = "user_id")
    private UUID usuarioId;

    @Column(name = "created_at")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm = LocalDateTime.now();
}
