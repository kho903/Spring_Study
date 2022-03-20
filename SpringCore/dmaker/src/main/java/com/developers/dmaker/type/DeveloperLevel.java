package com.developers.dmaker.type;

import static com.developers.dmaker.constant.DMakerConstant.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeveloperLevel {
	NEW("신입 개발자", 0, 0),
	JUNIOR("주니어 개발자", 1, MAX_JUNIOR_EXPERIENCE_YEARS),
	JUNGNIOR("중니어 개발자",
		MAX_JUNIOR_EXPERIENCE_YEARS + 1,
		MIN_SENIOR_EXPERIENCE_YEARS - 1),
	SENIOR("시니어 개발자",
		MIN_SENIOR_EXPERIENCE_YEARS,
		70);

	private final String description;
	private final Integer minExperienceYears;
	private final Integer maxExperienceYears;
}
