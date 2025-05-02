package org.healeasy.repositories;

import org.healeasy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameOrEmail(String username, String email);
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
