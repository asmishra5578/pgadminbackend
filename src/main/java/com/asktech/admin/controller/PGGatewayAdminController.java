package com.asktech.admin.controller;

//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.admin.AddMerchantByDistributorResponse;
import com.asktech.admin.dto.admin.AdminDetailDto;
import com.asktech.admin.dto.admin.AllMerchantDetails;
import com.asktech.admin.dto.admin.AllMerchantsAssociatedWithADistributorByDistributorIDResponse;
import com.asktech.admin.dto.admin.AllPgDetailsResponse;
import com.asktech.admin.dto.admin.BusinessAssociateCreateRequest;
import com.asktech.admin.dto.admin.CreateAdminUserRequest;
import com.asktech.admin.dto.admin.CreatePGDetailsRequest;
import com.asktech.admin.dto.admin.DistributorDetailInformationsResponse;
import com.asktech.admin.dto.admin.DistributorFromDistributorMerchantDetailsResponse;
import com.asktech.admin.dto.admin.DistributorMerchantDetailsInformationResponse;
import com.asktech.admin.dto.admin.DistributorMerchantDetailsResponse;
import com.asktech.admin.dto.admin.DistributorResponse;
import com.asktech.admin.dto.admin.FindAllResponseAddMerchantByDistributorRequest;
import com.asktech.admin.dto.admin.FindAllResponseRechargeRequestDetails;
import com.asktech.admin.dto.admin.MerchantCreateResponse;
import com.asktech.admin.dto.admin.MerchantDetailsStatusUpdateResponse;
import com.asktech.admin.dto.admin.MerchantKycDetailsResponse;
import com.asktech.admin.dto.admin.MerchantPGServiceAssociationResponse;
import com.asktech.admin.dto.admin.ProcessSettlementRequest;
import com.asktech.admin.dto.admin.ResetPasswordResponse;
import com.asktech.admin.dto.admin.UpdateDistributorDetails;
import com.asktech.admin.dto.admin.UpdateDistributorDetailsResponse;
import com.asktech.admin.dto.admin.UpdateDistributorMerchantAssociationDetails;
import com.asktech.admin.dto.admin.UpdatePGDetailsRequest;
import com.asktech.admin.dto.admin.UpdatedResponseOfRechargeRequest;
import com.asktech.admin.dto.admin.UserSessionDto;
import com.asktech.admin.dto.admin.masterList.RequestMasterBankListAssociation;
import com.asktech.admin.dto.admin.masterList.RequestMasterBankListUpdate;
import com.asktech.admin.dto.admin.masterList.RequestMasterWalletListAssociation;
import com.asktech.admin.dto.admin.masterList.RequestMasterWalletListUpdate;
import com.asktech.admin.dto.login.LoginRequestDto;
import com.asktech.admin.dto.login.LoginResponseDto;
import com.asktech.admin.dto.login.LogoutRequestDto;
import com.asktech.admin.dto.merchant.MerchantKycDocRes;
import com.asktech.admin.dto.merchant.MerchantPgDetailRes;
import com.asktech.admin.dto.merchant.MerchantRefundDto;
import com.asktech.admin.dto.merchant.MerchantUpdateReq;
import com.asktech.admin.dto.merchant.OTPConfirmation;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeRequestDto;
import com.asktech.admin.dto.utility.ErrorResponseDto;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.KycStatus;
import com.asktech.admin.enums.PGServices;
import com.asktech.admin.enums.RefundStatus;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.enums.UserStatus;
import com.asktech.admin.enums.UserTypes;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.BusinessAssociate;
import com.asktech.admin.model.BusinessAssociateCommissionDetails;
import com.asktech.admin.model.MerchantBankDetails;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantPGServices;
import com.asktech.admin.model.PGServiceDetails;
import com.asktech.admin.model.RefundDetails;
import com.asktech.admin.model.ServiceWisePaymentThresold;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.repository.UserAdminDetailsRepository;
import com.asktech.admin.schedular.SwitchPgServiceDynamically;
import com.asktech.admin.schedular.ThresholdUpdateService;
import com.asktech.admin.security.Encryption;
import com.asktech.admin.security.JwtGenerator;
import com.asktech.admin.service.PGGatewayAdminService;
import com.asktech.admin.service.PaymentMerchantService;
import com.asktech.admin.service.UserLoginService;
import com.asktech.admin.util.FileUpload;
import com.asktech.admin.util.JwtUserValidator;
import com.asktech.admin.util.Utility;
import com.asktech.admin.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class PGGatewayAdminController implements ErrorValues {
//pgadminbackend_kms
	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);

	@Autowired
	UserLoginService UserLoginService;
	@Autowired
	private FileUpload fileStorageService;
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	
	@Autowired
	private JwtGenerator jwtGenerator;
	@Autowired
	private JwtUserValidator jwtUserValidator;

	@Autowired
	SwitchPgServiceDynamically switchPgServiceDynamically;
	@Autowired
	ThresholdUpdateService thresholdUpdateService;

	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	
/**Method Handlers starts*/
	


	@PostMapping(value = "/admin/login")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> loginAdmin(@RequestBody LoginRequestDto dto) throws UserException,
			NoSuchAlgorithmException, IOException, ValidationExceptions, jdk.jshell.spi.ExecutionControl.UserException {
		ErrorResponseDto erdto = new ErrorResponseDto();

		if (dto.getUserNameOrEmail().isEmpty()) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		LoginResponseDto loginResponseDto = pgGatewayAdminService.getAdminLogin(dto);

		if (loginResponseDto.getUserType().equalsIgnoreCase(UserTypes.ADMIN.toString())) {
			String jwt = (jwtGenerator.generate(dto));
			loginResponseDto.setJwtToken(jwt);
		} else {
			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.USER_ROLE_ISSUE, null,
					"User is not have Admin Previledges.", false, 100);
			return ResponseEntity.ok().body(erdto);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Logged In Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("loginData", loginResponseDto);
		return ResponseEntity.ok().body(sdto);
	}
	
	
	@PutMapping(value = "api/admin/logout")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> userLogout(@RequestBody LogoutRequestDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		SuccessResponseDto sdto = new SuccessResponseDto();
		if (StringUtils.isEmpty(dto.getUuid()) || StringUtils.isEmpty(dto.getSessionToken())) {
			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(dto.getUuid());
		pgGatewayAdminService.userLogout(dto, userAdminDetails);
		sdto.getMsg().add("user logged out successfully");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);

		return ResponseEntity.ok().body(sdto);
	}
	
	@PostMapping(value = "api/admin/getSession")
	@ApiOperation(value = "Admin or Retailer check session ACTIVE or not.", authorizations = {
			@Authorization(value = "apiKey") })
	ResponseEntity<Object> getSession(@RequestBody UserSessionDto userSessionDto) throws Exception {
		SuccessResponseDto successResponseDto = new SuccessResponseDto();
		ErrorResponseDto erdto = new ErrorResponseDto();
		if (StringUtils.isEmpty(userSessionDto.getUuid()) || StringUtils.isEmpty(userSessionDto.getSessionToken())) {
			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(userSessionDto.getUuid());
		pgGatewayAdminService.getAdminSession(userSessionDto, userAdminDetails);
		successResponseDto.getMsg().add("Session is Active");
		successResponseDto.setSuccessCode(SuccessCode.SESSION_ACTIVE);
		// Log4jLogger.saveLog("Session is Active==> ");
		return ResponseEntity.ok().body(successResponseDto);
	}
	
	@GetMapping(value = "/api/admin/getAdminDetails")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getGetAdminDetails(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("adminDetail", userAdminDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "admin/getPassword")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getGetAdminPwd(@RequestParam("emailId") String emailId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByEmailId(emailId);
		userAdminDetails.setPassword(Encryption.getDecryptedPasswordKMS(userAdminDetails.getPassword()));

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("adminDetail", userAdminDetails);
		return ResponseEntity.ok().body(sdto);
	}
	@PostMapping(value = "/admin/forgetPasswordGenerateOTP")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPassword(@RequestParam("userEmail") String userNameOrEmailId)
			throws ValidationExceptions {

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		pgGatewayAdminService.forgotPassword(userNameOrEmailId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add(
				"OTP has been send to your mail id , The OTP will valid for 2 Mins, Please change the password within timeline.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}
	
	
	
	

	@PostMapping(value = "/admin/forgetPasswordResendOTP")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPasswordResendOtp(@RequestParam("userEmail") String userNameOrEmailId)
			throws ValidationExceptions {

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		pgGatewayAdminService.forgotPassword(userNameOrEmailId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add(
				"OTP has been send to your mail id , The OTP will valid for 2 Mins, Please change the password within timeline.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/admin/forgetPasswordChangeWithOTP")
	@ApiOperation(value = "Admin password change to secure PG gateway ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPasswordChange(@RequestParam("userEmail") String userNameOrEmailId,
			@RequestParam("password") String password, @RequestParam("mailOtp") String otp)
			throws ValidationExceptions {

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		pgGatewayAdminService.forgotPasswordChange(userNameOrEmailId, password, otp);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password has been changed Successfully!.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/super/login")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> loginSuper(@RequestBody LoginRequestDto dto) throws UserException,
			NoSuchAlgorithmException, IOException, ValidationExceptions, jdk.jshell.spi.ExecutionControl.UserException {
		ErrorResponseDto erdto = new ErrorResponseDto();

		if (StringUtils.isEmpty(dto.getUserNameOrEmail())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		LoginResponseDto loginResponseDto = pgGatewayAdminService.getAdminLogin(dto);
		if (loginResponseDto.getUserType().equalsIgnoreCase(UserTypes.SUPER.toString())) {

			String jwt = (jwtGenerator.generate(dto));
			loginResponseDto.setJwtToken(jwt);
		} else {
			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.USER_ROLE_ISSUE, null,
					"User is not have Super Admin Role", false, 100);
			return ResponseEntity.ok().body(erdto);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Logged In Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("loginData", loginResponseDto);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "api/admin/passwordChange")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> initialPasswordChange(@RequestParam("userName") String userNameOrEmailId,
			@RequestParam("password") String password)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		SuccessResponseDto sdto = new SuccessResponseDto();
		if (userNameOrEmailId == null) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		pgGatewayAdminService.passwordChange(userNameOrEmailId, password);

		sdto.getMsg().add("Password has been changed successsfully!");
		sdto.setSuccessCode(SuccessCode.RESET_PASSWORD_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}
	
	

	@SuppressWarnings("deprecation")
	@PostMapping(value = "/api/adminCreate")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createAdminUser(@RequestBody CreateAdminUserRequest createAdminUserRequest,
			@RequestParam String uuid) throws UserException, NoSuchAlgorithmException, IOException,
			ValidationExceptions, UserException, JWTException, SessionExpiredException {
		ErrorResponseDto erdto = new ErrorResponseDto();

		logger.info("Input DTO :: " + Utility.convertDTO2JsonString(createAdminUserRequest));

		if (StringUtils.isEmpty(createAdminUserRequest.getEmailId())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		if (StringUtils.isEmpty(createAdminUserRequest.getPhoneNumber())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		if (StringUtils.isEmpty(createAdminUserRequest.getUserName())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (userAdminDetails.getUserType().equalsIgnoreCase(UserTypes.ADMIN.toString())) {

			userAdminDetails = pgGatewayAdminService.createAdminUser(createAdminUserRequest);
		} else {
			throw new ValidationExceptions(SUPER_USER_ROLE, FormValidationExceptionEnums.USER_ROLE_ISSUE);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin User Registered Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("loginData", userAdminDetails);
		return ResponseEntity.ok().body(sdto);
	}
	
	
	

/**@author modified by abhimanyu start ***/	
	@GetMapping(value = "/api/admin/merchantPGServiceByID")
	public ResponseEntity<?> getMerchantPGServiceDetailsByID(@RequestParam("uuid") String uuid,@RequestParam(value = "merchantId") String merchantId){
		
		List<MerchantPGServices>  listOfMerchantPGService = pgGatewayAdminService.getMerchantPGServiceDetailsByID(uuid,merchantId);
		return ResponseEntity.ok().body(listOfMerchantPGService);
	}
	
	//For, Add merchant  by distributor
	
	
	//1.
	//Put API update API Add merchant Request
	@PutMapping("api/updateStatusOfAddMerchantRequestByDistributor")
	@ApiOperation(value = "Update Distributor Status", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateStatusOfAddMerchantRequestByDistributor(@RequestParam("uuid") String uuid,
			@RequestParam("distributorID") String distributorID,
			@RequestParam("addMerchantRequestUuid") String addMerchantRequestUuid, 
			@RequestParam("approval") String approval,
			@RequestParam("flagValue") String flagValue,
			@RequestParam("status") String status) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		
		AddMerchantByDistributorResponse addMerchantByDistributorResponse = pgGatewayAdminService.updateStatusOfAddMerchantRequestByDistributorToAdmin(uuid,distributorID,addMerchantRequestUuid,approval,flagValue,status);
		
		
		return ResponseEntity.ok().body(addMerchantByDistributorResponse);
	}
	//2.
	//Put API update API  Recharges create Request
	@PutMapping("api/updateStatusOfRechargeRequestForMerchantByDistributor")
	@ApiOperation(value = "Update Distributor Status", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateStatusOfRechargeRequestForMerchantByDistributor(@RequestParam("uuid") String uuid,
			@RequestParam("distributorID") String distributorID,
			@RequestParam("merchantID") String merchantID,
			@RequestParam("rechargeRequestUuid") String rechargeRequestUuid,
			@RequestParam("approval") String approval,
			@RequestParam("status") String status) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		
		UpdatedResponseOfRechargeRequest updatedResponseOfRechargeRequest = pgGatewayAdminService.updateStatusOfRechargeRequestForMerchantByDistributorToAdmin(uuid,distributorID,merchantID,rechargeRequestUuid,approval,status);
		
		
		return ResponseEntity.ok().body(updatedResponseOfRechargeRequest);
	}
	// findAllByDistributorIDAndFromDateUpToDateAndStatus is not working as expected, fix this query
	//Get API for all data with parameters
	//3.
	@GetMapping("api/findAllFromAddMerchantRequestByDistributorWith3Param")
	public ResponseEntity<?> findAllFromAddMerchantRequestByDistributorToAdminWith3Param(@RequestParam String uuid,@RequestParam("distributorID") String distributorID,@RequestParam("fromDate") String fromDate,@RequestParam("upToDate") String upToDate,@RequestParam("status") String status ) throws UserException, JWTException, SessionExpiredException, ValidationExceptions{
		// findAllByDistributorIDAndFromDateUpToDateAndStatus is not working as expected, fix this query
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = pgGatewayAdminService.findAllRequestsFromAddMerchantRequestByDistributorToAdminWith3Parameters(uuid,distributorID,fromDate,fromDate,status);
		return ResponseEntity.ok().body(findAllResponseAddMerchantByDistributorRequest);
	}
	//Get API for all data with parameters
	//4.
	@GetMapping("api/findAllFromAddMerchantRequestByDistributorWith2Param")
	public ResponseEntity<?> findAllFromAddMerchantRequestByDistributorToAdminWith2Param(@RequestParam String uuid,@RequestParam("distributorID") String distributorID,@RequestParam("status") String status ) throws UserException, JWTException, SessionExpiredException, ValidationExceptions{
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = pgGatewayAdminService.findAllRequestsFromAddMerchantRequestByDistributorToAdminWith2Parameters(uuid,distributorID,status);
		return ResponseEntity.ok().body(findAllResponseAddMerchantByDistributorRequest);
	}
	//Get API for all data with parameters
	//5.
	@GetMapping("api/findAllFromAddMerchantRequestByDistributorByDistributorID")
	public ResponseEntity<?> findAllFromAddMerchantRequestByDistributorToAdminByDistributorID(@RequestParam String uuid,@RequestParam("distributorID") String distributorID) throws UserException, JWTException, SessionExpiredException, ValidationExceptions{
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = pgGatewayAdminService.findAllRequestsFromAddMerchantRequestByDistributorToAdminByDistributorID(uuid,distributorID);
		return ResponseEntity.ok().body(findAllResponseAddMerchantByDistributorRequest);
	}
	//Get API for all data with parameters
	//6.
	@GetMapping("api/findAllFromRechargeRequestForMerchantByDistributorByDistributorID")
	public ResponseEntity<?> findAllFromRechargeRequestForMerchantByDistributorToAdminByDistributorID(@RequestParam("uuid") String uuid,@RequestParam("distributorID") String distributorID) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = pgGatewayAdminService.findAllFromRechargeRequestForMerchantByDistributorToAdminByDistributorID(uuid,distributorID);
		
		return ResponseEntity.ok().body(findAllResponseRechargeRequestDetails);
	}
	//Get API for all data with parameters
	//7.
	@GetMapping("api/findAllFromRechargeRequestForMerchantByDistributorWith3Param")
	public ResponseEntity<?> findAllFromRechargeRequestForMerchantByDistributorToAdminWith3Param(@RequestParam String uuid,@RequestParam("distributorID") String distributorID,@RequestParam("fromDate") String fromDate,@RequestParam("upToDate") String upToDate,@RequestParam("status") String status) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = pgGatewayAdminService.findAllFromRechargeRequestForMerchantByDistributorToAdminWith3Parameters(uuid,distributorID,fromDate,fromDate,status);
		
		return ResponseEntity.ok().body(findAllResponseRechargeRequestDetails);
	}
	//Get API for all data with parameters
	//8.
	@GetMapping("api/findAllFromRechargeRequestForMerchantByDistributorWith2Param")
	public ResponseEntity<?> findAllFromRechargeRequestForMerchantByDistributorToAdminWith2Param(@RequestParam String uuid,@RequestParam("distributorID") String distributorID,@RequestParam("status") String status) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = pgGatewayAdminService.findAllFromRechargeRequestForMerchantByDistributorToAdminWith2Parameters(uuid,distributorID,status);
		
		return ResponseEntity.ok().body(findAllResponseRechargeRequestDetails);
	}
	
	//9.
	//create a distributor
	@PostMapping("api/createDistributor")
	@ApiOperation(value = "Create Distributor by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createDistributor(@RequestBody String createDistributorRequest, @RequestParam String uuid) throws NoSuchAlgorithmException, ValidationExceptions,JWTException, SessionExpiredException, UserException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		DistributorResponse distributorResponse = pgGatewayAdminService.createDistributor(createDistributorRequest, uuid);

		return ResponseEntity.ok().body(distributorResponse);
	}
	//Get API for all data
	//10.
	@GetMapping("api/findAllFromAddMerchantRequestByDistributor")
	public ResponseEntity<?> findAllFromAddMerchantRequestByDistributorToAdmin(@RequestParam("uuid") String uuid) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = pgGatewayAdminService.findAllFromAddMerchantRequestByDistributorToAdmin(uuid);
		
		return ResponseEntity.ok().body(findAllResponseAddMerchantByDistributorRequest);
	}
	//POST API for create merchant by distributorID, request are stored in table
	//11.
	//create  merchant distributorId wise
	@PostMapping("api/createMerchantbydistributorid")
	public ResponseEntity<?> createMerchantByDistributorID(@RequestParam String uuid,@RequestParam("distributorID") String distributorID,@RequestParam("addMerchantRequestUuid") String addMerchantRequestUuid) throws UserException, JWTException, SessionExpiredException, NoSuchAlgorithmException,ValidationExceptions {
		// jsonStringCreateMerchantRequest
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.createMerchantByDistributorID(uuid, distributorID,addMerchantRequestUuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Registered Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_CREATED);
		sdto.getExtraData().put("merchantDetail", merchantCreateResponse);
		return ResponseEntity.ok().body(sdto);

	}
	//Get API for all data
	//12.
	@GetMapping("api/findAllFromRechargeRequestForMerchantByDistributor")
	public ResponseEntity<?> findAllFromRechargeRequestForMerchantByDistributorToAdmin(@RequestParam("uuid") String uuid) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = pgGatewayAdminService.findAllFromRechargeRequestForMerchantByDistributorToAdmin(uuid);
		
		return ResponseEntity.ok().body(findAllResponseRechargeRequestDetails);
	}
	
	//Get API for all data
	//13..find all distributors
	@GetMapping("api/findAllDistributor/informations")
	@ApiOperation(value = "Distributor Details Informations", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> findAllDistributorDetailInformations(@RequestParam String uuid) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		DistributorDetailInformationsResponse distributorDetailInformationsResponse = pgGatewayAdminService.findAllDistributorDetailsInformation(uuid);

		return ResponseEntity.ok().body(distributorDetailInformationsResponse);

	}
	// POST API create Association of Distributor and Merchant
	//14.
	@PostMapping("api/createDistributorMerchantDetails")
	@ApiOperation(value = "create Distributor Merchant Assoctaion Details.", authorizations = {@Authorization(value = "apiKey") })
	public ResponseEntity<?> associateDistributorMerchantDetails(@RequestParam("uuid") String uuid,@RequestParam("merchantID") String merchantID,@RequestParam("distributorID") String distributorID) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		DistributorMerchantDetailsResponse distributorMerchantDetailsResponse = pgGatewayAdminService.associateDistributorMerchantDetails(merchantID, distributorID, uuid);

		return ResponseEntity.ok().body(distributorMerchantDetailsResponse);
	}
	
	//15.
	// update status of distributor 
	@PutMapping("api/update/distributor/status")
	@ApiOperation(value = "Update Distributor Status", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateDistributorStatus(@RequestParam("uuid") String uuid,@RequestParam("distributorID") String distributorID, @RequestParam("status") String status) throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		UpdateDistributorDetails updateDistributorDetails = pgGatewayAdminService.updateDistributorStatus(uuid, distributorID,status);

		return ResponseEntity.ok().body(updateDistributorDetails);
	}
	
	//16.
	// update status
	@PutMapping("api/update/DistributorMerchantDetails/status")
	@ApiOperation(value = "Update DistributorMerchantDetails Status", authorizations = {@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateDistributorMerchantDetailsStatus(@RequestParam("uuid") String uuid, @RequestParam("distributorID") String distributorID,@RequestParam("merchantID") String merchantID,@RequestParam("status") String status) throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		DistributorMerchantDetailsResponse distributorMerchantDetailsResponse = pgGatewayAdminService.updateDistributorMerchantDetailsStatus(uuid, distributorID,merchantID,status);

		return ResponseEntity.ok().body(distributorMerchantDetailsResponse);
	}
	//17.
	// update all Information of Distributor by dto
	@PutMapping("api/update/updateDistributorDetails")
	public ResponseEntity<?> updateDistributorDetails(@RequestParam("uuid") String uuid,@RequestParam("distributorID") String distributorID,@RequestBody UpdateDistributorDetails updateDistributorDetails) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		UpdateDistributorDetailsResponse updateDistributorDetailsResponse = pgGatewayAdminService.updateDistributorDetails(uuid, distributorID, updateDistributorDetails);

		return ResponseEntity.ok().body(updateDistributorDetailsResponse);
	}
		
	//18.
	//update all Information of DistributorMerchantAssociation by dto
	@PutMapping("api/update/updateDistributorMerchantAssociationDetails")
	public ResponseEntity<?> updateDistributorMerchantAssociationDetails(@RequestParam("uuid") String uuid,@RequestParam("distributorID") String distributorID,@RequestParam("merchantID") String merchantID, @RequestBody UpdateDistributorMerchantAssociationDetails updateDistributorMerchantAssociationDetails) throws UserException, JWTException, SessionExpiredException{
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		DistributorMerchantDetailsResponse distributorMerchantDetailsResponse = pgGatewayAdminService.updateDistributorMerchantAssociationDetails(uuid, distributorID, merchantID, updateDistributorMerchantAssociationDetails);
		 return ResponseEntity.ok().body(distributorMerchantDetailsResponse);// add headers
	}
	//19.
	// DistributorID, MerchandID , rechargeRequestUuid Recharge request Processing
	@PostMapping("api/processingRechargeRequest")
	public ResponseEntity<?> processRechargeRequestByDistributorForMerchantOfSomeAmount(@RequestParam("uuid") String uuid,@RequestParam("distributorID") String distributorID, @RequestParam("merchantID") String merchantID ,@RequestParam("rechargeRequestUuid") String rechargeRequestUuid) throws UserException, JWTException, SessionExpiredException{
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
	
		pgGatewayAdminService.processingRechargeRequestedByDistributorForMerchantOfSomeAmount(uuid,distributorID,merchantID,rechargeRequestUuid);

		return ResponseEntity.ok().body("");
	}
	
	//20.
	// update status of merchant
	@PutMapping("api/update/merchant/status")
	@ApiOperation(value = "Update Merchant Status", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantStatus(@RequestParam("uuid") String uuid, @RequestParam("merchantID") String merchantID, @RequestParam("status") String status) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		MerchantDetailsStatusUpdateResponse merchantDetailsStatusUpdateResponse = pgGatewayAdminService.updateMerchantStatus(uuid, merchantID, status);

		return ResponseEntity.ok().body(merchantDetailsStatusUpdateResponse);
	}
	
	
	//21. Find DistributorMerchnatAsscoiationDetails
	@GetMapping("api/findAllDistributorMerchantAsso/informations")
	public ResponseEntity<DistributorMerchantDetailsInformationResponse> findAllDistributorMerchantDetailInformations(@RequestParam String uuid) throws ValidationExceptions, UserException, JWTException, SessionExpiredException{

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		DistributorMerchantDetailsInformationResponse distributorMerchantDetailsInformationResponse = pgGatewayAdminService.findAllDistributorMerchantDetailsInformation(uuid);
		
		
		return   ResponseEntity.ok().body(distributorMerchantDetailsInformationResponse);
	}
	//22.
	// Find Distributor by MerchandID
	@GetMapping("/api/findDistributor/informations")
	@ApiOperation(value = "Find A Distributor Details Informations By MerchantID", authorizations = {@Authorization(value = "apiKey") })
	public ResponseEntity<?> findDistributorDetailsByMerchantID(@RequestParam String uuid, @RequestParam String merchantID) throws ValidationExceptions, JWTException, SessionExpiredException, UserException{

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		DistributorFromDistributorMerchantDetailsResponse distributorFromDistributorMerchantDetailsResponse = pgGatewayAdminService.findDistributorByMerchantID(uuid, merchantID);
		
		return  ResponseEntity.ok().body(distributorFromDistributorMerchantDetailsResponse);
		
	}
	
	//23.
	// Find all Merchant associated by DistributorID
	@GetMapping("/api/findAllMerchantsAssociatedWithADistributor/informations")
	@ApiOperation(value = "All Merchants Associated With A Distributor By DistributorID", authorizations = {@Authorization(value = "apiKey") })
	public ResponseEntity<?> findAllMerchantsAssociatedWithADistributorByDistributorID(@RequestParam String uuid, @RequestParam String distributorID) throws ValidationExceptions, UserException, JWTException, SessionExpiredException{

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		AllMerchantsAssociatedWithADistributorByDistributorIDResponse allMerchantsAssociatedWithADistributorByDistributorIDResponse = pgGatewayAdminService.findAllMerchantsAssociatedWithADistributorByDistributorID(uuid, distributorID);
		
		return  ResponseEntity.ok().body(allMerchantsAssociatedWithADistributorByDistributorIDResponse);
		
	}
	
	//24.
	// Change password of merchant
	@PutMapping("/api/admin/changeMechantPassword")
	@ApiOperation(value = "change Merchant Password by Admin")
	public ResponseEntity<?> changeMerchantPassword(@RequestParam("uuid") String uuid,@RequestParam("merchantId") String merchantId)throws UserException, JWTException, SessionExpiredException, InvalidKeyException, IllegalBlockSizeException,BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		Map<Object, Object> merchantDetails = null;
		if (userAdminDetails != null) {
			merchantDetails = pgGatewayAdminService.changeMerchantPassword(merchantId);
		}

		ResetPasswordResponse resetPasswordResponseDto = null;

		if (merchantDetails != null) {

			List<String> msg = new ArrayList<>();
			msg.add("MerchandId password is changed successfully");
			boolean status = true;
			int statusCode = HttpStatus.SC_OK;
			Map<String, Object> extraData = new HashMap<>();
			// MerchantDetails m = (MerchantDetails) merchantDetails.get("merchantDetails");
			extraData.put("Password changed successfully", "Password changed successfully of Merchant with Id :  " + ((MerchantDetails) merchantDetails.get("merchantDetails")).getMerchantID());
			extraData.put("MerchantDetailsKey", merchantDetails);

			resetPasswordResponseDto = new ResetPasswordResponse(msg, status, "", statusCode, extraData);
			return ResponseEntity.ok().body(resetPasswordResponseDto);
		} else {
			resetPasswordResponseDto = new ResetPasswordResponse(null, false, "MERCHANT_ID_NOT_FOUND", 404, null);
			resetPasswordResponseDto.getMsg().add("merchant does not exist");
		}

		return ResponseEntity.ok().body(resetPasswordResponseDto);
	}

	//Create A New Merchant by Admin User.
	//MerchantCreateRequest
	@PostMapping("api/createMerchant")
	@ApiOperation(value = "Create A New Merchant by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchant(@RequestBody String jsonStringCreateMerchantRequest, @RequestParam("uuid") String uuid,@RequestParam(value = "pgId",required = false)String pgId,@RequestParam(value = "serviceFlag",required = false)String serviceFlag) throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions, UserException, JWTException, SessionExpiredException {
		//jsonStringCreateMerchantRequest
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.createMerchant(jsonStringCreateMerchantRequest, uuid,pgId,serviceFlag);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Registered Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_CREATED);
		sdto.getExtraData().put("merchantDetail", merchantCreateResponse);
		return ResponseEntity.ok().body(sdto);
	}
	
	//create a NEW PG
	@PostMapping("api/createPGDetails")
	@ApiOperation(value = "Create a PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createPGDetails(@RequestBody CreatePGDetailsRequest createPGDetailsRequest, @RequestParam String uuid) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		System.out.println("getPgSecretId"+createPGDetailsRequest.getPgSecretId());
		System.out.println("getPgSecretKey"+createPGDetailsRequest.getPgSecretKey());

		return ResponseEntity.ok().body(pgGatewayAdminService.createPg(createPGDetailsRequest, userAdminDetails.getUserId()));
	}
	
	//UPDATE STATUS A PG- | 
	@PutMapping(value = "api/admin/updatePGDetails")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminUpdatePGDetails(@RequestParam("uuid") String uuid, @RequestParam("pgUuid") String pgUuid, @RequestParam("status") String statusUpdate) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		return ResponseEntity.ok().body(pgGatewayAdminService.updatePGDetails(userAdminDetails.getUuid(), pgUuid, statusUpdate));
	
	
	}

	@PutMapping(value = "api/payout/updateStatusPayoutMerchant")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateStatusPayoutMerchant(@RequestParam("status")String status,@RequestParam("merchantId")String merchantId) throws ValidationExceptions{
	
		return ResponseEntity.ok().body(pgGatewayAdminService.updateStatusPayoutMerchant(status,merchantId));

	}


	//updatePgConfigurationDetails | UPDATE VALUE OF pgDailyLimit FOR A PG-
	@PutMapping("api/updatePgConfigurationDetails")
	@ApiOperation(value = "update Merchant Details with OTP verification.", authorizations = {@Authorization(value = "apiKey") })
	public ResponseEntity<?> updatePgConfigurationDetails(@RequestParam("uuid") String uuid, @RequestBody UpdatePGDetailsRequest dto)throws ValidationExceptions, UserException, JWTException, SessionExpiredException, jdk.jshell.spi.ExecutionControl.UserException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		SuccessResponseDto sdto = pgGatewayAdminService.updatePg(dto, userAdminDetails.getUserId());

		return ResponseEntity.ok().body(sdto);
	}

	//createPgServices  [UPI Wallet Cards NB UPI_QR GPAY EMI]
	@PostMapping("api/createPGServices")
	@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createPGServices(
			@RequestParam String uuid, 
			@RequestParam("pgUuid") String pgUuid,
			@RequestParam("pgServices") String pgServices,
			@RequestParam("defaultTag") String defaultTag,
			@RequestParam("thresoldMonth") String thresoldMonth,
			@RequestParam("thresoldDay") String thresoldDay,
			@RequestParam("thresoldWeek") String thresoldWeek,
			@RequestParam("thresold3Month") String thresold3Month,
			@RequestParam("thresold6Month") String thresold6Month,
			@RequestParam("thresoldYear") String thresoldYear) throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		

		PGServiceDetails pgServiceDetails = pgGatewayAdminService.createPgServices(pgUuid, pgServices,userAdminDetails.getUserId(), defaultTag, thresoldMonth, thresoldDay, thresoldWeek, thresold3Month, thresold6Month, thresoldYear);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", pgServiceDetails);
		return ResponseEntity.ok().body(sdto);
	}

	//UPDATE STATUS OF A pg'S SERVICE
	//api/admin/updatePGService
	@PutMapping(value = "api/admin/updatePGService")
	@ApiOperation(value = "UPDATE STATUS OF A pg'S SERVICE", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminUpdatePGServices(@RequestParam("uuid") String uuid,@RequestParam("pgUuid") String pgUuid, @RequestParam("status") String statusUpdate, @RequestParam("service") String service) throws Exception {
		

		// 1. find Admin in DB
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		// updatePGService for a PG
		return ResponseEntity.ok().body(pgGatewayAdminService.updatePGService(userAdminDetails.getUuid(), pgUuid, statusUpdate, service));
	}
	
	//A Merchant is being assigned a new PG
	//api/createMerchantPGDetails
	@PostMapping("api/createMerchantPGDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {@Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchantPGDetals(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("pgUuid") String pgUuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		
		MerchantPgDetailRes merchantPGDetails = pgGatewayAdminService.associatePGDetails(merchantId, pgUuid, uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_OK);
		sdto.getExtraData().put("merchantDetail", merchantPGDetails);
		return ResponseEntity.ok().body(sdto);
	}
	
	
	
	
	// UPDATE THE PG STATUS TO A MERCHANT START
		@PutMapping(value = "api/admin/updateMerchantPGDetails")
		@ApiOperation(value = "updateMerchantPGDetails", authorizations = { @Authorization(value = "apiKey") })
		public ResponseEntity<?> adminMerchantPGDetailsUpdate(
				@RequestParam("uuid") String uuid,
				@RequestParam("merchantId") String merchantId,
				@RequestParam("pgUuid") String pgUuid,
				@RequestParam("status") String statusUpdate)
				throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

			UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
			logger.info("User Validation done :: " + userAdminDetails.getEmailId());

			if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
				throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
			}

			return ResponseEntity.ok().body(pgGatewayAdminService.updateMerchantPGDetailsStatus(merchantId, pgUuid, statusUpdate));
		}

		
	
	//createMerchantPGServices
	@PostMapping("api/createMerchantPGServices")
	@ApiOperation(value = "Post Merchant Services from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchantPGServices(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("pgUuid") String pgUuid,
			@RequestParam("merchantService") String merchantService)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (!Validator.containsEnum(PGServices.class, merchantService)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		MerchantPGServiceAssociationResponse merchantPGServiceAssociationResponse = pgGatewayAdminService.associatePGServicesToAMerchant(pgUuid,merchantService, uuid, merchantId);

		/*
		 * SuccessResponseDto sdto = new SuccessResponseDto();
		 * sdto.getMsg().add("Request Processed Successfully !");
		 * sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		 * sdto.getExtraData().put("merchantDetail",
		 * merchantPGServiceAssociationResponse);
		 */
		return ResponseEntity.ok().body(merchantPGServiceAssociationResponse);
	}
	
	
	@GetMapping("api/getpgDetails")
	@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getPGDetails(@RequestParam String uuid)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		Object listPGConfigurationDetails = pgGatewayAdminService.getPgDetails();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", listPGConfigurationDetails);
		return ResponseEntity.ok().body(sdto);
	}
	@GetMapping("api/getAllPGDetails")
	@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllPGDetails(@RequestParam String uuid)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		

	
		return ResponseEntity.ok().body(pgGatewayAdminService.getAllPGDetails());
	}
	
	@PutMapping(value = "api/admin/updateMerchantPGServices")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantPGservicesUpdate(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("pgUuid") String pgUuid,
			@RequestParam("status") String statusUpdate,
			@RequestParam("service") String service)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		if (!Validator.containsEnum(PGServices.class, service)) {
			throw new ValidationExceptions(PG_SERVICE_NOT_FOUND, FormValidationExceptionEnums.PG_SERVICE_NOT_FOUND);
		}

		return ResponseEntity.ok().body(pgGatewayAdminService.updateMerchantPGServiceStatus(merchantId, pgUuid, statusUpdate, service,uuid));
	}
	
/*** Modified by abhimanyu end* */

	

	@GetMapping("api/getpgServiceList")
	//@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getpgServiceList(@RequestParam String uuid)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		Object listPGService = pgGatewayAdminService.getPgServiceList();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", listPGService);
		return ResponseEntity.ok().body(sdto);
	}

	// @GetMapping(value = "api/admin/getMerchantByAppid")
	// @ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	// public ResponseEntity<?> getMerchantByAppid(@RequestParam("uuid") String uuid, @RequestParam("uuid") String appid)
	// 		throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

	// 	UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
	// 	return ResponseEntity.ok().body();
	// }

	
	
	


	@GetMapping(value = "api/admin/merchantStatus")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantStatus(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusAdmin(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantDashboardDetails")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantDashboardDet(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantDashboardDets(userAdminDetails));
	}

	@GetMapping(value = "/api/admin/merchantByNameAndId")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantByNameAndId(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "merchantName", required = false) String merchantName)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		List<AllMerchantDetails> merchantList = pgGatewayAdminService.getMerchantIdAndMerchantName(merchantId,
				merchantName);

		if (merchantList.isEmpty()) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantList);
		return ResponseEntity.ok().body(sdto);
	}

//merchant By Name And  All Pg Details
@GetMapping(value = "/api/admin/merchantByNameAndIdWithAllPG")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantByNameAndIdWithAllPG(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "merchantName", required = false) String merchantName)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		List<AllMerchantDetails> merchantList = pgGatewayAdminService.getMerchantIdAndMerchantName2(merchantId,
				merchantName);

		if (merchantList.isEmpty()) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantList);
		return ResponseEntity.ok().body(sdto);
	}
// merchantTransactionDayDatewise
	@GetMapping(value = "api/admin/merchantTransactionDayDatewise")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantTransactionDayDatewise(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(pgGatewayAdminService
				.merchantStatusTransactionDurationWithDate(userAdminDetails, start_date, end_date));
	}
// merchantTransactionLastDay
	@GetMapping(value = "api/admin/merchantTransactionLastDay")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionYesterday(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionLastDay(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionToday")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionToday(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionToday(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionCurrMonth")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionCurrMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionCurrMonth(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionLastMonth")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionLastMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionLastMonth(userAdminDetails));
	}
	
	

	// UPDATE STATUS A MERCHANT START
	@PutMapping(value = "api/admin/updateMerchantStatus")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantStatusUpdate(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestParam("status") String statusUpdate)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		return ResponseEntity.ok().body(pgGatewayAdminService.updatMerchantStatus(userAdminDetails.getUuid(), merchantId, statusUpdate));
	}

	
	
	// UPDATE THE PG STATUS TO A MERCHANT END
	
	
	
	

	
// api/admin/transactionDetailsDateFilter
	@GetMapping(value = "/api/admin/transactionDetailsDateFilter")
	@ApiOperation(value = "Admin User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminTransactionDetailsDateWise(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestParam("dateFrom") String dateFrom,
			@RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (!Validator.isValidateDateFormat(dateFrom) || dateFrom == null) {
			logger.info("Date validation error 1 ... " + dateTo);
			throw new ValidationExceptions(DATE_FORMAT_VALIDATION, FormValidationExceptionEnums.DATE_FORMAT_VALIDATION);
		}
		if (dateTo.length() != 0) {
			if (!Validator.isValidateDateFormat(dateTo)) {
				logger.info("Date validation error ... " + dateTo);
				throw new ValidationExceptions(DATE_FORMAT_VALIDATION,
						FormValidationExceptionEnums.DATE_FORMAT_VALIDATION);
			}
		}

		return ResponseEntity.ok().body(pgGatewayAdminService.getTransactiilteronDetailsWithDateF(merchantId, dateFrom, dateTo));
	}

	
	
	
	//processsettlement
	@PostMapping(value = "/api/admin/settlement")
	@ApiOperation(value = "Admin User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminsettlement(@RequestParam("uuid") String uuid,
			@RequestBody ProcessSettlementRequest processSettlementRequest)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.processsettlement(userAdminDetails, processSettlementRequest));
	}

	

	@PostMapping(value = "/api/admin/createBusinessAssociate")
	@ApiOperation(value = "Create Business Associate with MerchantId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createBusinessAssociate(
			@RequestParam("uuid") String uuid,
			@RequestBody BusinessAssociateCreateRequest businessAssociateCreateRequest)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		BusinessAssociate businessAssociate = pgGatewayAdminService
				.createBusinessAssociate(businessAssociateCreateRequest);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("businessAssociate", businessAssociate);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/api/admin/createBusinessAssociateCommission")
	@ApiOperation(value = "Create Business Associate with MerchantId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createBusinessAssociateCommission(
			@RequestParam("uuid") String uuid,
			@RequestParam("busiAssociateuuid") String busiAssociateuuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("commType") String commType,
			@RequestParam("serviceType") String serviceType,
			@RequestParam("serviceSubType") String serviceSubType,
			@RequestParam("commAmount") double commAmount)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = pgGatewayAdminService
				.createBusinessAssociateCommission(
						busiAssociateuuid, merchantId, commType, serviceType, serviceSubType, commAmount, uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("businessAssociateCommission", businessAssociateCommissionDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "/api/admin/updateBusinessAssociateCommission")
	@ApiOperation(value = "Create Business Associate with MerchantId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateBusinessAssociateCommission(
			@RequestParam("uuid") String uuid,
			@RequestParam("busiAssociateuuid") String busiAssociateuuid,
			@RequestParam("commissionId") int commId,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = pgGatewayAdminService
				.updateBusinessAssociateCommission(
						busiAssociateuuid, commId, status, uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("businessAssociateCommission", businessAssociateCommissionDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/merchantCommissionDetailsTotal")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAdminMerchantCommission(
			@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getMerchantCommDetails(userAdminDetails));
	}

	@GetMapping(value = "/api/admin/merchantCommissionDetailsPendingSettlement")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAdminMerchantCommissionPendindSettlement(
			@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.getAdminMerchantCommissionPendindSettlement(userAdminDetails));
	}

	@PutMapping(value = "/api/admin/updateCommissionDetails")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateCommissionDetails(
			@RequestParam("uuid") String uuid,
			@RequestParam("orderId") String orderId,
			@RequestParam("pg_comm") int pgComm,
			@RequestParam("cust_comm") int custComm,
			@RequestParam("businessAssocComm") int businessAssocComm,
			@RequestParam("merchantSettleAmount") int merchantSettleAmount)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.updateCommissionDetails(userAdminDetails, orderId, pgComm,
				custComm, businessAssocComm, merchantSettleAmount));
	}

	@PostMapping(value = "/api/admin/refundRequest")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> refundRequest(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantOrderId") String orderId,
			@RequestParam("merchantId") String merchantId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.refundRequest(userAdminDetails, orderId, merchantId));
	}

	@PutMapping(value = "/api/admin/refundUpdate")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> refundRequestUpdate(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantOrderId") String orderId,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("status") String status, @RequestParam("refundText") String refundTxt)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(RefundStatus.class, status)) {
			throw new ValidationExceptions(REFUND_STATUS, FormValidationExceptionEnums.REFUND_STATUS);
		}

		return ResponseEntity.ok().body(
				pgGatewayAdminService.refundRequestUpdate(userAdminDetails, orderId, merchantId, status, refundTxt));
	}

	@GetMapping(value = "/api/admin/getRefundDetails")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getRefundDetails(
			@RequestParam("uuid") String uuid, @RequestParam(value = "refundId", required = false) String refundid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<MerchantRefundDto> refundlist = pgGatewayAdminService.refundDetail(refundid, start_date, end_date);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundlist);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getRefundList")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getRefundList(
			@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<RefundDetails> refundlist = pgGatewayAdminService.refundlist();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundlist);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getRefundByMerchantIdOrStatus")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getRefundByMerchantIdOrStatus(
			@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantOrderId", required = false) String merchantOrderId,
			@RequestParam(value = "status", required = false) String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (pgGatewayAdminService.txnParam(status) == true) {
			if (!Validator.containsEnum(RefundStatus.class, status)) {
				throw new ValidationExceptions(REFUND_STATUS, FormValidationExceptionEnums.REFUND_STATUS);
			}
		}

		List<RefundDetails> listrefund = pgGatewayAdminService.refundDetailByStatusOrMerchantOrderId(merchantOrderId,
				status);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", listrefund);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("requestSwitchOverPgServices")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> requestSwitchOverPgServices()
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {
		switchPgServiceDynamically.checkThresholdLimit();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("RequestSwitchOver", null);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("requestRevertSwitchOver")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> requestRevertSwitchOver()
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {
		thresholdUpdateService.ThresholdUpdate();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("RevertSwitchOver", null);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/uniqueWalletList")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getUniqueWalletList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getUniqueWalletList());
	}

	@GetMapping(value = "/api/admin/getWalletAssocationReport")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getWalletAssocationReport(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getWalletAssocationReport());
	}

	@GetMapping(value = "/api/admin/getWalletList")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getWalletList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getWalletList());
	}

	@GetMapping(value = "/api/admin/uniqueBankList")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getUniqueBankList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getUniqueBankList());
	}

	@GetMapping(value = "/api/admin/getBankAssocationReport")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getBankAssocationReport(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getBankAssocationReport());
	}

	@GetMapping(value = "/api/admin/getBankList")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getBankList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getBanklist());
	}

	@PostMapping(value = "/api/admin/associateWalletList")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> associateWalletList(
			@RequestParam("uuid") String uuid,
			@RequestBody RequestMasterWalletListAssociation request)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.associateWalletListToMerchant(userAdminDetails, request));
	}

	@PostMapping(value = "/api/admin/associatebankList")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> associatebankList(
			@RequestParam("uuid") String uuid,
			@RequestBody RequestMasterBankListAssociation request)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.associateBankListToMerchant(userAdminDetails, request));
	}

	@PutMapping(value = "/api/admin/updatebankListStatus")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updatebankListStatus(
			@RequestParam("uuid") String uuid,
			@RequestBody RequestMasterBankListUpdate request)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.updatebankListStatus(userAdminDetails, request));
	}

	@PutMapping(value = "/api/admin/updateWalletListStatus")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateWalletListStatus(
			@RequestParam("uuid") String uuid,
			@RequestBody RequestMasterWalletListUpdate request)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.updateWalletListStatus(userAdminDetails, request));
	}

	@GetMapping(value = "/api/admin/getDailyReportsMerchants")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getDailyReportsMerchants(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getDailyReportsMerchants());
	}

	@GetMapping(value = "/getTempReports")
	@ApiOperation(value = "Unique WalletList", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getDailyReportsMerchants() throws UserException, JWTException, SessionExpiredException {

		return ResponseEntity.ok().body(pgGatewayAdminService.getTempReport());
	}

	@PostMapping("api/admin/createBankDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createbankDetails(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestBody MerchantBankDetails merchantBankDetails)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		MerchantBankDetails BankDetails = pgGatewayAdminService.createBankDetails(merchantBankDetails, merchantId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", BankDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping("api/admin/updateBankdetails")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateBankDetails(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestBody MerchantBankDetails merchantBankDetails)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		MerchantBankDetails BankDetails = pgGatewayAdminService.updateBankDetails(merchantId, merchantBankDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", BankDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("api/admin/getBankdetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getBankDetails(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId) throws UserException, JWTException, SessionExpiredException,
			JsonProcessingException, IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		MerchantBankDetails BankDetails = pgGatewayAdminService.getBankDetails(merchantId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", BankDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "api/admin/merchantServiceLimit")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantServiceLimit(@RequestParam("uuid") String uuid,
			@RequestParam("pgUuid") String pgUuid, @RequestParam("service") String service,
			@RequestParam("thresoldValue") long thresoldValue, @RequestParam("merchantId") String merchantId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.merchantServiceLimit(userAdminDetails.getUuid(), pgUuid, service,
						thresoldValue, merchantId));
	}

	@PutMapping(value = "api/admin/updateMerchantServiceLimit")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantServiceLimit(@RequestParam("uuid") String uuid,
			@RequestParam("service") String service,
			@RequestParam("thresoldValue") long thresoldValue, @RequestParam("merchantId") String merchantId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updateMerchantServiceLimit(service, thresoldValue, merchantId));
	}

	@GetMapping("api/admin/getMerchantServiceLimit")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantServiceLimit(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId) throws UserException, JWTException, SessionExpiredException,
			JsonProcessingException, IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		ServiceWisePaymentThresold serviceWisePaymentThresold = pgGatewayAdminService
				.getMerchantServiceLimit(merchantId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", serviceWisePaymentThresold);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping("api/admin/merchantKycDetails")
	@ApiOperation(value = "Create Merchant by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantKycDetails(@RequestParam String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam(value = "merchantLegalName", required = true) String merchantLegalName,
			@RequestParam(value = "panCardNumber", required = true) String panCardNumber,
			@RequestParam(value = "GstId", required = false) String GstId,
			@RequestParam(value = "webstieUrl", required = true) String webstieUrl,
			@RequestParam(value = "businessEntityType", required = true) String businessEntityType,
			@RequestParam(value = "productDescription", required = true) String productDescription,
			@RequestParam(value = "tanNumber", required = false) String tanNumber,
			@RequestParam(value = "regName", required = true) String regName,
			@RequestParam(value = "regAddress", required = true) String regAddress,
			@RequestParam(value = "regPinCode", required = true) String regPinCode,
			@RequestParam(value = "regNumber", required = true) String regNumber,
			@RequestParam(value = "regEmailAddress", required = true) String regEmailAddress,
			@RequestParam(value = "commName", required = false) String commName,
			@RequestParam(value = "commAddress", required = false) String commAddress,
			@RequestParam(value = "commPinCode", required = false) String commPinCode,
			@RequestParam(value = "commNumber", required = false) String commNumber,
			@RequestParam(value = "commEmailAddress", required = false) String commEmailAddress,
			@RequestParam(value = "cancelledChequeOrAccountProof", required = false) MultipartFile cancelledChequeOrAccountProof,
			@RequestParam(value = "certificateOfIncorporation", required = false) MultipartFile certificateOfIncorporation,
			@RequestParam(value = "businessPAN", required = false) MultipartFile businessPAN,
			@RequestParam(value = "certificateOfGST", required = false) MultipartFile certificateOfGST,
			@RequestParam(value = "directorKYC", required = false) MultipartFile directorKYC,
			@RequestParam(value = "aoa", required = false) MultipartFile aoa,
			@RequestParam(value = "moa", required = false) MultipartFile moa,
			@RequestParam(value = "certficateOfNBFC", required = false) MultipartFile certficateOfNBFC,
			@RequestParam(value = "certficateOfBBPS", required = false) MultipartFile certficateOfBBPS,
			@RequestParam(value = "certificateOfSEBIOrAMFI", required = false) MultipartFile certificateOfSEBIOrAMFI)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			UserException, JWTException, SessionExpiredException, IOException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		MerchantKycDetailsResponse merchantKycDetailsResponse = pgGatewayAdminService.merchantKycDetails(merchantId,
				merchantLegalName, panCardNumber, GstId,
				webstieUrl, businessEntityType, productDescription, tanNumber, regName, regAddress, regPinCode,
				regNumber,
				regEmailAddress, commName, commAddress, commPinCode, commNumber, commEmailAddress,
				cancelledChequeOrAccountProof, certificateOfIncorporation, businessPAN, certificateOfGST, directorKYC,
				aoa, moa,
				certficateOfNBFC, certficateOfBBPS, certificateOfSEBIOrAMFI);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Kyc details uploaded Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKyc", merchantKycDetailsResponse);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "api/admin/updateMerchantKycStatus")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantKycStatusUpdate(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestParam("status") String statusUpdate)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (!Validator.containsEnum(KycStatus.class, statusUpdate)) {
			throw new ValidationExceptions(KYC_STATUS, FormValidationExceptionEnums.KYC_STATUS);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updatMerchantKycStatus(merchantId, statusUpdate));
	}

	@GetMapping(value = "/api/admin/getKycByMerchantIdOrStatus")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getKycByMerchantId(
			@RequestParam("uuid") String uuid, @RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "status", required = false) String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (!Validator.containsEnum(KycStatus.class, status)) {
			throw new ValidationExceptions(KYC_STATUS, FormValidationExceptionEnums.KYC_STATUS);
		}

		return ResponseEntity.ok().body(pgGatewayAdminService.getMerchantKyc(merchantId, status));
	}

	@PutMapping("api/admin/updateMerchantKycDetails")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantKycDetails(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam(value = "merchantLegalName", required = true) String merchantLegalName,
			@RequestParam(value = "panCardNumber", required = true) String panCardNumber,
			@RequestParam(value = "GstId", required = false) String GstId,
			@RequestParam(value = "webstieUrl", required = true) String webstieUrl,
			@RequestParam(value = "businessEntityType", required = true) String businessEntityType,
			@RequestParam(value = "productDescription", required = true) String productDescription,
			@RequestParam(value = "tanNumber", required = false) String tanNumber,
			@RequestParam(value = "regName", required = true) String regName,
			@RequestParam(value = "regAddress", required = true) String regAddress,
			@RequestParam(value = "regPinCode", required = true) String regPinCode,
			@RequestParam(value = "regNumber", required = true) String regNumber,
			@RequestParam(value = "regEmailAddress", required = true) String regEmailAddress,
			@RequestParam(value = "commName", required = false) String commName,
			@RequestParam(value = "commAddress", required = false) String commAddress,
			@RequestParam(value = "commPinCode", required = false) String commPinCode,
			@RequestParam(value = "commNumber", required = false) String commNumber,
			@RequestParam(value = "commEmailAddress", required = false) String commEmailAddress,
			@RequestParam(value = "cancelledChequeOrAccountProof", required = false) MultipartFile cancelledChequeOrAccountProof,
			@RequestParam(value = "certificateOfIncorporation", required = false) MultipartFile certificateOfIncorporation,
			@RequestParam(value = "businessPAN", required = false) MultipartFile businessPAN,
			@RequestParam(value = "certificateOfGST", required = false) MultipartFile certificateOfGST,
			@RequestParam(value = "directorKYC", required = false) MultipartFile directorKYC,
			@RequestParam(value = "aoa", required = false) MultipartFile aoa,
			@RequestParam(value = "moa", required = false) MultipartFile moa,
			@RequestParam(value = "certficateOfNBFC", required = false) MultipartFile certficateOfNBFC,
			@RequestParam(value = "certficateOfBBPS", required = false) MultipartFile certficateOfBBPS,
			@RequestParam(value = "certificateOfSEBIOrAMFI", required = false) MultipartFile certificateOfSEBIOrAMFI)
			throws UserException, JWTException, SessionExpiredException, IllegalAccessException,
			NoSuchAlgorithmException, ValidationExceptions, IOException {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updateKycDetails(merchantId, merchantLegalName, panCardNumber, GstId,
						webstieUrl, businessEntityType, productDescription, tanNumber, regName, regAddress, regPinCode,
						regNumber,
						regEmailAddress, commName, commAddress, commPinCode, commNumber, commEmailAddress,
						cancelledChequeOrAccountProof, certificateOfIncorporation, businessPAN, certificateOfGST,
						directorKYC, aoa, moa,
						certficateOfNBFC, certficateOfBBPS, certificateOfSEBIOrAMFI));
	}

	@GetMapping("api/admin/getAllKycDetails")
	@ApiOperation(value = "Get All Merchants Kyc Detail as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllKycDetails(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getMerchantsKyc(start_date, end_date));
	}

	@PutMapping("api/admin/merchantKycApproveOrReject")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantKycApproveOrReject(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("status") String status, @RequestParam("reason") String reason)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(KycStatus.class, status)) {
			throw new ValidationExceptions(KYC_STATUS, FormValidationExceptionEnums.KYC_STATUS);
		}

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantsKycApproveOrReject(merchantId, status, reason));
	}

	
	
	
	//pgDetailByPGNameAndPgId
	@GetMapping(value = "/api/admin/pgDetailByPGNameAndPgId")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> pGDetailsByPGNameAndPgId(@RequestParam("uuid") String uuid,
			@RequestParam(value = "pgName", required = false) String pgName,
			@RequestParam(value = "pguuid", required = false) String pgUuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		List<AllPgDetailsResponse> pgDetail = pgGatewayAdminService.findPgDetailsByPGNameAndPgId(pgName, pgUuid);
		/*
		 * for(AllPgDetailsResponse pg: pgDetail) {
		 * 
		 * pg.getPgservices().listIterator().next().getServiceDetails();
		 * 
		 * 
		 * }
		 */
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetails", pgDetail);
		return ResponseEntity.ok().body(sdto);
	}
	
	@PutMapping("/admin/kycFileUpload")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> kycFileUpload(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("cancelledChequeOrAccountProof") MultipartFile cancelledChequeOrAccountProof,
			@RequestParam("certificateOfIncorporation") MultipartFile certificateOfIncorporation,
			@RequestParam("businessPAN") MultipartFile businessPAN,
			@RequestParam("certificateOfGST") MultipartFile certificateOfGST,
			@RequestParam("directorKYC") MultipartFile directorKYC, @RequestParam("aoa") MultipartFile aoa,
			@RequestParam("moa") MultipartFile moa, @RequestParam("certficateOfNBFC") MultipartFile certficateOfNBFC,
			@RequestParam("certficateOfBBPS") MultipartFile certficateOfBBPS,
			@RequestParam("certificateOfSEBIOrAMFI") MultipartFile certificateOfSEBIOrAMFI)
			throws ValidationExceptions, NoSuchAlgorithmException,
			IOException, UserException, JWTException, SessionExpiredException, IllegalAccessException {
		logger.info("kycFileUpload Controller");
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		MerchantKycDocRes merchantKycDocResponse = pgGatewayAdminService.merchantKycDocs(merchantId,
				cancelledChequeOrAccountProof, certificateOfIncorporation, businessPAN, certificateOfGST, directorKYC,
				aoa, moa,
				certficateOfNBFC, certficateOfBBPS, certificateOfSEBIOrAMFI);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Document Uploaded Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("kycFileUpload", merchantKycDocResponse);
		return ResponseEntity.ok().body(sdto);
		// return new UploadFileResponse(fl[0], fileDownloadUri, file.getContentType(),
		// file.getSize(), fl[1]);
	}

	@GetMapping("api/admin/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@RequestParam("uuid") String uuid, @PathVariable String fileName,
			HttpServletRequest request) throws UserException, JWTException, SessionExpiredException {
		// Load file as Resource
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@PutMapping("/admin/updateMerchantDetails")
	public ResponseEntity<?> updateMerchantDetails(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestBody MerchantUpdateReq dto) throws IllegalAccessException,
			NoSuchAlgorithmException, ValidationExceptions, UserException, JWTException, SessionExpiredException,
			jdk.jshell.spi.ExecutionControl.UserException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		OTPConfirmation merchantResponse = UserLoginService.updateMerchant(merchantId, dto);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("OTP has been send to the Specified Mobile Number and Email Id . Valid for 2 Mins");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", merchantResponse);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping("/admin/updateMerchant/verify/otp")
	@ApiOperation(value = "update Merchant Details with OTP verification.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantVerifyOtp(@RequestParam("uuid") String uuid, @RequestParam("otp") int otp,
			@RequestParam("merchantId") String merchantId,
			@RequestBody MerchantUpdateReq dto, @RequestHeader("OTPSessionId") String otpSessionId)
			throws UserException, NoSuchAlgorithmException, ValidationExceptions, JWTException, SessionExpiredException,
			jdk.jshell.spi.ExecutionControl.UserException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		if (StringUtils.isEmpty(otp)) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}

		MerchantCreateResponse merchantres = UserLoginService.merchantUpdateVerifyOtp(merchantId, otp, dto,
				otpSessionId);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("verification success! Merchant Details has been Updated Successfully.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("LoginData", merchantres);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping("/admin/updateMerchant/resend/otp")
	public ResponseEntity<?> updateMerchantResendOtp(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestBody MerchantUpdateReq dto) throws IllegalAccessException,
			NoSuchAlgorithmException, ValidationExceptions, UserException, JWTException, SessionExpiredException,
			jdk.jshell.spi.ExecutionControl.UserException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		OTPConfirmation merchantResponse = UserLoginService.updateMerchant(merchantId, dto);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("OTP has been send to the Specified Mobile Number and Email Id . Valid for 2 Mins");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", merchantResponse);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/api/admin/adminDetails")
	public ResponseEntity<?> adminInfo(@RequestParam("uuid") String uuid) throws UserException, JWTException,
			SessionExpiredException, NoSuchAlgorithmException, ValidationExceptions, IOException {
		// Load file as Resource
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		AdminDetailDto res = pgGatewayAdminService.adminDetails(uuid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("admintDetail", res);
		return ResponseEntity.ok().body(sdto);
	}
	

	
	
	
	@PutMapping(value = "api/payin/updateTransactionStatus")
	@ApiOperation(value = "Update Transaction Status in payin", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateTransactionStatus(	@RequestParam("uuid") String uuid,
			@RequestBody TransactionChangeRequestDto transactionChangeRequestDto)
			throws JsonProcessingException, ValidationExceptions, ParseException, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updateTransactionStatus(transactionChangeRequestDto,uuid));
	}
	
	@GetMapping("payin/getallTransactionChangeRequest")
	public ResponseEntity<?> getallTransactionChangeRequest(@RequestParam("uuid") String uuid)
			throws UserException, JWTException,
			SessionExpiredException, NoSuchAlgorithmException, ValidationExceptions, IOException {
		// Load file as Resource
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(pgGatewayAdminService.getallTransactionChangeRequest());
	}	
	@GetMapping("api/getAllMerchantDetailsAllService")
	public ResponseEntity<?> getAllMerchantDetailsAllService(@RequestParam("uuid") String uuid)
			throws UserException, JWTException,
			SessionExpiredException, NoSuchAlgorithmException, ValidationExceptions, IOException {
		// Load file as Resource
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(pgGatewayAdminService.getAllMerchantDetailsAllService());
	}	
// payIn status update
@PutMapping(value = "/api/admin/payInMerchantBlock")
@ApiOperation(value = "Unique WalletList", authorizations = {
		@Authorization(value = "apiKey") })
public ResponseEntity<?> payInMerchantBlock(
		@RequestParam("uuid") String uuid,
		@RequestParam("status") String status,
		@RequestParam("merchantId") String merchantId)
		throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

	UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

	return ResponseEntity.ok().body(pgGatewayAdminService.payInMerchantBlock(status, merchantId));
}
@Autowired
PaymentMerchantService paymentMerchantService;

@GetMapping("api/getAppIdAndSecretByMerchantDetails")
public ResponseEntity<?> getAppIdAndSecretByMerchantDetails(@RequestParam("uuid") String uuid,@RequestParam("merchantId") String merchantId)
		throws UserException, JWTException,
		SessionExpiredException, NoSuchAlgorithmException, ValidationExceptions, IOException {
	// Load file as Resource
	jwtUserValidator.validatebyJwtAdminDetails(uuid);
	return ResponseEntity.ok().body(paymentMerchantService.getAppIdAndSecretByMerchantDetails(merchantId));
}
}
