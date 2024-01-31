package com.asktech.admin.exception;

import com.asktech.admin.enums.FormValidationExceptionEnums;

public class FileStorageException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 92326477088700648L;

	public FileStorageException(String msg, FormValidationExceptionEnums exception) {
		super(msg);
	}
}
