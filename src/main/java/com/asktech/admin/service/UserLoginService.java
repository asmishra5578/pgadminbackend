package com.asktech.admin.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.constants.cashfree.CashFreeFields;
import com.asktech.admin.dto.admin.MerchantCreateResponse;
import com.asktech.admin.dto.login.LoginOPTResponseDto;
import com.asktech.admin.dto.login.LoginRequestDto;
import com.asktech.admin.dto.login.LoginResponseDto;
import com.asktech.admin.dto.login.LogoutRequestDto;
import com.asktech.admin.dto.merchant.MerchantUpdateReq;
import com.asktech.admin.dto.merchant.OTPConfirmation;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.UserStatus;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.mail.MailIntegration;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MobileOtp;
import com.asktech.admin.model.UserOTPDetails;
import com.asktech.admin.model.UserSession;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.MobileOtpRepository;
import com.asktech.admin.repository.UserOTPDetailsRepository;
import com.asktech.admin.security.Encryption;
import com.asktech.admin.util.SmsCallTemplate;
import com.asktech.admin.util.Utility;
import com.asktech.admin.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

import jdk.jshell.spi.ExecutionControl.UserException;

@Service
public class UserLoginService implements CashFreeFields, ErrorValues {

	@Autowired
	MerchantDetailsRepository repo;
	@Autowired
	UserOTPDetailsRepository userOTPDetailsRepository;
	@Autowired
	MobileOtpRepository mobileOtpRepository;

	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;
	@Value("${sessionExpiryTime}")
	long sessionExpiryTime;
	@Value("${otpExpiryTime}")
	long otpExpiryTime;
	@Value("${otpCount}")
	long otpCount;

	@Value("${smsSenderId}")
	String smsSenderId;

	long EXPIRATION_TIME = 60 * 24;

	@Autowired
	MailIntegration sendMail;
	@Autowired
	SmsCallTemplate smsCallTemplate;

	static Logger logger = LoggerFactory.getLogger(UserLoginService.class);

	public LoginOPTResponseDto getUserLogin(LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {

		if (dto.getPassword().isEmpty()) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantDetails user = repo.findByMerchantEmail(dto.getUserNameOrEmail());
		if (user == null) {

			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);

		}
		if (user.getPassword() != null) {
			if (dto.getPassword() == null) {
				throw new ValidationExceptions(PASSWORD_CANT_BE_BLANK,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);

			} else if (!user.getPassword().equals(Encryption.getEncryptedPasswordKMS(dto.getPassword()))) {
				throw new ValidationExceptions(PASSWORD_MISMATCH,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
			}
		} else {
			throw new ValidationExceptions(PASSWORD_MISMATCH, FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
		}

		if (user.getInitialPwdChange() == null) {
			throw new ValidationExceptions(INITIAL_PASSWORD_CHANGE_REQUEST,
					FormValidationExceptionEnums.INITIAL_PASSWORD_CHANGE_REQUIRED);
		}

		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}

		sendMobileOtp(user);
		LoginOPTResponseDto loginOPTResponseDto = new LoginOPTResponseDto();
		loginOPTResponseDto.setResponseCode("200");
		loginOPTResponseDto
				.setResponseText("Login OPT has been send to register Mobile Number and Email Id . Valida for "
						+ otpExpiryTime + " Mins");
		loginOPTResponseDto.setUserId(user.getMerchantEmail());

		return loginOPTResponseDto;
	}

	public void userResenOtp(String emailorphone) throws UserException, ValidationExceptions {
		MerchantDetails user = repo.findByMerchantEmail(emailorphone);
		if (user == null) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		sendMobileOtp(user);
	}

	public LoginResponseDto verifyOtp(int otp, LoginRequestDto dto) throws UserException, ValidationExceptions {
		logger.info("== verifyOtp ==");
		long EXPIRATION_TIME = 60 * 24;
		try {
			logger.info("otp::" + otp + "|" + Utility.convertDTO2JsonString(dto));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MobileOtp motp = mobileOtpRepository.findByOtpAndUserName(otp, dto.getUserNameOrEmail());
		// logger.info(Utility.convertDTO2JsonString(motp));
		if (motp == null) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OPT_EXPIRED, FormValidationExceptionEnums.OPT_EXPIRED);
		}
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((motp.getExpDate()).before(dat)) {
			mobileOtpRepository.delete(motp);
			throw new ValidationExceptions(OPT_EXPIRED, FormValidationExceptionEnums.OPT_EXPIRED);
		}

		MerchantDetails user = repo.findByMerchantEmail(dto.getUserNameOrEmail());
		if (user == null) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}

		if (user.getUserSession() != null) {
			user.getUserSession().setSessionStatus(0);
		}

		UserSession session = new UserSession();
		if (user.getUserSession() != null) {
			session = user.getUserSession();
		}
		session.setSessionStatus(1);
		session.setUserAgent(dto.getUserAgent());
		session.setIpAddress(dto.getIpAddress());
		session.setUser(user);
		String hash = Encryption.getSHA256Hash(UUID.randomUUID().toString() + user.getMerchantEmail());
		session.setSessionToken(hash);
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		session.setSessionExpiryDate(date);
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		session.setIdealSessionExpiry(idealDate);
		user.setUserSession(session);
		repo.save(user);

		mobileOtpRepository.delete(motp);

		LoginResponseDto loginResponseDto = new LoginResponseDto();

		loginResponseDto.setUuid(user.getUuid());
		loginResponseDto.setEmail(user.getMerchantEmail());
		loginResponseDto.setPhoneNumber(user.getPhoneNumber());
		loginResponseDto.setSessionStatus(user.getUserSession().getSessionStatus());
		loginResponseDto.setSessionToken(user.getUserSession().getSessionToken());
		loginResponseDto.setSessionExpiryDate(user.getUserSession().getSessionExpiryDate());
		if ((user.getPayoutFlag() != null) && (user.getPayoutFlag().equalsIgnoreCase("true"))) {
			loginResponseDto.setPayoutFlag("True");
		} else {
			loginResponseDto.setPayoutFlag("False");
		}

		return loginResponseDto;
	}

	private void sendMobileOtp(MerchantDetails user) throws UserException, ValidationExceptions {
		try {
			int otp = new Random().nextInt(900000) + 100000;
			String message = "Hi " + user.getMerchantName() + ", Welcome to EazyPaymentz.Your OTP for login is " + otp;
			logger.info("SMS::" + message);
			// String message = "Hi " + user.getMerchantName() + " Welcome to IMobile, Your
			// OTP for login is " + otp;
			String mobileNo = user.getPhoneNumber();
			logger.info("MOBILE::" + mobileNo);
			sendSMSMessage(mobileNo, user.getMerchantEmail(), otp);
			smsCallTemplate.smsSendbyApi(message, mobileNo, smsSenderId);
			sendMail.sendLoginOtpMail(user.getMerchantName(), user.getMerchantEmail(), user.getPhoneNumber(),
					String.valueOf(otp));
		} catch (Exception e) {
			throw new ValidationExceptions(SMS_SEND_ERROR, FormValidationExceptionEnums.SMS_SEND_ERROR);
		}
	}

	private void sendSMSMessage(String phoneNumber, String userName, int otp)
			throws UserException, ValidationExceptions {
		MobileOtp motp = null;
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		motp = mobileOtpRepository.findBymobileNo(phoneNumber);
		if (motp == null) {
			motp = new MobileOtp();			
			motp.setCount(1);
			motp.setExpDate(date);
			motp.setUserName(userName);
			motp.setMobileNo(phoneNumber);
			motp.setOtp(otp);
		} else {
			Calendar cal = Calendar.getInstance();
			Date dat = cal.getTime();
			if ((motp.getExpDate()).after(dat) && motp.getCount() >= otpCount) {

				throw new ValidationExceptions(SMS_OTP_COUNTER_REACHED,
						FormValidationExceptionEnums.TRY_AFTER_5_MINUTES);
			}
			if (motp.getCount() == otpCount) {
				motp.setCount(1);
				motp.setExpDate(date);
			} else {
				motp.setExpDate(date);
				motp.setCount(motp.getCount() + 1);
			}
			motp.setOtp(otp);
		}
		mobileOtpRepository.save(motp);
	}

	public void userLogout(LogoutRequestDto dto) throws UserException, ValidationExceptions {

		MerchantDetails user = repo.findByuuid(dto.getUuid());

		if (!(user.getUserSession().getSessionToken()).equals(dto.getSessionToken())) {
			logger.error("Session Token does not Exist");

			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		user.getUserSession().setSessionStatus(0);
		repo.save(user);
	}

	public void initiatlPasswordChange(String userNameOrEmailId, String password) throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		MerchantDetails user = repo.findByMerchantEmail(userNameOrEmailId);

		if (user == null) {
			logger.error("User ot found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPasswordKMS(password));
		user.setInitialPwdChange("Y");
		user.setUserStatus(UserStatus.ACTIVE.toString());

		repo.save(user);

	}

	public void passwordChange(String userNameOrEmailId, String password) throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		MerchantDetails user = repo.findByMerchantEmail(userNameOrEmailId);

		if (user == null) {
			logger.error("User ot found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPasswordKMS(password));

		repo.save(user);

	}

	public void forgotPassword(String userNameOrEmailId) throws ValidationExceptions {
		MerchantDetails user = repo.findByMerchantEmail(userNameOrEmailId);
		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		int otp = (new Random()).nextInt(90000000) + 10000000;

		String message = "Hi " + user.getMerchantName() + " Your forgot password OTP for change the password is " + otp;

		UserOTPDetails userOTPDetails = userOTPDetailsRepository.findByUuid(user.getUuid());
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		if (userOTPDetails == null) {
			userOTPDetails = new UserOTPDetails();
			userOTPDetails.setEmailId(user.getMerchantEmail());
			userOTPDetails.setExpDate(date);
			userOTPDetails.setMobileNo(user.getPhoneNumber());
			userOTPDetails.setModeOfTr("MAIL");
			userOTPDetails.setOptCount(0);
			userOTPDetails.setUuid(user.getUuid());
			userOTPDetails.setOtpValue(String.valueOf(otp));

		} else {
			userOTPDetails.setExpDate(date);
			userOTPDetails.setModeOfTr("MAIL");
			userOTPDetails.setOptCount(userOTPDetails.getOptCount() + 1);
			userOTPDetails.setOtpValue(String.valueOf(otp));

		}
		userOTPDetailsRepository.save(userOTPDetails);

		sendMail.sendMailForgotPassword(user.getMerchantEmail(), message,
				"Forget Password : " + user.getMerchantName());
	}

	public void forgotPasswordChange(String userNameOrEmailId, String password, String otp)
			throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		MerchantDetails user = repo.findByMerchantEmail(userNameOrEmailId);
		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		UserOTPDetails userOTPDetails = userOTPDetailsRepository.findByUuid(user.getUuid());
		if (!userOTPDetails.getOtpValue().equalsIgnoreCase(otp)) {
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}

		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((userOTPDetails.getExpDate()).before(dat)) {
			userOTPDetailsRepository.delete(userOTPDetails);
			throw new ValidationExceptions(OTP_EXPIRED, FormValidationExceptionEnums.OTP_EXPIRED);
		}

		user.setPassword(Encryption.getEncryptedPasswordKMS(password));
		userOTPDetailsRepository.delete(userOTPDetails);
		repo.save(user);
	}
	
	private MobileOtp sendSMSMessageForMerchantUpdate(String phoneNumber, String userName,int otp) throws UserException, ValidationExceptions {
		MobileOtp motp = null;
		ZonedDateTime SessionexpireTime = ZonedDateTime.now(ZoneOffset.UTC).plus(sessionExpiryTime, ChronoUnit.MINUTES);
		Date dateExpiry = Date.from(SessionexpireTime.toInstant());
		motp = mobileOtpRepository.findBymobileNo(phoneNumber);
		if (motp == null) {
			motp = new MobileOtp();
			ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
			
			Date date = Date.from(expirationTime.toInstant());
			motp.setCount(1);
			motp.setExpDate(date);
			motp.setUserName(userName);
			motp.setMobileNo(phoneNumber);
			motp.setOtpSessionExpiry(dateExpiry);
			motp.setOtpSessionId(Utility.generateAppId());
			motp.setOtp(otp);
		} else {
			Calendar cal = Calendar.getInstance();
			Date dat = cal.getTime();
			if ((motp.getExpDate()).before(dat) && motp.getCount() >= otpCount) {			
				
				logger.info("Checking the otp Counter");
				throw new ValidationExceptions(SMS_OTP_COUNTER_REACHED.replace("<AttemptCount>", String.valueOf(otpCount)), FormValidationExceptionEnums.TRY_AFTER_5_MINUTES);
			}
			logger.info("After Checking the otp Counter");
			if (motp.getCount() == otpCount) {
				motp.setCount(1);
			} else {
				motp.setCount(motp.getCount() + 1);
			}
			motp.setOtp(otp);
			motp.setOtpSessionExpiry(dateExpiry);
		}
		return mobileOtpRepository.save(motp);
	}
	
    private MobileOtp updateMerchantSendMobileOtp(MerchantDetails user, MerchantUpdateReq dto) throws UserException, ValidationExceptions {
		
		int otp = new Random().nextInt(900000) + 100000;
		String message = "Hi " + user.getMerchantName() + ", Welcome to EazyPaymentz.Your OTP for login is " + otp;
		logger.info("SMS::" + message);
		// String message = "Hi " + user.getMerchantName() + " Welcome to IMobile, Your
		// OTP for login is " + otp;
		String mobileNo = dto.getPhoneNumber();
		logger.info("MOBILE::" + mobileNo);
		MobileOtp mobileOtp = sendSMSMessageForMerchantUpdate(mobileNo, dto.getEmailId(), otp);
		logger.info("smsSenderId::" + smsSenderId);
		try {
			smsCallTemplate.smsSendbyApi(message, mobileNo, smsSenderId);
			sendMail.sendLoginOtpMail(user.getMerchantName(), dto.getEmailId(), dto.getPhoneNumber(),
				String.valueOf(otp));
		} catch (Exception e) {
			throw new ValidationExceptions(SMS_SEND_ERROR, FormValidationExceptionEnums.SMS_SEND_ERROR);
		}
		
		return mobileOtp;
}
	
	public OTPConfirmation updateMerchant(String merchantId, MerchantUpdateReq dto ) throws ValidationExceptions, NoSuchAlgorithmException, UserException {

		logger.info("merchantView In this Method.");
		
		MerchantDetails merchantDetails = repo.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		
		MobileOtp mobileOtp = updateMerchantSendMobileOtp(merchantDetails,dto);
		OTPConfirmation otpConfirmation = new OTPConfirmation();
		otpConfirmation.setUserId(dto.getEmailId());
		otpConfirmation.setOtpSessionId(mobileOtp.getOtpSessionId());
		
		return otpConfirmation;
	}
	
   public MerchantCreateResponse merchantUpdateVerifyOtp(String merchantId, int otp, MerchantUpdateReq dto , String sessionId) throws UserException, ValidationExceptions {
	   
	     MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
	     MerchantDetails merchantDetails = repo.findByMerchantID(merchantId);

	     if (merchantDetails == null) {
		  throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
	     }
	
		if (otp>1000000) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}
		MobileOtp motp = mobileOtpRepository.findByOtpAndUserNameAndOtpSessionId(otp, dto.getEmailId(), sessionId);
		if (motp == null ) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((motp.getExpDate()).before(dat)) {
			mobileOtpRepository.delete(motp);			
			throw new ValidationExceptions(MERCHANT_OTP_EXPIRED, FormValidationExceptionEnums.OTP_EXPIRED);
		}
		

		merchantDetails.setMerchantEmail(dto.getEmailId());
		merchantDetails.setPhoneNumber(dto.getPhoneNumber());
		
		repo.save(merchantDetails);
		
		mobileOtpRepository.delete(motp);
		
		merchantCreateResponse.setAppId(merchantDetails.getAppID());
		merchantCreateResponse.setEmailId(merchantDetails.getMerchantEmail());
		merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(merchantDetails.getSecretId());
		
		return merchantCreateResponse;

	}
	

}
