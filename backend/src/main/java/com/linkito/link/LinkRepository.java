package com.linkito.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkEncurtado, UUID> {
    @Query("select l from LinkEncurtado l where l.codigoCurto = :codigoCurto")
    Optional<LinkEncurtado> buscarPorCodigoCurto(String codigoCurto);
    List<LinkEncurtado> findAllByUsuarioId(UUID usuarioId);
    List<LinkEncurtado> findAllByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);
    Optional<LinkEncurtado> findByIdAndUsuarioId(UUID id, UUID usuarioId);
    long countByUsuarioId(UUID usuarioId);
    Optional<LinkEncurtado> findFirstByUsuarioIdOrderByTotalCliquesDesc(UUID usuarioId);

    @Query("select coalesce(sum(l.totalCliques), 0) from LinkEncurtado l where l.usuarioId = :usuarioId")
    long somarCliquesPorUsuarioId(UUID usuarioId);
}
