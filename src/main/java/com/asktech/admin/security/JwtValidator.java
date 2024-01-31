package com.asktech.admin.security;

import org.springframework.stereotype.Component;

import com.asktech.admin.dto.login.LoginRequestDto;
import com.asktech.admin.enums.AskTechGateway;
import com.asktech.admin.exception.JwtIllegalArgumentException;
import com.asktech.admin.exception.JwtMalformedJwtException;
import com.asktech.admin.exception.JwtSignatureException;
import com.asktech.admin.exception.JwtTokenExpiredException;
import com.asktech.admin.exception.JwtUnsupportedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtValidator {

	private String secret = "XZUMS4pa2Wus+2LC+VdjM6oJoZawVcvUvc1X9Ovx1mA=";

	public LoginRequestDto validate(String token) {
		LoginRequestDto jwtUser = null;
		try {
			Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			jwtUser = new LoginRequestDto();
			jwtUser.setUserNameOrEmail(body.getSubject());
			jwtUser.setPassword((String) body.get("Password"));
			jwtUser.setUserAgent((String) body.get("UserAgent"));
			jwtUser.setIpAddress((String) body.get("ipAddress"));
		} catch (ExpiredJwtException e) {
			//Log4jLogger.saveLog("JWT Token is expired==> ");
			throw new JwtTokenExpiredException("JWT Token is expired " + AskTechGateway.JWT_EXPIRED, e);
		} catch (SignatureException se) {
			//Log4jLogger.saveLog("JWT signature is missing==> ");
			throw new JwtSignatureException("JWT signature is missing " + AskTechGateway.JWT_SIGNATURE_MISSING, se);
		} catch (MalformedJwtException me) {
			//Log4jLogger.saveLog("JWT content is missing==> ");
			throw new JwtMalformedJwtException("JWT content is missing " + AskTechGateway.JWT_FORMATE_INVALID, me);
		} catch (UnsupportedJwtException ue) {
			//Log4jLogger.saveLog("JWT is Unsupported==> ");
			throw new JwtUnsupportedException("JWT is Unsupported " + AskTechGateway.JWT_UNSUPPORTED, ue);
		} catch (IllegalArgumentException ie) {
			//Log4jLogger.saveLog("JWT is IllegalArgument==> ");
			throw new JwtIllegalArgumentException("JWT is IllegalArgument " + AskTechGateway.JWT_ILLEGAL_ARGUMENT, ie);
		}
		return jwtUser;
	}
}