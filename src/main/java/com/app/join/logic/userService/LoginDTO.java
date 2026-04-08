package com.app.join.logic.userService;

public record LoginDTO(
        String jwtToken,
        String userName
) {
}
