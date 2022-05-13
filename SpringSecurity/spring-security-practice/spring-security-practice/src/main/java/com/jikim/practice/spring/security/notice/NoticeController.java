package com.jikim.practice.spring.security.notice;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jikim.practice.spring.security.note.NoteRegisterDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

	private final NoticeService noticeService;

	/**
	 * 공지사항 조회
	 *
	 * @return notice/index.html
	 */
	@GetMapping
	public String getNotice(Model model) {
		List<Notice> notices = noticeService.findAll();
		model.addAttribute("notices", notices);
		return "notice/index";
	}

	/**
	 * 공지사항 등록
	 *
	 * @param noteDto 노트 등록 Dto
	 * @return notice/index.html refresh
	 */
	@PostMapping
	public String postNotice(@ModelAttribute NoteRegisterDto noteDto) {
		noticeService.saveNotice(noteDto.getTitle(), noteDto.getContent());
		return "redirect:notice";
	}

	@DeleteMapping
	public String deleteNotice(@RequestParam Long id) {
		noticeService.deleteNotice(id);
		return "redirect:notice";
	}
}
