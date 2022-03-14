package com.developers.dmaker.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.service.DMakerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController // Controller + ResponseBody
@RequiredArgsConstructor
public class DMakerController {

	private final DMakerService dMakerService;

	@GetMapping("/developers")
	public List<DeveloperDto> getAllDevelopers() {
		log.info("GET /developers HTTP/1.1");

		return dMakerService.getAllDevelopers();
	}

	@GetMapping("/developers/{memberId}")
	public DeveloperDetailDto getDeveloper(
		@PathVariable String memberId
	) {
		log.info("GET //developers/{memberId} HTTP/1.1");

		return dMakerService.getDeveloperDetail(memberId);
	}

	@PostMapping("/create-developer")
	public CreateDeveloper.Response createDevelopers(
		@Valid @RequestBody CreateDeveloper.Request request
	) {
		log.info("request : {}", request);

		return dMakerService.createDeveloper(request);
	}

}
