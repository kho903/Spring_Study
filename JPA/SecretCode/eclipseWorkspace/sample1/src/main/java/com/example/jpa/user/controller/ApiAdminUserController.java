package com.example.jpa.user.controller;

import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserLoginHistory;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.ResponseMessage;
import com.example.jpa.user.model.UserNoticeCount;
import com.example.jpa.user.model.UserSearch;
import com.example.jpa.user.model.UserStatusInput;
import com.example.jpa.user.model.UserSummary;
import com.example.jpa.user.repository.UserLoginHistoryRepository;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ApiAdminUserController {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;

    private final UserService userService;

    /*@GetMapping("/api/admin/user")
    public ResponseMessage userList() {
        List<User> userList = userRepository.findAll();
        Long totalUserCount = userRepository.count();

        return ResponseMessage.builder()
                .totalCount(totalUserCount)
                .data(userList)
                .build();

//        return userList;
    }*/

    @GetMapping("/api/admin/user/{id}")
    public ResponseEntity<?> userDetail(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("????????? ????????? ???????????? ????????????."), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body(ResponseMessage.success(user));
    }

    @GetMapping("/api/admin/user/search")
    public ResponseEntity<?> findUser(@RequestBody UserSearch userSearch) {

        List<User> userList = userRepository.findByEmailContainsOrPhoneContainsOrUserNameContains(
                userSearch.getEmail(), userSearch.getPhone(), userSearch.getUserName());

        return ResponseEntity.ok().body(ResponseMessage.success(userList));
    }

    @PatchMapping("/api/admin/user/{id}/status")
    public ResponseEntity<?> userStatus(@PathVariable Long id, @RequestBody UserStatusInput userStatusInput) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("????????? ????????? ???????????? ????????????."), HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        user.setStatus(userStatusInput.getStatus());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("????????? ????????? ???????????? ????????????."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (noticeRepository.countByUser(user) > 0) {
            return new ResponseEntity<>(ResponseMessage.fail("???????????? ????????? ??????????????? ????????????."), HttpStatus.BAD_REQUEST);
        }

        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/admin/user/login/history")
    public ResponseEntity<?> userLoginHistory() {
        List<UserLoginHistory> userLoginHistories = userLoginHistoryRepository.findAll();

        return ResponseEntity.ok().body(userLoginHistories);
    }

    @PatchMapping("/api/admin/user/{id}/lock")
    public ResponseEntity<?> userLock(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("????????? ????????? ???????????? ????????????."), HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        if (user.isLockYn()) {
            return new ResponseEntity<>(ResponseMessage.fail("?????? ??????????????? ??? ??????????????????."), HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(true);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }


    @PatchMapping("/api/admin/user/{id}/unlock")
    public ResponseEntity<?> userUnLock(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("????????? ????????? ???????????? ????????????."), HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();

        if (!user.isLockYn()) {
            return new ResponseEntity<>(ResponseMessage.fail("?????? ??????????????? ?????? ??? ??????????????????."), HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(false);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @GetMapping("/api/admin/user/status/count")
    public ResponseEntity<?> userStatusCount() {
        UserSummary userSummary = userService.getUserStatusCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userSummary));
    }

    @GetMapping("/api/admin/user/today")
    public ResponseEntity<?> todayUser() {
        List<User> users = userService.getTodayUsers();

        return ResponseEntity.ok().body(ResponseMessage.success(users));
    }

    @GetMapping("/api/admin/user/notice/count")
    public ResponseEntity<?> userNoticeCount() {

        List<UserNoticeCount> userNoticeCountList = userService.getUserNoticeCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userNoticeCountList));
    }

    @GetMapping("/api/admin/user/log/count")
    public ResponseEntity<?> userLogCount() {
        List<UserNoticeCount> userLogCounts = userService.getUserLogCount();
        return ResponseEntity.ok().body(ResponseMessage.success(userLogCounts));
    }

    @GetMapping("/api/admin/user/like/best")
    public ResponseEntity<?> bestLikeCount() {
        List<UserNoticeCount> userLogCounts = userService.getUserLikeBest();
        return ResponseEntity.ok().body(ResponseMessage.success(userLogCounts));
    }
}














