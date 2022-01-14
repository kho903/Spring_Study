package com.example.jpa.user.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiUserInterestController {

    private final UserService userService;

    @PutMapping("/user/{id}/interest")
    public ResponseEntity<?> interestUser(
            @PathVariable Long id,
            @RequestHeader("K-TOKEN") String token) {

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        } catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        ServiceResult result = userService.addInterestUser(email, id);
        return ResponseResult.result(result);
    }

    @DeleteMapping("/user/{id}/interest")
    public ResponseEntity<?> deleteInterestUser(
            @PathVariable Long id,
            @RequestHeader("K-TOKEN") String token) {

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        } catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        ServiceResult result = userService.removeInterestUser(email, id);
        return ResponseResult.result(result);
    }
}
