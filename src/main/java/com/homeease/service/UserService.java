package com.homeease.service;

import com.homeease.config.EmailService;
import com.homeease.entity.Role;
import com.homeease.entity.User;
import com.homeease.repository.RoleRepository;
import com.homeease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    
    public User registerUser(User user) throws Exception {
        
    	
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new Exception("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            throw new Exception("Default role USER not found in database");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        String html = "<h2>Welcome to HomeEase!</h2><p>Hi " + user.getName() + ",<br/>Thank you for registering.</p>";
        emailService.sendHtmlEmail(user.getEmail(), "Welcome to HomeEase", html);

        return userRepository.save(user); 
        
        
        
    }

    
    public User loginUser(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }

        return user;
    }
    
    
    
}
