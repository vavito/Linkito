package com.linkito.servico;

import com.linkito.link.LinkEncurtado;
import com.linkito.link.LinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class LinkService {

    private final LinkRepository linkRepository;
    private final Random random = new Random();

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public LinkEncurtado criar(LinkEncurtado link) {
        String codigo = gerarCodigoCurto();
        int tentativas = 0;

        while (linkRepository.buscarPorCodigoCurto(codigo).isPresent() && tentativas < 5) {
            codigo = gerarCodigoCurto();
            tentativas++;
        }

        if (tentativas == 5 && linkRepository.buscarPorCodigoCurto(codigo).isPresent()) {
            throw new IllegalStateException("Nao foi possivel gerar um codigo curto unico");
        }

        link.setCodigoCurto(codigo);
        return linkRepository.save(link);
    }

    public List<LinkEncurtado> buscarPorUsuarioId(UUID usuarioId) {
        return linkRepository.findAllByUsuarioId(usuarioId);
    }

    public List<LinkEncurtado> buscarUltimosPorUsuarioId(UUID usuarioId) {
        return linkRepository.findAllByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public Optional<LinkEncurtado> buscarPorCodigoCurto(String codigoCurto) {
        return linkRepository.buscarPorCodigoCurto(codigoCurto);
    }

    public Optional<LinkEncurtado> buscarPorIdEUsuarioId(UUID id, UUID usuarioId) {
        return linkRepository.findByIdAndUsuarioId(id, usuarioId);
    }

    public LinkEncurtado atualizar(LinkEncurtado link, String titulo, String urlOriginal, Boolean ativo) {
        if (titulo != null) {
            link.setTitulo(titulo);
        }
        if (urlOriginal != null && !urlOriginal.isBlank()) {
            link.setUrlOriginal(urlOriginal);
        }
        if (ativo != null) {
            link.setAtivo(ativo);
        }

        link.setAtualizadoEm(LocalDateTime.now());
        return linkRepository.save(link);
    }

    public void remover(LinkEncurtado link) {
        linkRepository.delete(link);
    }

    public void incrementarTotalCliques(LinkEncurtado link) {
        link.setTotalCliques(link.getTotalCliques() + 1);
        link.setAtualizadoEm(LocalDateTime.now());
        linkRepository.save(link);
    }

    public long contarPorUsuarioId(UUID usuarioId) {
        return linkRepository.countByUsuarioId(usuarioId);
    }

    public long somarCliquesPorUsuarioId(UUID usuarioId) {
        return linkRepository.somarCliquesPorUsuarioId(usuarioId);
    }

    public Optional<LinkEncurtado> buscarLinkMaisAcessadoPorUsuarioId(UUID usuarioId) {
        return linkRepository.findFirstByUsuarioIdOrderByTotalCliquesDesc(usuarioId);
    }

    private String gerarCodigoCurto() {
        String caracteres = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return codigo.toString();
    }
}
