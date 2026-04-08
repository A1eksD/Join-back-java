package com.app.join.restcontroller;

import com.app.join.logic.userService.LoginDTO;
import com.app.join.logic.userService.RegisterUserService;
import com.app.join.logic.userService.UserDTO;
import com.app.join.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegisterUserRestController {

    @Autowired
    private RegisterUserService registerUserService;

    @PostMapping("/registerUser")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserDTO user) {
        try {
            String token = registerUserService.registerNewUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDTO>> login(@RequestBody UserDTO user) {
        try {
            LoginDTO dto = registerUserService.loginUser(user);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
