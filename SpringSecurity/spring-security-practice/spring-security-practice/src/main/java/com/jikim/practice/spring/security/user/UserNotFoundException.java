package com.jikim.practice.spring.security.user;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException() {
		super("유저를 찾을 수 없습니다.");
	}

	public UserNotFoundException(String message) {
		super(message);
	}
}
