package com.linkito.servico;

import com.linkito.clique.CliqueLink;
import com.linkito.clique.CliqueLinkRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CliqueService {

    private final CliqueLinkRepository cliqueRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CliqueService(CliqueLinkRepository cliqueRepository) {
        this.cliqueRepository = cliqueRepository;
    }

    public void registrarClique(CliqueLink clique) {
        executor.submit(() -> cliqueRepository.save(clique));
    }

    @PreDestroy
    public void encerrar() {
        executor.shutdown();
    }
}
