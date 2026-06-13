package com.linkito.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.segredo:changeit-changeit-changeit-changeit-changeit}")
    private String segredoJwt;

    @Value("${jwt.expiracao-ms:3600000}")
    private long expiracaoJwtMs;

    public String gerarToken(String assunto) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoJwtMs);
        return Jwts.builder()
                .subject(assunto)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(buscarChaveAssinatura())
                .compact();
    }

    public String buscarAssuntoDoToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(buscarChaveAssinatura())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(buscarChaveAssinatura())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private SecretKey buscarChaveAssinatura() {
        return Keys.hmacShaKeyFor(segredoJwt.getBytes(StandardCharsets.UTF_8));
    }
}
