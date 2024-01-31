package com.asktech.admin.util;



import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.model.seam.CustomerRequest;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.UserAdminDetailsRepository;
import com.asktech.admin.repository.seam.CustomerRequestRepository;
import com.asktech.admin.security.JwtAuthentication;



@Service
public class JwtUserValidator implements ErrorValues{

	@Autowired
	MerchantDetailsRepository repo;

	@Autowired
	JwtAuthentication jwtAuth;
	@Autowired
	CustomerRequestRepository customerRequestRepository;	
	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;

	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;

	static Logger logger = LoggerFactory.getLogger(JwtUserValidator.class);

	public MerchantDetails validatebyJwtMerchantDetails(String uuid)
			throws UserException, JWTException, SessionExpiredException {
		logger.info("Inside validatebyJwtMerchantDetails()");
		
		MerchantDetails user = repo.findByuuid(uuid);
		if (user == null) {
			logger.error("User does not exist");
			
			throw new UserException("User does not exist", FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		if (!(jwtAuth.verifyjwt(user.getMerchantEmail()))) {
			logger.error("Jwt is not valid for this user");
			throw new JWTException("Jwt is not valid for this user", FormValidationExceptionEnums.JWT_NOT_VALID);
		}
		
		logger.info("After (jwtAuth.verifyjwt(user.getEmail())");
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((user.getUserSession().getSessionExpiryDate()).before(dat)) {
			logger.error("Session is Expired Please Login Again !!");
			throw new SessionExpiredException("Session is Expired Please Login Again !!",
					FormValidationExceptionEnums.SESSION_EXPIRED);
		}
		if (user.getUserSession().getIdealSessionExpiry().before(dat)) {
			logger.error("You was 10 minutes ideal on board so Ideal Session is Expired Please Login Again !!");
			throw new SessionExpiredException(
					"You was 10 minutes ideal on board so Ideal Session is Expired Please Login Again !!",
					FormValidationExceptionEnums.IDEAL_SESSION_EXPIRED);
		}
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		user.getUserSession().setIdealSessionExpiry(idealDate);
		repo.save(user);
		return user;
	}
	
	public CustomerRequest validatebyJwtUserPhone(String uuid)
			throws UserException, JWTException, SessionExpiredException {
		logger.info("Inside validatebyJwtUserPhone()");
		
		CustomerRequest customerRequest = customerRequestRepository.findAllByUuid(uuid);
		if (customerRequest == null) {
			logger.error("User does not exist");
			
			throw new UserException("User does not exist", FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		if (!(jwtAuth.verifyjwt(customerRequest.getUserEmail()))) {
			logger.error("Jwt is not valid for this user");
			throw new JWTException("Jwt is not valid for this user", FormValidationExceptionEnums.JWT_NOT_VALID);
		}
		
		logger.info("After (jwtAuth.verifyjwt(user.getEmail())");
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((customerRequest.getSessionExpiryDate()).before(dat)) {
			logger.error("Session is Expired Please Login Again !!");
			throw new SessionExpiredException("Session is Expired Please Login Again !!",
					FormValidationExceptionEnums.SESSION_EXPIRED);
		}
		if (customerRequest.getIdealSessionExpiry().before(dat)) {
			logger.error("You was 10 minutes ideal on board so Ideal Session is Expired Please Login Again !!");
			throw new SessionExpiredException(
					"You was 10 minutes ideal on board so Ideal Session is Expired Please Login Again !!",
					FormValidationExceptionEnums.IDEAL_SESSION_EXPIRED);
		}
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		customerRequest.setIdealSessionExpiry(idealDate);
		customerRequestRepository.save(customerRequest);
		return customerRequest;
	}
	
	public UserAdminDetails validatebyJwtAdminDetails(String uuid)
			throws UserException, JWTException, SessionExpiredException {
		logger.info("Inside validatebyJwtMerchantDetails()");
		
		UserAdminDetails user = userAdminDetailsRepository.findByuuid(uuid);
		if (user == null) {
			logger.error("User does not exist");
			
			throw new UserException("User does not exist", FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		if (!(jwtAuth.verifyjwt(user.getEmailId()))) {
			logger.error("Jwt is not valid for this user");
			throw new JWTException("Jwt is not valid for this user", FormValidationExceptionEnums.JWT_NOT_VALID);
		}
		
		logger.info("After (jwtAuth.verifyjwt(user.getEmail())");
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((user.getUserSession().getSessionExpiryDate()).before(dat)) {
			logger.error("Session is Expired Please Login Again !!");
			throw new SessionExpiredException("Session is Expired Please Login Again !!",
					FormValidationExceptionEnums.SESSION_EXPIRED);
		}
		if (user.getUserSession().getIdealSessionExpiry().before(dat)) {
			logger.error("You was 10 minutes ideal on board so Ideal Session is Expired Please Login Again !!");
			throw new SessionExpiredException(
					"You was 10 minutes ideal on board so Ideal Session is Expired Please Login Again !!",
					FormValidationExceptionEnums.IDEAL_SESSION_EXPIRED);
		}
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		user.getUserSession().setIdealSessionExpiry(idealDate);
		userAdminDetailsRepository.save(user);
		return user;
	}

}
