package com.developers.dmaker.service;

import static com.developers.dmaker.exception.DMakerErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DMakerService {
	private final DeveloperRepository developerRepository;

	@Transactional
	public void createDeveloper(CreateDeveloper.Request request) {
		validateCreateDeveloperRequest(request);
		Developer developer = Developer.builder()
			.developerLevel(DeveloperLevel.JUNIOR)
			.developerSkillType(DeveloperSkillType.FRONT_END)
			.experienceYears(2)
			.name("Olaf")
			.age(5)
			.build();

		developerRepository.save(developer);
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
