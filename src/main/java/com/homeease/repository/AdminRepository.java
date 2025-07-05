package com.homeease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homeease.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
Admin findByUsernameAndPassword(String username, String password);

}
