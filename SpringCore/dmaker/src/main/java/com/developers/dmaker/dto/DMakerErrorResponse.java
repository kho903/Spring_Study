package com.developers.dmaker.dto;

import com.developers.dmaker.exception.DMakerErrorCode;

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
public class DMakerErrorResponse {
	private DMakerErrorCode errorCode;
	private String errorMessage;
}
