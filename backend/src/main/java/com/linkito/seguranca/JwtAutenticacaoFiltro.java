package com.linkito.seguranca;

import com.linkito.usuario.Usuario;
import com.linkito.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAutenticacaoFiltro extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public JwtAutenticacaoFiltro(JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String cabecalho = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            String token = cabecalho.substring(7);
            if (jwtUtil.validarToken(token)) {
                String assunto = jwtUtil.buscarAssuntoDoToken(token);
                autenticarUsuario(assunto, request);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarUsuario(String assunto, HttpServletRequest request) {
        if (assunto == null) {
            return;
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(UUID.fromString(assunto));
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                        usuario.getId(), null, java.util.Collections.emptyList());
                autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
        } catch (IllegalArgumentException ignored) {
            // Token com id invalido: segue sem autenticar.
        }
    }
}
