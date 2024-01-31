package com.asktech.admin.dto.merchant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class OTPConfirmation {

	private String userId;
	private String otpSessionId;
	
}
