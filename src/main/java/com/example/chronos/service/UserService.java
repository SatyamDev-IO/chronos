package com.example.chronos.service;

import com.example.chronos.dto.UserDTO;
import com.example.chronos.entity.User;
import com.example.chronos.exception.BadRequestException;
import com.example.chronos.exception.UnauthorizedException;
import com.example.chronos.repository.UserRepository;
import com.example.chronos.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return TokenUtil.generateJwtToken(user);

    }


}
