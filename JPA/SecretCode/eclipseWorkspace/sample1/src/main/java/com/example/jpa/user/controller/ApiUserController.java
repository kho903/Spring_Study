package com.example.jpa.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.exception.BizException;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.entity.NoticeLike;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeLikeRepository;
import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.exception.ExistsEmailException;
import com.example.jpa.user.exception.PasswordNotMatchException;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.NoticeResponse;
import com.example.jpa.user.model.UserInput;
import com.example.jpa.user.model.UserInputFind;
import com.example.jpa.user.model.UserInputPassword;
import com.example.jpa.user.model.UserLogin;
import com.example.jpa.user.model.UserLoginToken;
import com.example.jpa.user.model.UserPasswordResetInput;
import com.example.jpa.user.model.UserPointInput;
import com.example.jpa.user.model.UserResponse;
import com.example.jpa.user.model.UserUpdate;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.PointService;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import com.example.jpa.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApiUserController {
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeLikeRepository noticeLikeRepository;

    private final BoardService boardService;
    private final PointService pointService;

    private final UserService userService;

    @PostMapping("/api/user")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

//        return new ResponseEntity<>(HttpStatus.OK);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/user2")
    public ResponseEntity<?> addUser2(@RequestBody @Valid UserInput userInput, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .password(userInput.getPassword())
                .phone(userInput.getPhone())
                .regDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handlerUserNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/api/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                           @RequestBody @Valid UserUpdate userUpdate, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        user.setPhone(userUpdate.getPhone());
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/{id}")
    public UserResponse getUser(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

//        UserResponse userResponse = new UserResponse(user);
        UserResponse userResponse = UserResponse.of(user);

        return userResponse;
    }

    @GetMapping("/api/user/{id}/notice")
    public List<NoticeResponse> userNotice(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        List<Notice> noticeList = noticeRepository.findByUser(user);

        List<NoticeResponse> noticeResponsesList = new ArrayList<>();

        noticeList.stream().forEach((e) -> {
            noticeResponsesList.add(NoticeResponse.of(e));
        });

        return noticeResponsesList;
    }

    @ExceptionHandler(value = {ExistsEmailException.class, PasswordNotMatchException.class})
    public ResponseEntity<?> handlerExistsEmailException(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/api/user3")
    public ResponseEntity<?> addUser3(@RequestBody @Valid UserInput userInput, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.countByEmail(userInput.getEmail())> 0) {
            throw new ExistsEmailException("?????? ???????????? ??????????????????.");
        }

        User user = User.builder()
                .email(userInput.getEmail())
                .userName((userInput.getUserName()))
                .phone(userInput.getPhone())
                .password(userInput.getPassword())
                .regDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/user/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id, @RequestBody UserInputPassword userInputPassword, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByIdAndPassword(id, userInputPassword.getPassword())
                .orElseThrow(() -> new PasswordNotMatchException("??????????????? ???????????? ????????????."));

        user.setPassword(userInputPassword.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    private String getEncryptPassword(String password) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    @PostMapping("/api/user4")
    public ResponseEntity<?> addUser4(@RequestBody @Valid UserInput userInput, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.countByEmail(userInput.getEmail())> 0) {
            throw new ExistsEmailException("?????? ???????????? ??????????????????.");
        }

        String encryptPassword = getEncryptPassword(userInput.getPassword());

        User user = User.builder()
                .email(userInput.getEmail())
                .userName((userInput.getUserName()))
                .phone(userInput.getPhone())
                .password(encryptPassword)
                .regDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        try {
            userRepository.delete(user);
        } catch (DataIntegrityViolationException e) {
            String message = "??????????????? ????????? ?????????????????????.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            String message = "???????????? ??? ????????? ?????????????????????.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user")
    public ResponseEntity<?> findUser(@RequestBody UserInputFind userInputFind) {
        User user = userRepository.findByUserNameAndPhone(userInputFind.getUserName(), userInputFind.getPhone())
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));
        UserResponse userResponse = UserResponse.of(user);

        return ResponseEntity.ok().body(userResponse);
    }

    private String getResetPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }

    @GetMapping("/api/user/{id}/password/reset")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        // ???????????? ?????????
        String resetPassword = getResetPassword();
        String resetEncryptPassword = getEncryptPassword(resetPassword);
        user.setPassword(resetEncryptPassword);
        userRepository.save(user);

        String message = String.format("[%s]?????? ?????? ??????????????? [%s]??? ????????? ???????????????."
                , user.getUserName()
                , resetPassword);
        sendSMS(message);
        return ResponseEntity.ok().build();
    }

    void sendSMS(String message) {
        System.out.println("[??????????????? ??????]");
        System.out.println(message);
    }

    @GetMapping("/api/user/{id}/notice/like")
    public List<NoticeLike> likeNotice(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        List<NoticeLike> noticeLikeList = noticeLikeRepository.findByUser(user);

        return noticeLikeList;
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<?> createToken(@RequestBody @Valid UserLogin userLogin, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        if (!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("??????????????? ???????????? ????????????.");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/user/login/v2")
    public ResponseEntity<?> createTokenV2(@RequestBody @Valid UserLogin userLogin, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        if (!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("??????????????? ???????????? ????????????.");
        }
        // ?????? ????????????
        String token = JWT.create()
                .withExpiresAt(new Date())
                .withClaim("user_id", user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("jikim".getBytes()));

        return ResponseEntity.ok().body(UserLoginToken.builder().token(token).build());
    }

    @PostMapping("/api/user/login/v3")
    public ResponseEntity<?> createTokenV3(@RequestBody @Valid UserLogin userLogin, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        if (!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("??????????????? ???????????? ????????????.");
        }

        LocalDateTime expiredDatetime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDatetime);

        String token = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id", user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("jikim".getBytes()));

        return ResponseEntity.ok().body(UserLoginToken.builder().token(token).build());
    }

    @PatchMapping("/api/user/login")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("K-TOKEN");
        String email = "";
        try {
             email = JWT.require(Algorithm.HMAC512("jikim".getBytes()))
                    .build()
                    .verify(token)
                    .getIssuer();
        } catch (SignatureVerificationException | IllegalArgumentException | JWTDecodeException e) {
            throw new PasswordNotMatchException("??????????????? ???????????? ????????????.");
        } catch (Exception e) {
            throw new PasswordNotMatchException("?????? ????????? ?????????????????????.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("????????? ????????? ????????????."));

        LocalDateTime expiredDatetime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDatetime);

        String newToken = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id", user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("jikim".getBytes()));

        return ResponseEntity.ok().body(UserLoginToken.builder().token(newToken).build());

    }

    @DeleteMapping("/api/user/login")
    public ResponseEntity<?> removeToken(@RequestHeader("K-TOKEN") String token) {
        String email = "";

        try {
            email = JWTUtils.getIssuer(token);
        }  catch (SignatureVerificationException | IllegalArgumentException | JWTDecodeException e) {
            return new ResponseEntity<>("?????? ????????? ???????????? ????????????.", HttpStatus.BAD_REQUEST);
        }

        // ??????, ?????? ??????
        // ??????????????? ?????? / ?????? ???????????? / ?????? ????????????
        // ??????????????? ??????

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/board/post")
    public ResponseEntity<?> myPost(@RequestHeader("K-TOKEN") String token) {
        String email = "";

        try {
            email = JWTUtils.getIssuer(token);
        }  catch (SignatureVerificationException | IllegalArgumentException | JWTDecodeException e) {
            return ResponseResult.fail("?????? ????????? ???????????? ????????????.");
        }

        List<Board> list = boardService.postList(email);
        return ResponseResult.success(list);
    }

    @GetMapping("/api/user/board/comment")
    public ResponseEntity<?> myComments(@RequestHeader("K-TOKEN") String token) {
        String email = "";

        try {
            email = JWTUtils.getIssuer(token);
        }  catch (SignatureVerificationException | IllegalArgumentException | JWTDecodeException e) {
            return ResponseResult.fail("?????? ????????? ???????????? ????????????.");
        }

        List<BoardComment> list = boardService.commentList(email);
        return ResponseResult.success(list);
    }

    @PostMapping("/api/user/point")
    public ResponseEntity<?> userPoint(@RequestHeader("K-TOKEN") String token,
                          @RequestBody UserPointInput userPointInput) {
        String email = "";

        try {
            email = JWTUtils.getIssuer(token);
        }  catch (SignatureVerificationException | IllegalArgumentException | JWTDecodeException e) {
            return ResponseResult.fail("?????? ????????? ???????????? ????????????.");
        }
        ServiceResult result = pointService.addPoint(email, userPointInput);
        return ResponseResult.result(result);
    }

    @PostMapping("/api/public/user")
    public ResponseEntity<?> addUser(@RequestBody UserInput userInput) {
        ServiceResult result = userService.addUser(userInput);
        return ResponseResult.result(result);
    }

    @PostMapping("/api/public/user/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid UserPasswordResetInput userPasswordResetInput, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseResult.fail("???????????? ???????????? ????????????.", ResponseError.of(errors.getAllErrors()));
        }
        ServiceResult result = null;
        try {
            result = userService.resetPassword(userPasswordResetInput);
        } catch (BizException e) {
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.result(result);
    }
}













