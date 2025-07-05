package com.homeease.repository;


import com.homeease.entity.UserFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFlagRepository extends JpaRepository<UserFlag, Integer> {
   
}

