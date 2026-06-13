package com.linkito.clique;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CliqueLinkRepository extends JpaRepository<CliqueLink, UUID> {
    long countByLinkEncurtadoId(UUID linkEncurtadoId);
    List<CliqueLink> findAllByLinkEncurtadoIdOrderByClicadoEmDesc(UUID linkEncurtadoId);
}
