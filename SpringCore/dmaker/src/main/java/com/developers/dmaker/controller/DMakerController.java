package com.developers.dmaker.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DMakerErrorResponse;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.dto.EditDeveloper;
import com.developers.dmaker.exception.DMakerException;
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

		return dMakerService.getAllEmployedDevelopers();
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

	@PutMapping("/developers/{memberId}")
	public DeveloperDetailDto editDeveloper(
		@PathVariable String memberId,
		@Valid @RequestBody EditDeveloper.Request request
	) {
		log.info("PUT /developers/{memberId} HTTP/1.1");

		return dMakerService.editDeveloper(memberId, request);
	}

	@DeleteMapping("/developers/{memberId}")
	public DeveloperDetailDto deleteDeveloper(
		@PathVariable String memberId
	) {
		return dMakerService.deleteDeveloper(memberId);
	}

}
