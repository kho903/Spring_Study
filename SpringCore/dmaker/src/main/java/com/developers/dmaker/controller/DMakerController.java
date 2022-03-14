package com.developers.dmaker.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.service.DMakerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController // Controller + ResponseBody
@RequiredArgsConstructor
public class DMakerController {

	private final DMakerService dMakerService;

	@GetMapping("/developers")
	public List<String> getAllDevelopers() {
		log.info("GET /developers HTTP/1.1");

		return Arrays.asList("Snow", "Elsa", "Olaf");
	}

	@PostMapping("/create-developers")
	public List<String> createDevelopers(
		@Valid @RequestBody CreateDeveloper.Request request
	) {
		log.info("request : {}", request);
		dMakerService.createDeveloper(request);

		return Collections.singletonList("Olaf");
	}

}
