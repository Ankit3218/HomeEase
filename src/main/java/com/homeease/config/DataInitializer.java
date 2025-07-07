package com.homeease.config;

import com.homeease.entity.Role;

import com.homeease.entity.User;
import com.homeease.repository.RoleRepository;
import com.homeease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

 
    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            // Check and create roles
            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole == null) {
                adminRole = new Role("ADMIN");
                roleRepository.save(adminRole);
            }

            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                userRole = new Role("USER");
                roleRepository.save(userRole);
            }

            // Check and create admin user
            if (userRepository.findByEmail(adminEmail) == null) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);

                userRepository.save(admin);
                System.out.println("✅ Predefined admin user created.");
            } else {
                System.out.println("ℹ️ Admin user already exists.");
            }
        };
    }
}
