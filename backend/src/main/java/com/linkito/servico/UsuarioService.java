package com.linkito.servico;

import com.linkito.usuario.Usuario;
import com.linkito.usuario.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorSenha;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder codificadorSenha) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorSenha = codificadorSenha;
    }

    public Usuario cadastrar(String nome, String email, String senhaAberta) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(codificadorSenha.encode(senhaAberta));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }
}
