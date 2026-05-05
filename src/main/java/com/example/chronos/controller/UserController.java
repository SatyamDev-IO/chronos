package com.example.chronos.controller;

import com.example.chronos.dto.UserDTO;
import com.example.chronos.entity.User;
import com.example.chronos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registorUser(@RequestBody UserDTO userDTO){
        return userService.registerUser(userDTO);
    }

    @PostMapping("/signin")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username, password);
    }

}
