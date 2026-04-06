package com.app.join.restcontroller;

import com.app.join.classes.User;
import com.app.join.config.JwtService;
import com.app.join.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1. User anhand der E-Mail suchen
        Optional<User> userOpt = userRepository.findByEmail(request.email());

        // 2. Kein User gefunden → 401 (bewusst vage, damit man nicht raten kann ob E-Mail existiert)
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();

        // 3. Eingegebenes Passwort gegen den BCrypt-Hash in der DB prüfen
        // passwordEncoder.matches() hasht das eingegebene PW und vergleicht es mit dem gespeicherten Hash
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // 4. Alles ok → JWT generieren und zurückschicken
        // Der Token enthält die E-Mail und läuft nach der konfigurierten Zeit ab.
        // Kein Speichern in der DB nötig!
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    record LoginRequest(String email, String password) {}
    record TokenResponse(String token) {}
}
