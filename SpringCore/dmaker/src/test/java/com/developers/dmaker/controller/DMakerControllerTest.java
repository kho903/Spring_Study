package com.developers.dmaker.controller;

import static com.developers.dmaker.type.DeveloperLevel.*;
import static com.developers.dmaker.type.DeveloperSkillType.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.service.DMakerService;

@WebMvcTest(DMakerController.class)
class DMakerControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DMakerService dMakerService;

	protected MediaType contentType =
		new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			StandardCharsets.UTF_8);

	@Test
	void getAllDevelopers() throws Exception {
		// given
		DeveloperDto juniorDeveloperDto = DeveloperDto.builder()
			.developerSkillType(BACK_END)
			.developerLevel(JUNIOR)
			.memberId("memberId1")
			.build();
		DeveloperDto seniorDeveloperDto = DeveloperDto.builder()
			.developerSkillType(FRONT_END)
			.developerLevel(SENIOR)
			.memberId("memberId2")
			.build();
		given(dMakerService.getAllEmployedDevelopers())
			.willReturn(Arrays.asList(juniorDeveloperDto, seniorDeveloperDto));

		mockMvc.perform(get("/developers").contentType(contentType))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(
				jsonPath("$.[0].developerSkillType",
					is(BACK_END.name()))
			)
			.andExpect(
				jsonPath("$.[0].developerLevel",
					is(JUNIOR.name()))
			)
			.andExpect(
				jsonPath("$.[1].developerSkillType",
					is(FRONT_END.name()))
			)
			.andExpect(
				jsonPath("$.[1].developerLevel",
					is(SENIOR.name()))
			);

	}
}