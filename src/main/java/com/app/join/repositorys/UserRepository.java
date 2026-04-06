package com.app.join.repositorys;

import com.app.join.classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByEmail(String email);

    // Muss User zurückgeben, nicht UserDTO — JPA mapped immer auf die Entity-Klasse
    Optional<User> findByEmail(String email);
}
