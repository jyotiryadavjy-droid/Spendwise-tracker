
package com.SpendWise.Tracker.repository;

import com.SpendWise.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByContactNumber(String contactNumber);

    boolean existsByEmail(String email);

    boolean existsByContactNumber(String contactNumber);
}
