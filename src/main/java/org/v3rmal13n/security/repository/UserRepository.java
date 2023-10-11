package org.v3rmal13n.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.v3rmal13n.security.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
