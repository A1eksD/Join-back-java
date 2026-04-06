package com.app.join.restcontroller;

import com.app.join.logic.userService.RegisterUserService;
import com.app.join.logic.userService.UserDTO;
import com.app.join.logic.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registerUser")
public class RegisterUserRestController {

    @Autowired
    private RegisterUserService registerUserService;

    @PostMapping("/")
    private ResponseEntity<?> registerUser(@RequestBody UserDTO user){
        try {
            String token = registerUserService.registerNewUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    record TokenResponse(String token) {}
}
