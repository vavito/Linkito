package com.linkito.clique;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "link_click")
@Data
@NoArgsConstructor
public class CliqueLink {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "short_link_id")
    private UUID linkEncurtadoId;

    @Column(name = "ip_address")
    private String enderecoIp;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String agenteUsuario;

    @Column(name = "clicked_at")
    private LocalDateTime clicadoEm = LocalDateTime.now();
}
