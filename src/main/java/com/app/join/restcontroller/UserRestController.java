package com.app.join.restcontroller;

import com.app.join.classes.User;
import com.app.join.logic.userService.UserDTO;
import com.app.join.logic.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/createNewUser")
    public HttpStatus createNewUser(@RequestBody User user){
        userService.createUser(user);
        return HttpStatus.CREATED;
    }

    @PutMapping("/{id}")
    public HttpStatus updateNewUser(@PathVariable Integer id, @Validated @RequestBody UserDTO user){
        userService.updateUserById(id, user);
        return HttpStatus.OK;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNewUser(@PathVariable Integer id){
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
