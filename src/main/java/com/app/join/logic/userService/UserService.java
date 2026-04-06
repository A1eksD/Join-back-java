package com.app.join.logic.userService;

import com.app.join.classes.User;
import com.app.join.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // TODO: add pagination later
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getColor(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone()
        );
    }

    public void createUser(User newUser){
        try {
            User user = new User();
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setEmail(newUser.getEmail());
            user.setPhone(newUser.getPhone());
            user.setColor(newUser.getColor());
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create user --->", e);
        }
    }

    public void updateUserById(Integer id, UserDTO userInfo){
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setFirstName(userInfo.firstName());
                user.setLastName(userInfo.lastName());
                user.setColor(userInfo.color());
                user.setEmail(userInfo.email());
                user.setPhone(userInfo.phone());
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user --->", e);
        }
    }

    public void deleteUserById(Integer id){
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user --->", e);
        }
    }
}
