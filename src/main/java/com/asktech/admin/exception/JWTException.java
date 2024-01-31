package com.asktech.admin.exception;


import com.asktech.admin.enums.FormValidationExceptionEnums;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTException extends Exception {

	private static final long serialVersionUID = 1L;

	private FormValidationExceptionEnums exception;

	public JWTException(String msg, FormValidationExceptionEnums exception) {
		super(msg);
		this.exception = exception;
	}

}
