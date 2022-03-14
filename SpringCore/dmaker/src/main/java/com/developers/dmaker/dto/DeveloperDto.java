package com.developers.dmaker.dto;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeveloperDto {
	private DeveloperLevel developerLevel;
	private DeveloperSkillType developerSkillType;
	private String memberId;

	public static DeveloperDto fromEntity(Developer developer) {
		return DeveloperDto.builder()
			.developerLevel(developer.getDeveloperLevel())
			.developerSkillType(developer.getDeveloperSkillType())
			.memberId(developer.getMemberId())
			.build();
	}
}
