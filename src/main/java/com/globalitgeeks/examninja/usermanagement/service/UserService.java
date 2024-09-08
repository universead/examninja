package com.globalitgeeks.examninja.usermanagement.service;

import com.globalitgeeks.examninja.usermanagement.dto.ChangePasswordRequest;
import com.globalitgeeks.examninja.usermanagement.dto.LoginRequest;
import com.globalitgeeks.examninja.usermanagement.dto.UserRequest;
import com.globalitgeeks.examninja.usermanagement.model.User;
import com.globalitgeeks.examninja.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register a new user
    public User register(UserRequest userRequest) {
        validateUserRequest(userRequest);
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        return userRepository.save(user);
    }

    // Login user
    public User login(String email, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("Email not found");
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            throw new Exception("Invalid password");
        }
        return user;
    }


    // Change user password
    public User changePassword(String email, String newPassword) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }
        User user = userOpt.get();
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    // Validate user request fields
    private void validateUserRequest(UserRequest userRequest) {
        if (userRequest.getFirstName() == null || userRequest.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (userRequest.getLastName() == null || userRequest.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (userRequest.getEmail() == null || !isValidEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (userRequest.getPassword() == null || !isValidPassword(userRequest.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain 1 special character and 2 numbers.");
        }
    }

    // Check if email format is valid
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Check if password meets the requirements
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.chars().anyMatch(Character::isDigit) &&
                password.chars().anyMatch(ch -> "!@#$%^&*()_+{}|:<>?[];',./`~".indexOf(ch) >= 0);
    }
}
