package com.linkito.estatisticas;

import com.linkito.servico.EstatisticasService;
import com.linkito.servico.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/stats")
public class EstatisticasController {

    private final EstatisticasService estatisticasService;
    private final LinkService linkService;

    public EstatisticasController(EstatisticasService estatisticasService, LinkService linkService) {
        this.estatisticasService = estatisticasService;
        this.linkService = linkService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        UUID usuarioId = buscarUsuarioAtualId();
        return ResponseEntity.ok(estatisticasService.montarDashboard(usuarioId));
    }

    @GetMapping("/links/{id}")
    public ResponseEntity<?> estatisticasLink(@PathVariable UUID id) {
        UUID usuarioId = buscarUsuarioAtualId();
        var linkOpt = linkService.buscarPorIdEUsuarioId(id, usuarioId);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(estatisticasService.buscarEstatisticasLink(linkOpt.get()));
    }

    private UUID buscarUsuarioAtualId() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao != null && autenticacao.getPrincipal() instanceof UUID usuarioId) {
            return usuarioId;
        }
        throw new IllegalStateException("Usuario autenticado nao encontrado");
    }
}
