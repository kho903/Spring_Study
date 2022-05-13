package com.jikim.practice.spring.security.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 유저 등록 username username
	 *
	 * @param username username
	 * @param password password
	 * @return 유저 권한을 가지고 있는 유저
	 */
	public User signup(
		String username,
		String password
	) {
		if (userRepository.findByUsername(username) != null) {
			throw new AlreadyRegisteredUserException();
		}
		return userRepository.save(new User(username, passwordEncoder.encode(password), "ROLE_USER"));
	}

	public User signupAdmin(
		String username,
		String password
	) {
		if (userRepository.findByUsername(username) != null) {
			throw new AlreadyRegisteredUserException();
		}
		return userRepository.save(new User(username, passwordEncoder.encode(password), "ROLE_ADMIN"));
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}
