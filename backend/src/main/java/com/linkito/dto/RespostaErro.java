package com.linkito.dto;

import java.util.Map;

public class RespostaErro {
    public String mensagem;
    public Map<String, String> campos;

    public RespostaErro(String mensagem) {
        this.mensagem = mensagem;
    }

    public RespostaErro(String mensagem, Map<String, String> campos) {
        this.mensagem = mensagem;
        this.campos = campos;
    }
}
