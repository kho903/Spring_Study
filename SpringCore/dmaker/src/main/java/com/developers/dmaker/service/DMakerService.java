package com.developers.dmaker.service;

import static com.developers.dmaker.exception.DMakerErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.developers.dmaker.code.StatusCode;
import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.dto.EditDeveloper;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.entity.RetiredDeveloper;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.repository.RetiredDeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DMakerService {
	private final DeveloperRepository developerRepository;
	private final RetiredDeveloperRepository retiredDeveloperRepository;

	@Transactional
	public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {
		validateCreateDeveloperRequest(request);

		return CreateDeveloper.Response.fromEntity(
			developerRepository.save(
				createDeveloperFromRequest(request)
			)
		);
	}

	private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
		return Developer.builder()
			.developerLevel(request.getDeveloperLevel())
			.developerSkillType(request.getDeveloperSkillType())
			.experienceYears(request.getExperienceYears())
			.memberId(request.getMemberId())
			.statusCode(StatusCode.EMPLOYED)
			.name(request.getName())
			.age(request.getAge())
			.build();
	}

	private void validateCreateDeveloperRequest(@NonNull CreateDeveloper.Request request) {
		// business validation
		request.getDeveloperLevel().validateExperienceYears(
			request.getExperienceYears()
		);

		developerRepository.findByMemberId(request.getMemberId())
			.ifPresent((developer -> {
				throw new DMakerException(DUPLICATED_MEMBER_ID);
			}));
	}

	@Transactional(readOnly = true)
	public List<DeveloperDto> getAllEmployedDevelopers() {
		return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
			.stream()
			.map(DeveloperDto::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public DeveloperDetailDto getDeveloperDetail(String memberId) {
		return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
	}

	private Developer getDeveloperByMemberId(String memberId) {
		return developerRepository.findByMemberId(memberId)
			.orElseThrow(() -> new DMakerException(NO_DEVELOPER));
	}

	@Transactional
	public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {

		request.getDeveloperLevel().validateExperienceYears(
			request.getExperienceYears()
		);

		return DeveloperDetailDto.fromEntity(
			getUpdatedDeveloperFromRequest(request,
				getDeveloperByMemberId(memberId)
			)
		);
	}

	private Developer getUpdatedDeveloperFromRequest(
		EditDeveloper.Request request, Developer developer
	) {
		developer.setDeveloperLevel(request.getDeveloperLevel());
		developer.setDeveloperSkillType(request.getDeveloperSkillType());
		developer.setExperienceYears(request.getExperienceYears());

		return developer;
	}

	@Transactional
	public DeveloperDetailDto deleteDeveloper(String memberId) {
		// 1. EMPLOYED -> RETIRED
		Developer developer = developerRepository.findByMemberId(memberId)
			.orElseThrow(() -> new DMakerException(NO_DEVELOPER));
		developer.setStatusCode(StatusCode.RETIRED);

		// 2. save into RetiredDeveloper
		RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
			.memberId(memberId)
			.name(developer.getName())
			.build();
		retiredDeveloperRepository.save(retiredDeveloper);
		return DeveloperDetailDto.fromEntity(developer);
	}
}
