package com.PersonalDrive.demo.services;


import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getById(int id) {
        return userRepository.findById(id).get();
    }

    public User getByName(String name){
        try{
            List<User> result = userRepository.findByName(name);
            return result.getFirst();
        }catch (Exception e){
            return null;
        }

    }

    public boolean registerUser(User user){
        try{
            if(!userRepository.findById(user.getId()).isEmpty()) return false;
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    public boolean loginUser(User user){
        try{
            return passwordEncoder.matches(user.getPassword(), this.getByName(user.getName()).getPassword());
        }catch (Exception e){
            return false;
        }
    }
}
