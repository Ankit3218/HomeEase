package com.homeease.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.homeease.dto.RegisterDto;
import com.homeease.entity.Role;
import com.homeease.entity.User;
import com.homeease.repository.RoleRepository;
import com.homeease.repository.UserRepository;

@Service
public class AuthService {
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User registerUser(RegisterDto dto) {
		
		User user= new User();
		
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		Role role=roleRepository.findByName("ROLE_USER");
		if(role==null) {
			role= new Role("ROLE_USER");
			roleRepository.save(role);
		}
		user.getRoles().add(role);
		userRepository.save(user);
		return user;
		
	}

}
