package com.homeease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homeease.entity.Role;

public interface RoleRepository extends  JpaRepository<Role, Long> {
	Role findByName(String name);

}
