
package com.SpendWise.Tracker.service.impl;

import com.SpendWise.Tracker.dto.RegisterRequest;
import com.SpendWise.Tracker.entity.User;
import com.SpendWise.Tracker.mapper.UserMapper;
import com.SpendWise.Tracker.repository.UserRepository;
import com.SpendWise.Tracker.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = new UserMapper();

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {

        if (request.getPassword() == null
                || request.getConfirmPassword() == null
                || !request.getPassword().equals(
                        request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered");
        }

        if (userRepository.existsByContactNumber(
                request.getContactNumber())) {
            throw new IllegalArgumentException(
                    "Contact number already registered");
        }

        User user = userMapper.toEntity(request);

        // Encode the password before storing it.
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        // Save the new user in the database.
        return userRepository.save(user);
    }
}
