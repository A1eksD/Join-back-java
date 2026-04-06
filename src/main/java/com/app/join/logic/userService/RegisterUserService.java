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

        // Prüfen ob E-Mail schon vergeben ist
        if (userRepository.existsByEmail(userDTO.email())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(userDTO.email());
        user.setPhone(userDTO.phone());
        user.setColor(userDTO.color());
        user.setStatus(true);
        user.setPassword(passwordEncoder.encode(userDTO.password()));

        userRepository.save(user);

        // JWT generieren — enthält die E-Mail als Subject, läuft nach X Stunden ab.
        return jwtService.generateToken(user.getEmail());
    }
}
