package com.linkito.servico;

import com.linkito.usuario.Usuario;
import com.linkito.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsuarioServiceTest {

    @Test
    void cadastrarDeveCriptografarSenhaESalvar() {
        UsuarioRepository repository = Mockito.mock(UsuarioRepository.class);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        UsuarioService service = new UsuarioService(repository, new BCryptPasswordEncoder());
        Usuario usuario = service.cadastrar("Alice", "a@a.com", "secret");

        assertNotNull(usuario.getSenhaHash());
        assertNotEquals("secret", usuario.getSenhaHash());
    }

    @Test
    void buscarPorEmailDeveChamarRepositorio() {
        UsuarioRepository repository = Mockito.mock(UsuarioRepository.class);
        Mockito.when(repository.buscarPorEmail("x@x.com")).thenReturn(Optional.empty());

        UsuarioService service = new UsuarioService(repository, new BCryptPasswordEncoder());

        assertTrue(service.buscarPorEmail("x@x.com").isEmpty());
    }
}
