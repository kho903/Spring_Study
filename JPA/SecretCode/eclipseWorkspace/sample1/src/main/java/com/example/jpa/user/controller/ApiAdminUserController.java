package com.example.jpa.user.controller;

import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.ResponseMessage;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiAdminUserController {

    private final UserRepository userRepository;

    @GetMapping("/api/admin/user")
    public ResponseMessage userList() {
        List<User> userList = userRepository.findAll();
        Long totalUserCount = userRepository.count();

        return ResponseMessage.builder()
                .totalCount(totalUserCount)
                .data(userList)
                .build();

//        return userList;
    }
}
