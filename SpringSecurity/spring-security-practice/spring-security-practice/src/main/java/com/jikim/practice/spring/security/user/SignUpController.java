package com.jikim.practice.spring.security.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

	private final UserService userService;

	@GetMapping
	public String signup() {
		return "signup";
	}

	@PostMapping
	public String signup(
		@ModelAttribute UserRegisterDto userDto
	) {
		userService.signup(userDto.getUsername(), userDto.getPassword());
		return "redirect:login";
	}
}
