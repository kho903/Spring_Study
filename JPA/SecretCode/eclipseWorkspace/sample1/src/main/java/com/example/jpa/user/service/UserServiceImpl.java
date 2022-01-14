package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.common.exception.BizException;
import com.example.jpa.logs.service.LogService;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserInterest;
import com.example.jpa.user.model.UserLogin;
import com.example.jpa.user.model.UserNoticeCount;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final UserInterestRepository userInterestRepository;

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
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }
        User user = optionalUser.get();

        Optional<User> optionalInterestUser = userRepository.findById(id);
        if (!optionalInterestUser.isPresent()) {
            return ServiceResult.fail("관심 사용자에 추가할 회원 정보가 존재하지 않습니다.");
        }
        User interestUser = optionalInterestUser.get();

        // 내가 나를 추가함
        if (user.getId().equals(interestUser.getId())) {
            return ServiceResult.fail("자기 자신은 추가할 수 없습니다.");
        }

        if (userInterestRepository.countByUserAndInterestUser(user, interestUser) > 0) {
            return ServiceResult.fail("이미 관심 사용자 목록에 추가하였습니다.");
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
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }
        User user = optionalUser.get();

        Optional<UserInterest> optionalUserInterest = userInterestRepository.findById(interestId);
        if (!optionalUserInterest.isPresent()) {
            return ServiceResult.fail("삭제할 정보가 없습니다.");
        }
        UserInterest userInterest = optionalUserInterest.get();

        if (!(userInterest.getUser().getId().equals(user.getId()))) {
            return ServiceResult.fail("본인의 관심자 정보만 삭제할 수 있습니다.");
        }

        userInterestRepository.delete(userInterest);
        return ServiceResult.success();
    }

    @Override
    public User login(UserLogin userLogin) {
        Optional<User> optionalUser = userRepository.findByEmail(userLogin.getEmail());
        if (!optionalUser.isPresent()) {
            throw new BizException("회원 정보가 존재하지 않습니다.");
        }
        User user = optionalUser.get();

        if (!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())) {
            throw new BizException("일치하는 정보가 없습니다.");
        }

        return user;
    }
}
