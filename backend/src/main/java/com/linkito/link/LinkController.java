package com.linkito.link;

import com.linkito.dto.LinkDtos;
import com.linkito.servico.LinkService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody LinkDtos.RequisicaoCriarLink requisicao) {
        UUID usuarioId = buscarUsuarioAtualId();

        LinkEncurtado link = new LinkEncurtado();
        link.setTitulo(requisicao.titulo);
        link.setUrlOriginal(requisicao.urlOriginal);
        link.setUsuarioId(usuarioId);

        LinkEncurtado linkCriado = linkService.criar(link);
        return ResponseEntity.ok(paraResposta(linkCriado));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        UUID usuarioId = buscarUsuarioAtualId();
        List<LinkDtos.RespostaLink> resposta = linkService.buscarPorUsuarioId(usuarioId)
                .stream()
                .map(this::paraResposta)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalhar(@PathVariable UUID id) {
        UUID usuarioId = buscarUsuarioAtualId();
        return linkService.buscarPorIdEUsuarioId(id, usuarioId)
                .map(link -> ResponseEntity.ok(paraResposta(link)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable UUID id, @Valid @RequestBody LinkDtos.RequisicaoAtualizarLink requisicao) {
        UUID usuarioId = buscarUsuarioAtualId();
        var linkOpt = linkService.buscarPorIdEUsuarioId(id, usuarioId);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LinkEncurtado linkAtualizado = linkService.atualizar(
                linkOpt.get(),
                requisicao.titulo,
                requisicao.urlOriginal,
                requisicao.ativo);
        return ResponseEntity.ok(paraResposta(linkAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable UUID id) {
        UUID usuarioId = buscarUsuarioAtualId();
        var linkOpt = linkService.buscarPorIdEUsuarioId(id, usuarioId);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        linkService.remover(linkOpt.get());
        return ResponseEntity.noContent().build();
    }

    private UUID buscarUsuarioAtualId() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao != null && autenticacao.getPrincipal() instanceof UUID usuarioId) {
            return usuarioId;
        }
        throw new IllegalStateException("Usuario autenticado nao encontrado");
    }

    private LinkDtos.RespostaLink paraResposta(LinkEncurtado link) {
        LinkDtos.RespostaLink resposta = new LinkDtos.RespostaLink();
        resposta.id = link.getId();
        resposta.titulo = link.getTitulo();
        resposta.urlOriginal = link.getUrlOriginal();
        resposta.codigoCurto = link.getCodigoCurto();
        resposta.totalCliques = link.getTotalCliques();
        resposta.ativo = link.isAtivo();
        resposta.usuarioId = link.getUsuarioId();
        resposta.criadoEm = link.getCriadoEm();
        return resposta;
    }
}
