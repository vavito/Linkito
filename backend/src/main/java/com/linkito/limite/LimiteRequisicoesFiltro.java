package com.linkito.limite;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LimiteRequisicoesFiltro extends OncePerRequestFilter {

    private final Map<String, ContadorSimples> contadoresLogin = new ConcurrentHashMap<>();
    private final Map<String, ContadorSimples> contadoresLink = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String caminho = request.getRequestURI();
        String ip = request.getRemoteAddr();

        if ("/api/auth/login".equals(caminho)) {
            if (estaPermitido(contadoresLogin, ip, 5)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            return;
        }

        if ("/api/links".equals(caminho) && "POST".equalsIgnoreCase(request.getMethod())) {
            var autenticacao = SecurityContextHolder.getContext().getAuthentication();
            String chave = ip;
            if (autenticacao != null && autenticacao.getPrincipal() != null) {
                chave = autenticacao.getPrincipal().toString();
            }

            if (estaPermitido(contadoresLink, chave, 30)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean estaPermitido(Map<String, ContadorSimples> contadores, String chave, int limite) {
        long agora = System.currentTimeMillis();
        ContadorSimples contador = contadores.computeIfAbsent(chave, ignorado -> new ContadorSimples(agora));

        synchronized (contador) {
            if (agora - contador.inicioJanela >= 60_000) {
                contador.inicioJanela = agora;
                contador.requisicoes = 0;
            }

            if (contador.requisicoes >= limite) {
                return false;
            }

            contador.requisicoes++;
            return true;
        }
    }

    private static class ContadorSimples {
        private long inicioJanela;
        private int requisicoes;

        private ContadorSimples(long inicioJanela) {
            this.inicioJanela = inicioJanela;
        }
    }
}
