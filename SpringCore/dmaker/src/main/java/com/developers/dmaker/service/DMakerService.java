package com.developers.dmaker.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.entity.Developer;
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
		Developer developer = Developer.builder()
			.developerLevel(DeveloperLevel.JUNIOR)
			.developerSkillType(DeveloperSkillType.FRONT_END)
			.experienceYears(2)
			.name("Olaf")
			.age(5)
			.build();

		developerRepository.save(developer);
	}
}
