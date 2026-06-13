package com.linkito.redirecionamento;

import com.linkito.clique.CliqueLink;
import com.linkito.servico.CliqueService;
import com.linkito.servico.LinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirecionamentoController {

    private final LinkService linkService;
    private final CliqueService cliqueService;

    public RedirecionamentoController(LinkService linkService, CliqueService cliqueService) {
        this.linkService = linkService;
        this.cliqueService = cliqueService;
    }

    @GetMapping("/r/{codigoCurto}")
    public ResponseEntity<?> redirecionar(@PathVariable String codigoCurto,
                                          @RequestHeader(value = "User-Agent", required = false) String agenteUsuario,
                                          @RequestHeader(value = "X-Forwarded-For", required = false) String enderecoIp) {
        var linkOpt = linkService.buscarPorCodigoCurto(codigoCurto);
        if (linkOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var link = linkOpt.get();
        if (!link.isAtivo()) {
            return ResponseEntity.status(410).build();
        }

        CliqueLink clique = new CliqueLink();
        clique.setLinkEncurtadoId(link.getId());
        clique.setAgenteUsuario(agenteUsuario);
        clique.setEnderecoIp(enderecoIp);
        cliqueService.registrarClique(clique);
        linkService.incrementarTotalCliques(link);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(link.getUrlOriginal()));
        return ResponseEntity.status(302).headers(headers).build();
    }
}
