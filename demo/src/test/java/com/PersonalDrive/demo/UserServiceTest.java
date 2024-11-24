package com.PersonalDrive.demo;

import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.repositories.UserRepository;
import com.PersonalDrive.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BCryptPasswordEncoder BPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserById() {

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("John Doe");
        mockUser.setPassword("password");

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        User user = userService.getById(1);

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testFindUserByName() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("John Doe");
        mockUser.setPassword("password");

        when(userRepository.findByName("John Doe")).thenReturn(List.of(mockUser));

        User user = userService.getByName("John Doe");

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testRegisterUser(){
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setPassword("plaintext");
        String encryptedPassword = passwordEncoder.encode("plaintext");
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plaintext")).thenReturn(encryptedPassword);

        boolean result = userService.registerUser(user);

        assertTrue(result);
        assertEquals(encryptedPassword, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testLoginUser() {
        String encryptedPassword = BPasswordEncoder.encode("plaintext");

        assertTrue(BPasswordEncoder.matches("plaintext", encryptedPassword));
    }
}
