package com.linkito.servico;

import com.linkito.clique.CliqueLink;
import com.linkito.clique.CliqueLinkRepository;
import com.linkito.dto.EstatisticasDtos;
import com.linkito.dto.LinkDtos;
import com.linkito.link.LinkEncurtado;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EstatisticasService {

    private final LinkService linkService;
    private final CliqueLinkRepository cliqueRepository;

    public EstatisticasService(LinkService linkService, CliqueLinkRepository cliqueRepository) {
        this.linkService = linkService;
        this.cliqueRepository = cliqueRepository;
    }

    public EstatisticasDtos.RespostaDashboard montarDashboard(UUID usuarioId) {
        EstatisticasDtos.RespostaDashboard resposta = new EstatisticasDtos.RespostaDashboard();
        resposta.totalLinks = linkService.contarPorUsuarioId(usuarioId);
        resposta.totalCliques = linkService.somarCliquesPorUsuarioId(usuarioId);
        resposta.linkMaisAcessado = linkService.buscarLinkMaisAcessadoPorUsuarioId(usuarioId)
                .map(this::paraRespostaLink)
                .orElse(null);
        resposta.ultimosLinks = linkService.buscarUltimosPorUsuarioId(usuarioId)
                .stream()
                .limit(5)
                .map(this::paraRespostaLink)
                .toList();
        return resposta;
    }

    public EstatisticasDtos.RespostaEstatisticasLink buscarEstatisticasLink(LinkEncurtado link) {
        EstatisticasDtos.RespostaEstatisticasLink resposta = new EstatisticasDtos.RespostaEstatisticasLink();
        resposta.linkId = link.getId();
        resposta.codigoCurto = link.getCodigoCurto();
        resposta.totalCliques = cliqueRepository.countByLinkEncurtadoId(link.getId());
        resposta.cliques = cliqueRepository.findAllByLinkEncurtadoIdOrderByClicadoEmDesc(link.getId())
                .stream()
                .map(this::paraRespostaClique)
                .toList();
        return resposta;
    }

    private LinkDtos.RespostaLink paraRespostaLink(LinkEncurtado link) {
        LinkDtos.RespostaLink resposta = new LinkDtos.RespostaLink();
        resposta.id = link.getId();
        resposta.titulo = link.getTitulo();
        resposta.urlOriginal = link.getUrlOriginal();
        resposta.codigoCurto = link.getCodigoCurto();
        resposta.totalCliques = link.getTotalCliques();
        resposta.ativo = link.isAtivo();
        resposta.usuarioId = link.getUsuarioId();
        return resposta;
    }

    private EstatisticasDtos.RespostaClique paraRespostaClique(CliqueLink clique) {
        EstatisticasDtos.RespostaClique resposta = new EstatisticasDtos.RespostaClique();
        resposta.id = clique.getId();
        resposta.enderecoIp = clique.getEnderecoIp();
        resposta.agenteUsuario = clique.getAgenteUsuario();
        resposta.clicadoEm = clique.getClicadoEm();
        return resposta;
    }
}
