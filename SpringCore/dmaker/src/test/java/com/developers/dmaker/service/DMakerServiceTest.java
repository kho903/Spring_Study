package com.developers.dmaker.service;

import static com.developers.dmaker.type.DeveloperLevel.*;
import static com.developers.dmaker.type.DeveloperSkillType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.repository.RetiredDeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;

// @SpringBootTest // spring을 직접 띄워 테스트, 통합 테스트
@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {
	@Mock
	private DeveloperRepository developerRepository;

	@Mock
	private RetiredDeveloperRepository retiredDeveloperRepository;

	@InjectMocks
	private DMakerService dMakerService;

	@Test
	public void testSomething() {

		dMakerService.createDeveloper(CreateDeveloper.Request.builder()
			.developerLevel(SENIOR)
			.developerSkillType(FRONT_END)
			.experienceYears(12)
			.memberId("memberId")
			.name("name")
			.age(32)
			.build()
		);

		List<DeveloperDto> allEmployedDevelopers = dMakerService.getAllEmployedDevelopers();
		System.out.println("====================================");
		System.out.println(allEmployedDevelopers);
		System.out.println("====================================");
	}

	@Test
	public void tdd() {
		// mocking
		given(developerRepository.findByMemberId(anyString()))
			.willReturn(Optional.of(Developer.builder()
				.developerLevel(SENIOR)
				.developerSkillType(FRONT_END)
				.experienceYears(12)
				.memberId("memberId")
				.name("name")
				.age(32)
				.build()));
		DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

		assertEquals(SENIOR, developerDetail.getDeveloperLevel());
		assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
		assertEquals(12, developerDetail.getExperienceYears());
	}
}