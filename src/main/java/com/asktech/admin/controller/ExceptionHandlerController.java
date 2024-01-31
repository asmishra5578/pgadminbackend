package com.asktech.admin.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.asktech.admin.dto.error.ErrorResponseDto;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;


@ControllerAdvice
public class ExceptionHandlerController {
	
	@ExceptionHandler(ValidationExceptions.class)
	ResponseEntity<Object> sessionException(final ValidationExceptions see) {
		ErrorResponseDto err = new ErrorResponseDto();
		err.getMsg().add(see.getMessage());
		err.setExceptionEnum(see.getException());
		//Log4jLogger.saveLog(see.getMessage()+"==>");
		return ResponseEntity.ok().body(err);
	}
	
	@ExceptionHandler(SessionExpiredException.class)
	ResponseEntity<Object> sessionException(final SessionExpiredException see) {
		ErrorResponseDto err = new ErrorResponseDto();
		err.getMsg().add(see.getMessage());
		err.setExceptionEnum(see.getException());
		//Log4jLogger.saveLog(see.getMessage()+"==>");
		return ResponseEntity.ok().body(err);
	}
	@ExceptionHandler(UserException.class)
	ResponseEntity<Object> userException(final UserException ue) {
		ErrorResponseDto err = new ErrorResponseDto();
		err.setExceptionEnum(ue.getException());
		err.getMsg().add(ue.getMessage());
		//Log4jLogger.saveLog(ue.getMessage()+"==>");
		return ResponseEntity.ok().body(err);
	}
	@ExceptionHandler(JWTException.class)
	ResponseEntity<Object> jwtException(final JWTException je) {
		ErrorResponseDto err = new ErrorResponseDto();
		err.getMsg().add(je.getMessage());
		err.setExceptionEnum(je.getException());
		//Log4jLogger.saveLog(je.getMessage()+"==>");
		return ResponseEntity.ok().body(err);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Object> exception(final Exception e) {
		ErrorResponseDto err = new ErrorResponseDto();
		
		err.getMsg().add(e.getMessage());
		//Log4jLogger.saveLog(e.getMessage()+"==>");
		return ResponseEntity.ok().body(err);
	}
}
