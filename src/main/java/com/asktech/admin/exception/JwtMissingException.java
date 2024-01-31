package com.asktech.admin.exception;

import org.springframework.security.core.AuthenticationException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtMissingException extends AuthenticationException {
	private static final long serialVersionUID = 1L;
	//private RmeException exception;
	public JwtMissingException(String msg, Throwable exception) {
		super(msg,exception);
	}
}
