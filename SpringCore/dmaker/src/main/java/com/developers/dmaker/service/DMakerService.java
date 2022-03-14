package com.developers.dmaker.service;

import static com.developers.dmaker.exception.DMakerErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DMakerService {
	private final DeveloperRepository developerRepository;

	@Transactional
	public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {
		validateCreateDeveloperRequest(request);
		Developer developer = Developer.builder()
			.developerLevel(request.getDeveloperLevel())
			.developerSkillType(request.getDeveloperSkillType())
			.experienceYears(request.getExperienceYears())
			.memberId(request.getMemberId())
			.name(request.getName())
			.age(request.getAge())
			.build();

		developerRepository.save(developer);
		return CreateDeveloper.Response.fromEntity(developer);
	}

	private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
		// business validation
		DeveloperLevel developerLevel = request.getDeveloperLevel();
		Integer experienceYears = request.getExperienceYears();
		if (developerLevel == DeveloperLevel.SENIOR
			&& experienceYears < 10) {
			throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
		}
		if (developerLevel == DeveloperLevel.JUNGNIOR
			&& experienceYears < 4 || experienceYears > 10) {
			throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
		}
		if (developerLevel == DeveloperLevel.JUNIOR
			&& experienceYears > 4) {
			throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
		}

		// Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
		// if (developer.isPresent()) throw new DMakerException(DUPLICATED_MEMBER_ID);
		developerRepository.findByMemberId(request.getMemberId())
			.ifPresent((developer -> {
				throw new DMakerException(DUPLICATED_MEMBER_ID);
			}));
	}
}
