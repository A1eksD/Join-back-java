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
        if (userRepository.existsByEmail(userDTO.email())) {
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
        userRepository.save(user);

        return jwtService.generateToken(user.getEmail());
    }

    public LoginDTO loginUser(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.email())
                .orElseThrow(() -> new SecurityException("Invalid credentials"));
        boolean matches = passwordEncoder.matches(userDTO.password(), user.getPassword());

        if (!matches) {
            throw new SecurityException("Invalid credentials");
        }

        String jwtToken = jwtService.generateToken(user.getEmail());
        String userName = user.getFirstName() + " " + user.getLastName();
        return new LoginDTO(jwtToken, userName);
    }
}
