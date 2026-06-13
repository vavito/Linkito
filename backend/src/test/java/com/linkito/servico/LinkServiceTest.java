package com.linkito.servico;

import com.linkito.link.LinkEncurtado;
import com.linkito.link.LinkRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinkServiceTest {

    @Test
    void criarDeveGerarCodigoCurto() {
        LinkRepository repository = Mockito.mock(LinkRepository.class);
        Mockito.when(repository.buscarPorCodigoCurto(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        LinkService service = new LinkService(repository);
        LinkEncurtado link = new LinkEncurtado();
        link.setUrlOriginal("https://example.com/some/path");

        LinkEncurtado linkCriado = service.criar(link);

        assertNotNull(linkCriado.getCodigoCurto());
        assertEquals(8, linkCriado.getCodigoCurto().length());
    }

    @Test
    void buscarPorIdEUsuarioIdDeveChamarRepositorio() {
        LinkRepository repository = Mockito.mock(LinkRepository.class);
        UUID linkId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        LinkEncurtado link = new LinkEncurtado();

        Mockito.when(repository.findByIdAndUsuarioId(linkId, usuarioId)).thenReturn(Optional.of(link));

        LinkService service = new LinkService(repository);

        assertTrue(service.buscarPorIdEUsuarioId(linkId, usuarioId).isPresent());
        Mockito.verify(repository).findByIdAndUsuarioId(linkId, usuarioId);
    }

    @Test
    void atualizarDeveAlterarSomenteCamposInformados() {
        LinkRepository repository = Mockito.mock(LinkRepository.class);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        LinkService service = new LinkService(repository);
        LinkEncurtado link = new LinkEncurtado();
        link.setTitulo("Antigo");
        link.setUrlOriginal("https://old.com");
        link.setAtivo(true);

        LinkEncurtado atualizado = service.atualizar(link, "Novo", null, false);

        assertEquals("Novo", atualizado.getTitulo());
        assertEquals("https://old.com", atualizado.getUrlOriginal());
        assertFalse(atualizado.isAtivo());
    }
}
