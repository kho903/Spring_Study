package com.developers.dmaker.dto;

import com.developers.dmaker.code.StatusCode;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperDetailDto {

	private DeveloperLevel developerLevel;
	private DeveloperSkillType developerSkillType;
	private Integer experienceYears;
	private String memberId;
	private StatusCode statusCode;
	private String name;
	private Integer age;


	public static DeveloperDetailDto fromEntity(Developer developer) {
		return DeveloperDetailDto.builder()
			.developerLevel(developer.getDeveloperLevel())
			.developerSkillType(developer.getDeveloperSkillType())
			.experienceYears(developer.getExperienceYears())
			.memberId(developer.getMemberId())
			.statusCode(developer.getStatusCode())
			.name(developer.getName())
			.age(developer.getAge())
			.build();
	}

}
