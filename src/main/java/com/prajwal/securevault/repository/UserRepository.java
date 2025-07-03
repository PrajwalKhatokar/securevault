package com.prajwal.securevault.repository;

import com.prajwal.securevault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber); // ✅ Phone recovery

    // ✅ Needed by AfterLifeScheduler
    List<User> findByLastLoginBeforeAndTrustedEmailIsNotNull(LocalDateTime dateTime);
}
