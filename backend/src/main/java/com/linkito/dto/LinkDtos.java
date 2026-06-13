package com.linkito.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class LinkDtos {

    public static class RequisicaoCriarLink {
        @Size(max = 150)
        public String titulo;

        @NotBlank
        @Pattern(regexp = "^https?://.+", message = "urlOriginal deve comecar com http:// ou https://")
        public String urlOriginal;
    }

    public static class RequisicaoAtualizarLink {
        @Size(max = 150)
        public String titulo;
        @Pattern(regexp = "^https?://.+", message = "urlOriginal deve comecar com http:// ou https://")
        public String urlOriginal;
        public Boolean ativo;
    }

    public static class RespostaLink {
        public UUID id;
        public String titulo;
        public String urlOriginal;
        public String codigoCurto;
        public long totalCliques;
        public boolean ativo;
        public UUID usuarioId;
    }
}
