package com.linkito.dto;

import java.util.UUID;

public class RespostasAutenticacao {

    public static class RespostaLogin {
        public String token;
        public String tipoToken = "Bearer";

        public RespostaLogin(String token) {
            this.token = token;
        }
    }

    public static class RespostaCadastro {
        public UUID id;
        public String nome;
        public String email;

        public RespostaCadastro(UUID id, String nome, String email) {
            this.id = id;
            this.nome = nome;
            this.email = email;
        }
    }

    public static class RespostaPerfil {
        public UUID id;
        public String nome;
        public String email;
        public String perfil;

        public RespostaPerfil(UUID id, String nome, String email, String perfil) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.perfil = perfil;
        }
    }
}
