package com.jikim.practice.spring.security.note;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jikim.practice.spring.security.user.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

	private final NoteService noteService;

	/**
	 * 노트(게시글) 조회
	 *
	 * @param authentication
	 * @param model
	 * @return
	 */
	@GetMapping
	public String getNote(Authentication authentication, Model model) {
		User user = (User) authentication.getPrincipal();
		List<Note> notes = noteService.findByUser(user);
		// note/index.html 에서 notes 사용 가능
		model.addAttribute("notes", notes);
		// 개인 노트 페이지를 전달한다.
		return "note/index";
	}

	/**
	 * 노트 저장
	 */
	@PostMapping
	public String saveNote(Authentication authentication, @ModelAttribute NoteRegisterDto noteDto) {
		User user = (User)authentication.getPrincipal();
		noteService.saveNote(user, noteDto.getTitle(), noteDto.getContent());
		return "redirect:note";
	}

	/**
	 * 노트 삭제
	 */
	@DeleteMapping
	public String deleteNote(Authentication authentication, @RequestParam Long id) {
		User user = (User) authentication.getPrincipal();
		noteService.deleteNote(user, id);
		return "redirect:note";
	}
}
