package com.jikim.practice.spring.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.jikim.practice.spring.security.note.NoteService;
import com.jikim.practice.spring.security.notice.NoticeService;
import com.jikim.practice.spring.security.user.User;
import com.jikim.practice.spring.security.user.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Profile(value = "!test") // test 에서는 제외
public class InitializeDefaultConfig {

	private final UserService userService;
	private final NoteService noteService;
	private final NoticeService noticeService;

	/**
	 * 유저 등록, note 4개 등록
	 */
	@Bean
	public void initializeDefaultUser() {
		User user = userService.signup("user", "user");
		// 개인 노트를 작성한다.
		noteService.saveNote(user, "제목입니다.", "내용입니다.");
	}

	/**
	 * 어드민등록, 공지사항 2개 등록
	 */
	@Bean
	public void initializeDefaultAdmin() {
		userService.signupAdmin("admin", "admin");
		noticeService.saveNotice("환영합니다.", "환영합니다 여러분");
		noticeService.saveNotice("노트 작성 방법 공지", "1. 회원가입\n2. 로그인\n3. 노트 작성\n4. 저장\n* 본인 외에는 게시글을 볼 수 없습니다.");
	}

}
