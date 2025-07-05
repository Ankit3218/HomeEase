package com.homeease.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.homeease.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
User findByEmail(String email);

Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);


User findByResetToken(String token);

}





