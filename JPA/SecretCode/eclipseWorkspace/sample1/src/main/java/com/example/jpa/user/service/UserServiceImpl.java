package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.common.MailComponent;
import com.example.jpa.common.exception.BizException;
import com.example.jpa.mail.entity.MailTemplate;
import com.example.jpa.mail.repository.MailTemplateRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserInterest;
import com.example.jpa.user.model.UserInput;
import com.example.jpa.user.model.UserLogin;
import com.example.jpa.user.model.UserNoticeCount;
import com.example.jpa.user.model.UserPasswordResetInput;
import com.example.jpa.user.model.UserStatus;
import com.example.jpa.user.model.UserSummary;
import com.example.jpa.user.repository.UserCustomRepository;
import com.example.jpa.user.repository.UserInterestRepository;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final UserInterestRepository userInterestRepository;
    private final MailComponent mailComponent;
    private final MailTemplateRepository mailTemplateRepository;

    @Override
    public UserSummary getUserStatusCount() {
        Long usingUserCount = userRepository.countByStatus(UserStatus.Using);
        Long stopUserCount = userRepository.countByStatus(UserStatus.Stop);
        Long totalUserCount = userRepository.count();

        return UserSummary.builder()
                .usingUserCount(usingUserCount)
                .stopUserCount(stopUserCount)
                .totalUserCount(totalUserCount)
                .build();
    }

    @Override
    public List<User> getTodayUsers() {

        LocalDateTime t = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), 0, 0);
        LocalDateTime endDate = startDate.plusDays(1);

        return userRepository.findToday(startDate, endDate);
    }

    @Override
    public List<UserNoticeCount> getUserNoticeCount() {
        return userCustomRepository.findUserNoticeCount();
    }

    @Override
    public List<UserNoticeCount> getUserLogCount() {
        return userCustomRepository.findUserLogCount();
    }

    @Override
    public List<UserNoticeCount> getUserLikeBest() {
        return userCustomRepository.findUserLikeBest();
    }

    @Override
    public ServiceResult addInterestUser(String email, Long id) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("?????? ????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        Optional<User> optionalInterestUser = userRepository.findById(id);
        if (!optionalInterestUser.isPresent()) {
            return ServiceResult.fail("?????? ???????????? ????????? ?????? ????????? ???????????? ????????????.");
        }
        User interestUser = optionalInterestUser.get();

        // ?????? ?????? ?????????
        if (user.getId().equals(interestUser.getId())) {
            return ServiceResult.fail("?????? ????????? ????????? ??? ????????????.");
        }

        if (userInterestRepository.countByUserAndInterestUser(user, interestUser) > 0) {
            return ServiceResult.fail("?????? ?????? ????????? ????????? ?????????????????????.");
        }

        UserInterest userInterest = UserInterest.builder()
                .user(user)
                .interestUser(interestUser)
                .regDate(LocalDateTime.now())
                .build();

        userInterestRepository.save(userInterest);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeInterestUser(String email, Long interestId) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ServiceResult.fail("?????? ????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        Optional<UserInterest> optionalUserInterest = userInterestRepository.findById(interestId);
        if (!optionalUserInterest.isPresent()) {
            return ServiceResult.fail("????????? ????????? ????????????.");
        }
        UserInterest userInterest = optionalUserInterest.get();

        if (!(userInterest.getUser().getId().equals(user.getId()))) {
            return ServiceResult.fail("????????? ????????? ????????? ????????? ??? ????????????.");
        }

        userInterestRepository.delete(userInterest);
        return ServiceResult.success();
    }

    @Override
    public User login(UserLogin userLogin) {
        Optional<User> optionalUser = userRepository.findByEmail(userLogin.getEmail());
        if (!optionalUser.isPresent()) {
            throw new BizException("?????? ????????? ???????????? ????????????.");
        }
        User user = optionalUser.get();

        if (!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())) {
            throw new BizException("???????????? ????????? ????????????.");
        }

        return user;
    }

    @Override
    public ServiceResult addUser(UserInput userInput) {
        Optional<User> optionalUser = userRepository.findByEmail(userInput.getEmail());
        if (optionalUser.isPresent()) {
            throw new BizException("?????? ????????? ??????????????????.");
        }

        String encryptPassword = PasswordUtils.encryptedPassword(userInput.getPassword());

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .regDate(LocalDateTime.now())
                .password(encryptPassword)
                .phone(userInput.getPhone())
                .status(UserStatus.Using)
                .build();

        userRepository.save(user);

        // ?????? ??????.
        String fromEmail = "smtptestkk@gmail.com";
        String fromName = "?????????";
        String toEmail = user.getEmail();
        String toName = user.getUserName();

        String title = "??????????????? ??????????????????.";
        String contents = "??????????????? ??????????????????.";

        mailComponent.send(fromEmail, fromName, toEmail, toName, title, contents);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult resetPassword(UserPasswordResetInput userPasswordResetInput) {
        Optional<User> optionalUser = userRepository.findByEmailAndUserName(userPasswordResetInput.getEmail(), userPasswordResetInput.getUserName());
        if (!optionalUser.isPresent()) {
            throw new BizException("?????? ????????? ???????????? ????????????.");
        }

        User user = optionalUser.get();
        String passwordResetKey = UUID.randomUUID().toString();

        user.setPasswordResetYn(true);
        user.setPasswordResetKey(passwordResetKey);
        userRepository.save(user);

        String serverURL = "http://localhost:8080";

        Optional<MailTemplate> optionalMailTemplate = mailTemplateRepository.findByTemplateId("USER_RESET_PASSWORD");
        optionalMailTemplate.ifPresent(e -> {

            String fromEmail = e.getSendEmail();
            String fromUserName = e.getSendUserName();
            String title = e.getTitle().replaceAll("\\{USER_NAME\\}", user.getUserName());
            String contents = e.getContents().replaceAll("\\{USER_NAME\\}", user.getUserName())
                    .replaceAll("\\{SERVER_URL\\}", serverURL)
                    .replaceAll("\\{RESET_PASSWORD_KEY\\}", passwordResetKey);

            mailComponent.send(fromEmail, fromUserName, user.getEmail(), user.getUserName(), title, contents);
        });

        return ServiceResult.success();
    }
}
