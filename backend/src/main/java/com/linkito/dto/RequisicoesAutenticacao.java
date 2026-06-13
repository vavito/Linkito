package com.linkito.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequisicoesAutenticacao {

    public static class RequisicaoCadastro {
        @NotBlank
        public String nome;

        @Email
        @NotBlank
        public String email;

        @NotBlank
        @Size(min = 6)
        public String senha;
    }

    public static class RequisicaoLogin {
        @Email
        @NotBlank
        public String email;

        @NotBlank
        public String senha;
    }
}
