package com.linkito.servico;

import com.linkito.clique.CliqueLink;
import com.linkito.clique.CliqueLinkRepository;
import com.linkito.dto.EstatisticasDtos;
import com.linkito.link.LinkEncurtado;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EstatisticasServiceTest {

    @Test
    void montarDashboardDeveRetornarTotaisDoUsuario() {
        LinkService linkService = Mockito.mock(LinkService.class);
        CliqueLinkRepository cliqueRepository = Mockito.mock(CliqueLinkRepository.class);
        UUID usuarioId = UUID.randomUUID();

        LinkEncurtado link = new LinkEncurtado();
        link.setUsuarioId(usuarioId);
        link.setTitulo("Docs");
        link.setTotalCliques(10);

        Mockito.when(linkService.contarPorUsuarioId(usuarioId)).thenReturn(1L);
        Mockito.when(linkService.somarCliquesPorUsuarioId(usuarioId)).thenReturn(10L);
        Mockito.when(linkService.buscarLinkMaisAcessadoPorUsuarioId(usuarioId)).thenReturn(Optional.of(link));
        Mockito.when(linkService.buscarUltimosPorUsuarioId(usuarioId)).thenReturn(List.of(link));

        EstatisticasService service = new EstatisticasService(linkService, cliqueRepository);
        EstatisticasDtos.RespostaDashboard resposta = service.montarDashboard(usuarioId);

        assertEquals(1L, resposta.totalLinks);
        assertEquals(10L, resposta.totalCliques);
        assertNotNull(resposta.linkMaisAcessado);
        assertEquals(1, resposta.ultimosLinks.size());
    }

    @Test
    void buscarEstatisticasLinkDeveRetornarCliques() {
        LinkService linkService = Mockito.mock(LinkService.class);
        CliqueLinkRepository cliqueRepository = Mockito.mock(CliqueLinkRepository.class);

        LinkEncurtado link = new LinkEncurtado();
        link.setCodigoCurto("abc12345");

        CliqueLink clique = new CliqueLink();
        clique.setLinkEncurtadoId(link.getId());

        Mockito.when(cliqueRepository.countByLinkEncurtadoId(link.getId())).thenReturn(1L);
        Mockito.when(cliqueRepository.findAllByLinkEncurtadoIdOrderByClicadoEmDesc(link.getId())).thenReturn(List.of(clique));

        EstatisticasService service = new EstatisticasService(linkService, cliqueRepository);
        EstatisticasDtos.RespostaEstatisticasLink resposta = service.buscarEstatisticasLink(link);

        assertEquals(link.getId(), resposta.linkId);
        assertEquals(1L, resposta.totalCliques);
        assertEquals(1, resposta.cliques.size());
    }
}
