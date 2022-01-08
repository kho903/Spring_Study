package com.example.jpa.user.repository;

import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {

}
