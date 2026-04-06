package com.app.join.logic.userService;

import com.app.join.classes.User;
import com.app.join.config.JwtService;
import com.app.join.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String registerNewUser(UserDTO userDTO) {
        System.out.println("=== REGISTER ATTEMPT ===");
        System.out.println("Email:     " + userDTO.email());
        System.out.println("FirstName: " + userDTO.firstName());
        System.out.println("Password:  " + (userDTO.password() != null ? "NOT NULL" : "NULL"));

        if (userRepository.existsByEmail(userDTO.email())) {
            System.out.println("==> Email already exists!");
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(userDTO.email());
        user.setPhone(userDTO.phone());
        user.setColor(userDTO.color());
        user.setStatus(true);
        user.setPassword(passwordEncoder.encode(userDTO.password()));

        User saved = userRepository.save(user);
        System.out.println("==> User saved with ID: " + saved.getId());
        System.out.println("==> Saved email: " + saved.getEmail());

        return jwtService.generateToken(user.getEmail());
    }

    public String loginUser(UserDTO userDTO) {
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email received:    " + userDTO.email());
        System.out.println("Password received: " + (userDTO.password() != null ? "NOT NULL" : "NULL"));

        User user = userRepository.findByEmail(userDTO.email())
                .orElseThrow(() -> {
                    System.out.println("==> User NOT found in DB for email: " + userDTO.email());
                    return new SecurityException("Invalid credentials");
                });

        System.out.println("==> User found: " + user.getEmail());
        System.out.println("==> Password in DB: " + (user.getPassword() != null ? "NOT NULL" : "NULL"));

        boolean matches = passwordEncoder.matches(userDTO.password(), user.getPassword());
        System.out.println("==> Password matches: " + matches);

        if (!matches) {
            throw new SecurityException("Invalid credentials");
        }

        return jwtService.generateToken(user.getEmail());
    }
}
