package com.developers.dmaker.service;

import static com.developers.dmaker.constant.DMakerConstant.*;
import static com.developers.dmaker.exception.DMakerErrorCode.*;
import static com.developers.dmaker.type.DeveloperLevel.*;
import static com.developers.dmaker.type.DeveloperSkillType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;

// @SpringBootTest // spring을 직접 띄워 테스트, 통합 테스트
@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {
	@Mock
	private DeveloperRepository developerRepository;

	@InjectMocks
	private DMakerService dMakerService;

	private final Developer defaultDeveloper = Developer.builder()
		.developerLevel(SENIOR)
		.developerSkillType(FRONT_END)
		.experienceYears(12)
		.memberId("memberId")
		.name("name")
		.age(32)
		.build();

	/*private final CreateDeveloper.Request defaultCreateRequest = CreateDeveloper.Request.builder()
		.developerLevel(SENIOR)
		.developerSkillType(FRONT_END)
		.experienceYears(12)
		.memberId("memberId")
		.name("name")
		.age(32)
		.build();*/

	private CreateDeveloper.Request getCreateRequest(
		DeveloperLevel developerLevel,
		DeveloperSkillType developerSkillType,
		Integer experienceYears
	) {
		return CreateDeveloper.Request.builder()
			.developerLevel(developerLevel)
			.developerSkillType(developerSkillType)
			.experienceYears(experienceYears)
			.memberId("memberId")
			.name("name")
			.age(32)
			.build();
	}

	@Test
	public void tdd() {
		// given
		// mocking
		given(developerRepository.findByMemberId(anyString()))
			.willReturn(Optional.of(defaultDeveloper));

		// when
		DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

		// then
		assertEquals(SENIOR, developerDetail.getDeveloperLevel());
		assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
		assertEquals(12, developerDetail.getExperienceYears());
	}

	@Test
	public void createDeveloperTest_success() {
		// given
		given(developerRepository.findByMemberId(anyString()))
			.willReturn(Optional.empty());
		given(developerRepository.save(any()))
			.willReturn(defaultDeveloper);

		ArgumentCaptor<Developer> captor =
			ArgumentCaptor.forClass(Developer.class);

		// when
		dMakerService.createDeveloper(getCreateRequest(SENIOR, FRONT_END, 12));

		// then
		verify(developerRepository, times(1))
			.save(captor.capture());

		Developer savedDeveloper = captor.getValue();
		assertEquals(SENIOR, savedDeveloper.getDeveloperLevel());
		assertEquals(FRONT_END, savedDeveloper.getDeveloperSkillType());
		assertEquals(12, savedDeveloper.getExperienceYears());
	}

	@Test
	public void createDeveloperTest_fail_with_unmatched_level() {
		// given
		// when
		// then
		DMakerException dMakerException = assertThrows(DMakerException.class,
			() -> dMakerService.createDeveloper(
				getCreateRequest(SENIOR, FRONT_END,
					MIN_SENIOR_EXPERIENCE_YEARS - 1)
			)
		);

		assertEquals(
			LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
			dMakerException.getDMakerErrorCode()
		);

		dMakerException = assertThrows(DMakerException.class,
			() -> dMakerService.createDeveloper(
				getCreateRequest(JUNIOR, FRONT_END,
					MAX_JUNIOR_EXPERIENCE_YEARS + 1)
			)
		);

		assertEquals(
			LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
			dMakerException.getDMakerErrorCode()
		);

		dMakerException = assertThrows(DMakerException.class,
			() -> dMakerService.createDeveloper(
				getCreateRequest(JUNGNIOR, FRONT_END,
					MIN_SENIOR_EXPERIENCE_YEARS + 1)
			)
		);

		assertEquals(
			LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
			dMakerException.getDMakerErrorCode()
		);
	}

	@Test
	public void createDeveloperTest_failed_with_duplicated() {
		// given
		given(developerRepository.findByMemberId(anyString()))
			.willReturn(Optional.of(defaultDeveloper));

		// when
		// then
		DMakerException dMakerException = assertThrows(DMakerException.class,
			() -> dMakerService.createDeveloper(getCreateRequest(SENIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS))
		);

		assertEquals(DUPLICATED_MEMBER_ID, dMakerException.getDMakerErrorCode());
	}
}