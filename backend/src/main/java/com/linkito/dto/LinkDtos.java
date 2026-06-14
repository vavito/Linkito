package com.linkito.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class LinkDtos {
    private static final String URL_PATTERN = "^https?://([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d{2,5})?([/?#]\\S*)?$";

    public static class RequisicaoCriarLink {
        @Size(max = 150)
        public String titulo;

        @NotBlank(message = "Informe a URL original.")
        @Pattern(regexp = URL_PATTERN, message = "Informe uma URL válida com http:// ou https://.")
        public String urlOriginal;
    }

    public static class RequisicaoAtualizarLink {
        @Size(max = 150)
        public String titulo;
        @Pattern(regexp = URL_PATTERN, message = "Informe uma URL válida com http:// ou https://.")
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
