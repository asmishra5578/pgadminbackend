package com.asktech.admin.exception;

import com.asktech.admin.enums.FormValidationExceptionEnums;

public class FormValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4236137461216744410L;
	
	public FormValidationException(String msg, FormValidationExceptionEnums exception) {
		super(msg);
	}

}
