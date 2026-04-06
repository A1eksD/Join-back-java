package com.app.join.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Secret und Ablaufzeit werden aus application.properties gelesen
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // ─── Token erstellen ─────────────────────────────────────────────────────────
    // Wird nach Login/Register aufgerufen.
    // "subject" ist die E-Mail des Users — damit kann man ihn später wieder
    // identifizieren, ohne die User-ID preiszugeben.

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                                    // Wer ist der User?
                .issuedAt(new Date())                              // Wann wurde der Token ausgestellt?
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // Wann läuft er ab?
                .signWith(getSigningKey())                         // Token mit Secret signieren (HS256)
                .compact();                                        // Alles zu einem String zusammenbauen
    }

    // ─── E-Mail aus Token lesen ───────────────────────────────────────────────────
    // Der Token enthält die E-Mail verschlüsselt im "subject"-Feld.
    // Diese Methode entschlüsselt und liest sie aus.

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // ─── Token validieren ────────────────────────────────────────────────────────
    // Prüft ob:
    //   1. Die Signatur stimmt (wurde nicht manipuliert)
    //   2. Der Token noch nicht abgelaufen ist

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date()); // Noch nicht abgelaufen?
        } catch (Exception e) {
            // Ungültige Signatur, abgelaufen oder falsches Format → false
            return false;
        }
    }

    // ─── Claims parsen (intern) ───────────────────────────────────────────────────
    // "Claims" = der Inhalt des Tokens (subject, issuedAt, expiration, ...)
    // Wirft eine Exception wenn Signatur falsch oder Token abgelaufen ist.

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Signatur mit Secret prüfen
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ─── Signing Key ──────────────────────────────────────────────────────────────
    // Wandelt den Secret-String in einen kryptografischen Schlüssel um.
    // HMAC-SHA256 wird zum Signieren verwendet.

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
