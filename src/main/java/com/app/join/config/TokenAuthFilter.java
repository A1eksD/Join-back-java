package com.app.join.config;

import com.app.join.classes.User;
import com.app.join.repositorys.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    // Dieser Filter läuft bei JEDEM eingehenden Request einmal durch.
    // Er prüft ob ein gültiger JWT im Authorization-Header steckt.

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. Kein Header oder falsches Format → direkt weiter, Spring Security blockt später
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. "Bearer " (7 Zeichen) abschneiden → reiner Token-String
        String token = authHeader.substring(7);

        // 3. Token validieren (Signatur + Ablaufzeit)
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. E-Mail aus dem Token lesen → kein DB-Lookup für die Validierung nötig!
        String email = jwtService.extractEmail(token);

        // 5. User aus DB laden (nur um das Authentication-Objekt zu befüllen)
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 6. User als "eingeloggt" in den Security-Kontext setzen
        // Spring Security weiß jetzt: dieser Request ist authentifiziert
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userOpt.get(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
