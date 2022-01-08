package com.example.jpa.user.service;

import com.example.jpa.user.model.UserStatus;
import com.example.jpa.user.model.UserSummary;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}
