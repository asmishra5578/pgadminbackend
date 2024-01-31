package com.asktech.admin.dto.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDto {

	private List<String> msg = new ArrayList<>();
	private boolean status = false;
	private FormValidationExceptionEnums exception;
	private int statusCode = 404;
	Map<String,Object> extraData = new HashMap<>();
}
