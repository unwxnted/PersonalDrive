package com.PersonalDrive.demo.controllers;

import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.security.JwtTokenProvider;
import com.PersonalDrive.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserContoller {


    @Autowired
    UserService userService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String test(){
        return "Hello World";
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        try{
            if(!userService.loginUser(user)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");

            return ResponseEntity.ok(jwtTokenProvider.generateToken(user));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {

            if (!userService.registerUser(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }

            return ResponseEntity.ok(jwtTokenProvider.generateToken(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

}
