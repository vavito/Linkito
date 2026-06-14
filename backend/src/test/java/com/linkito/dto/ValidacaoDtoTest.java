package com.linkito.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidacaoDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void cadastroDeveRejeitarEmailInvalido() {
        var requisicao = new RequisicoesAutenticacao.RequisicaoCadastro();
        requisicao.nome = "Teste";
        requisicao.email = "email-invalido";
        requisicao.senha = "123456";

        assertFalse(validator.validate(requisicao).isEmpty());
    }

    @Test
    void cadastroDeveAceitarEmailValido() {
        var requisicao = new RequisicoesAutenticacao.RequisicaoCadastro();
        requisicao.nome = "Teste";
        requisicao.email = "teste@example.com";
        requisicao.senha = "123456";

        assertTrue(validator.validate(requisicao).isEmpty());
    }

    @Test
    void criarLinkDeveRejeitarUrlSemDominio() {
        var requisicao = new LinkDtos.RequisicaoCriarLink();
        requisicao.urlOriginal = "qualquer-coisa";

        assertFalse(validator.validate(requisicao).isEmpty());
    }

    @Test
    void criarLinkDeveAceitarUrlHttpOuHttps() {
        var http = new LinkDtos.RequisicaoCriarLink();
        http.urlOriginal = "http://example.com/campanha";

        var https = new LinkDtos.RequisicaoCriarLink();
        https.urlOriginal = "https://example.com?utm_source=teste";

        assertTrue(validator.validate(http).isEmpty());
        assertTrue(validator.validate(https).isEmpty());
    }
}
