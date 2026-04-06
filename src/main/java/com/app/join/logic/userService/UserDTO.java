package com.app.join.logic.userService;

public record UserDTO(
        String firstName,
        String lastName,
        String color,
        String email,
        String password,
        Integer phone
) {
}
