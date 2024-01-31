package com.asktech.admin.service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.apache.http.HttpStatus;
//import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.customInterface.IAllMerchantDetailsReport;
import com.asktech.admin.customInterface.IMerchantList;
import com.asktech.admin.customInterface.IMerchantStatus;
import com.asktech.admin.dto.admin.AddMerchantByDistributorResponse;
import com.asktech.admin.dto.admin.AdminDetailDto;
import com.asktech.admin.dto.admin.AllMerchantDetails;
import com.asktech.admin.dto.admin.AllMerchantsAssociatedWithADistributorByDistributorIDResponse;
import com.asktech.admin.dto.admin.AllPgDetailsResponse;
import com.asktech.admin.dto.admin.BusinessAssociateCreateRequest;
import com.asktech.admin.dto.admin.CreateAdminUserRequest;
import com.asktech.admin.dto.admin.CreateDistributorRequest;
import com.asktech.admin.dto.admin.CreatePGDetailsRequest;
import com.asktech.admin.dto.admin.DistributorDetailInformationsResponse;
import com.asktech.admin.dto.admin.DistributorFromDistributorMerchantDetailsResponse;
import com.asktech.admin.dto.admin.DistributorMerchantDetailsInformationResponse;
import com.asktech.admin.dto.admin.DistributorMerchantDetailsResponse;
import com.asktech.admin.dto.admin.DistributorResponse;
import com.asktech.admin.dto.admin.FindAllResponseAddMerchantByDistributorRequest;
import com.asktech.admin.dto.admin.FindAllResponseRechargeRequestDetails;
import com.asktech.admin.dto.admin.MerchantCreateRequest;
import com.asktech.admin.dto.admin.MerchantCreateResponse;
import com.asktech.admin.dto.admin.MerchantDashboardDet;
import com.asktech.admin.dto.admin.MerchantDetailsStatusUpdateResponse;
import com.asktech.admin.dto.admin.MerchantKycDetailsResponse;
import com.asktech.admin.dto.admin.MerchantPGServiceAssociationResponse;
import com.asktech.admin.dto.admin.MerchantPgdetails;
import com.asktech.admin.dto.admin.MerchantServiceDetails;
import com.asktech.admin.dto.admin.Pgdetails;
import com.asktech.admin.dto.admin.Pgservices;
import com.asktech.admin.dto.admin.ProcessSettlement;
import com.asktech.admin.dto.admin.ProcessSettlementRequest;
import com.asktech.admin.dto.admin.ProcessSettlementResponse;
import com.asktech.admin.dto.admin.ServiceDetails;
import com.asktech.admin.dto.admin.TransactionChangeResponceListDto;
import com.asktech.admin.dto.admin.UpdateDistributorDetails;
import com.asktech.admin.dto.admin.UpdateDistributorDetailsResponse;
import com.asktech.admin.dto.admin.UpdateDistributorMerchantAssociationDetails;
import com.asktech.admin.dto.admin.UpdatePGDetailsRequest;
import com.asktech.admin.dto.admin.UpdateTransactionDetailsRequestDto;
import com.asktech.admin.dto.admin.UpdatedResponseOfRechargeRequest;
import com.asktech.admin.dto.admin.UserSessionDto;
import com.asktech.admin.dto.admin.masterList.RequestMasterBankListAssociation;
import com.asktech.admin.dto.admin.masterList.RequestMasterBankListUpdate;
import com.asktech.admin.dto.admin.masterList.RequestMasterWalletListAssociation;
import com.asktech.admin.dto.admin.masterList.RequestMasterWalletListUpdate;
import com.asktech.admin.dto.admin.masterList.ResponseMasterBankListAssociation;
import com.asktech.admin.dto.admin.masterList.ResponseMasterWalletListAssociation;
import com.asktech.admin.dto.login.LoginRequestDto;
import com.asktech.admin.dto.login.LoginResponseDto;
import com.asktech.admin.dto.login.LogoutRequestDto;
import com.asktech.admin.dto.merchant.MerchantKycDocRes;
import com.asktech.admin.dto.merchant.MerchantPgDetailRes;
import com.asktech.admin.dto.merchant.MerchantRefundDto;
import com.asktech.admin.dto.merchant.SettlementDetailsDto;
import com.asktech.admin.dto.merchant.TransactionDetailsDto;
import com.asktech.admin.dto.merchant.UploadFileResponse;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeRequestDto;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeResponce;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeResponceList;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.ApprovalStatus;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.KycStatus;
import com.asktech.admin.enums.MerchantWalletStatus;
import com.asktech.admin.enums.OtpStatus;
import com.asktech.admin.enums.PGSERVICEPRIORITY;
import com.asktech.admin.enums.PGServices;
import com.asktech.admin.enums.PayoutUserStatus;
import com.asktech.admin.enums.RechargeStatus;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.enums.UserStatus;
import com.asktech.admin.enums.UserTypes;
import com.asktech.admin.exception.FileStorageException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.mail.MailIntegration;
import com.asktech.admin.model.AddMerchantByDistributorRequest;
import com.asktech.admin.model.BusinessAssociate;
import com.asktech.admin.model.BusinessAssociateCommissionDetails;
import com.asktech.admin.model.DistributorDetails;
import com.asktech.admin.model.DistributorMerchantDetails;
import com.asktech.admin.model.FileLoading;
import com.asktech.admin.model.MasterBankList;
import com.asktech.admin.model.MasterWalletList;
import com.asktech.admin.model.MerchantBalanceSheet;
import com.asktech.admin.model.MerchantBankDetails;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantKycDetails;
import com.asktech.admin.model.MerchantPGDetails;
import com.asktech.admin.model.MerchantPGServices;
import com.asktech.admin.model.PGConfigurationDetails;
import com.asktech.admin.model.PGServiceDetails;
import com.asktech.admin.model.PGServiceThresoldCalculation;
import com.asktech.admin.model.RechargeRequestDetails;
import com.asktech.admin.model.RefundDetails;
import com.asktech.admin.model.ServiceWisePaymentThresold;
import com.asktech.admin.model.TicketComplaintDetails;
import com.asktech.admin.model.TransactionChangeRequest;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.model.UserDetails;
import com.asktech.admin.model.UserOTPDetails;
import com.asktech.admin.model.UserSession;
import com.asktech.admin.model.payout.PayoutApiUserDetails;
import com.asktech.admin.model.seam.BankList;
import com.asktech.admin.model.seam.WalletList;
import com.asktech.admin.repository.AddMerchantByDistributorRequestRepository;
import com.asktech.admin.repository.BusinessAssociateCommissionDetailsRepo;
import com.asktech.admin.repository.BusinessAssociateRepository;
import com.asktech.admin.repository.CardPaymentDetailsRepository;
import com.asktech.admin.repository.CommissionStructureRepository;
import com.asktech.admin.repository.DistributorDetailsRepository;
import com.asktech.admin.repository.DistributorMerchantAssociationDetailsRepository;
import com.asktech.admin.repository.FileUploadRepo;
import com.asktech.admin.repository.MasterBankListRepository;
import com.asktech.admin.repository.MasterWalletListRepository;
import com.asktech.admin.repository.MerchantBalanceSheetRepository;
import com.asktech.admin.repository.MerchantBankDetailsRepository;
import com.asktech.admin.repository.MerchantDashBoardBalanceRepository;
import com.asktech.admin.repository.MerchantDetailsAddRepository;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.MerchantKycDetailsRepository;
import com.asktech.admin.repository.MerchantPGDetailsRepository;
import com.asktech.admin.repository.MerchantPGServicesRepository;
import com.asktech.admin.repository.PGConfigurationDetailsRepository;
import com.asktech.admin.repository.PGServiceDetailsRepository;
import com.asktech.admin.repository.PGServiceThresoldCalculationRepository;
import com.asktech.admin.repository.RechargeRequestDetailsRepository;
import com.asktech.admin.repository.RefundDetailsRepository;
import com.asktech.admin.repository.ServiceWisePaymentThresoldRepository;
import com.asktech.admin.repository.TicketComplaintDetailsRepository;
import com.asktech.admin.repository.TransactionChangeRequestRepo;
import com.asktech.admin.repository.TransactionDetailsRepository;
import com.asktech.admin.repository.UserAdminDetailsRepository;
import com.asktech.admin.repository.UserDetailsRepository;
import com.asktech.admin.repository.UserOTPDetailsRepository;
import com.asktech.admin.repository.UserSessionRepo;
import com.asktech.admin.repository.payout.PayoutApiUserDetailsRepo;
import com.asktech.admin.repository.seam.BankListRepository;
import com.asktech.admin.repository.seam.WalletListRepository;
import com.asktech.admin.security.Encryption;
import com.asktech.admin.service.payout.PayoutMerchant;
import com.asktech.admin.util.FileUpload;
import com.asktech.admin.util.GeneralUtils;
import com.asktech.admin.util.SecurityUtils;
import com.asktech.admin.util.Utility;
import com.asktech.admin.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PGGatewayAdminService implements ErrorValues {

	@Value("${file.uploaddir}")
	String fileStorage;
	@Autowired
	FileUploadRepo fileUploadRepo;
	@Autowired
	PayoutApiUserDetailsRepo payoutApiUserDetailsRepo;
	@Autowired
	private FileUpload fileStorageService;
	@Autowired
	TicketComplaintDetailsRepository ticketComplaintDetailsRepository;
	@Autowired
	CardPaymentDetailsRepository cardPaymentDetailsRepository;
	@Autowired
	MerchantKycDetailsRepository merchantKycDetailsRepository;
	@Autowired
	ServiceWisePaymentThresoldRepository serviceWisePaymentThresoldRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantBalanceSheetRepository merchantBalanceSheetRepository;
	@Autowired
	MerchantDashBoardBalanceRepository merchantDashBoardBalanceRepository;
	@Autowired
	MerchantBankDetailsRepository merchantBankDetailsRepository;
	@Autowired
	MerchantPGDetailsRepository merchantPGDetailsRepository;
	@Autowired
	MerchantPGServicesRepository merchantPGServicesRepository;
	@Autowired
	CommissionStructureRepository commissionStructureRepository;
	@Autowired
	UserDetailsRepository userDetailsRepository;
	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;
	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
	@Autowired
	PGServiceDetailsRepository pgServiceDetailsRepository;
	@Autowired
	MerchantDetailsAddRepository merchantDetailsAddRepository;
	@Autowired
	UserOTPDetailsRepository userOTPDetailsRepository;
	@Autowired
	BusinessAssociateRepository businessAssociateRepository;
	@Autowired
	BusinessAssociateCommissionDetailsRepo businessAssociateCommissionDetailsRepo;
	@Autowired
	RefundDetailsRepository refundDetailsRepository;
	@Autowired
	PGServiceThresoldCalculationRepository pgServiceThresoldCalculationRepository;
	@Autowired
	WalletListRepository walletListRepository;
	@Autowired
	BankListRepository bankListRepository;
	@Autowired
	MasterBankListRepository masterBankListRepository;
	@Autowired
	MasterWalletListRepository masterWalletListRepository;
	@Autowired
	PayoutMerchant payoutMerchant;

	@Autowired
	MailIntegration sendMail;
	@Autowired
	UserSessionRepo sessionRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private DistributorDetailsRepository distributorDetailsRepository;
	@Autowired
	private DistributorMerchantAssociationDetailsRepository distributorMerchantAssociationDetailsRepository;

	@Autowired
	private AddMerchantByDistributorRequestRepository addMerchantByDistributorRequestRepository;
	@Autowired
	private RechargeRequestDetailsRepository rechargeRequestDetailsRepository;

	ObjectMapper mapper = new ObjectMapper();

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminService.class);

	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;

	@Value("${otpExpiryTime}")
	long otpExpiryTime;
	@Value("${apiEndPoint}")
	String apiEndPoint;

	long EXPIRATION_TIME = 60 * 24;

	@SuppressWarnings("unlikely-arg-type")
	public LoginResponseDto getAdminLogin(LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {

		if (dto.getPassword().isEmpty()) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		UserAdminDetails user = userAdminDetailsRepository.findByUserId(dto.getUserNameOrEmail());
		if (user == null) {

			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);

		}

		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}
		// Encryption.getDecryptedPasswordKMS(user.getPassword());
		// logger.info("Password :: " +
		// Encryption.getDecryptedPasswordKMS(user.getPassword()));
		if (user.getPassword() != null) {
			if (dto.getPassword() == null) {
				throw new ValidationExceptions(PASSWORD_CANT_BE_BLANK,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);

			} else if (!dto.getPassword().equals(Encryption.getDecryptedPasswordKMS(user.getPassword()))) {
				// logger.info("user.getPassword() :: "+user.getPassword());
				// logger.info("Encryption.getEncryptedPassword(dto.getPassword())) ::
				// "+Encryption.getEncryptedPassword(dto.getPassword()));
				// logger.info("Encryption.getEncryptedPassword(dto.getPassword())) ::
				// "+Encryption.getDecryptedPassword(user.getPassword()));
				throw new ValidationExceptions(PASSWORD_MISMATCH,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
			}
		} else {
			throw new ValidationExceptions(PASSWORD_MISMATCH, FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
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
		session.setUserAdmin(user);
		String hash = Encryption.getSHA256Hash(UUID.randomUUID().toString() + user.getUserId());
		session.setSessionToken(hash);
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		session.setSessionExpiryDate(date);
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		session.setIdealSessionExpiry(idealDate);
		user.setUserSession(session);
		userAdminDetailsRepository.save(user);

		LoginResponseDto loginResponseDto = new LoginResponseDto();

		loginResponseDto.setUuid(user.getUuid());
		loginResponseDto.setEmail(user.getEmailId());
		loginResponseDto.setPhoneNumber(user.getPhoneNumber());
		loginResponseDto.setSessionStatus(user.getUserSession().getSessionStatus());
		loginResponseDto.setSessionToken(user.getUserSession().getSessionToken());
		loginResponseDto.setSessionExpiryDate(user.getUserSession().getSessionExpiryDate());
		loginResponseDto.setUserType(user.getUserType());

		return loginResponseDto;
	}

	public void passwordChange(String userNameOrEmailId, String password) throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		UserAdminDetails user = userAdminDetailsRepository.findByEmailId(userNameOrEmailId);

		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPasswordKMS(password));

		userAdminDetailsRepository.save(user);

	}

	public void forgotPassword(String userNameOrEmailId) throws ValidationExceptions {
		UserAdminDetails user = userAdminDetailsRepository.findByEmailId(userNameOrEmailId);
		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		int otp = (new Random()).nextInt(90000000) + 10000000;

		String message = "Hi " + user.getUserName() + " Your forgot password OTP for change the password is " + otp;

		UserOTPDetails userOTPDetails = userOTPDetailsRepository.findByUuid(user.getUuid());
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		if (userOTPDetails == null) {
			userOTPDetails = new UserOTPDetails();
			userOTPDetails.setEmailId(user.getEmailId());
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

		sendMail.sendMailForgotPassword(user.getEmailId(), message, "Forget Password : " + user.getUserName());
	}

	public void forgotPasswordChange(String userNameOrEmailId, String password, String otp)
			throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		UserAdminDetails user = userAdminDetailsRepository.findByEmailId(userNameOrEmailId);
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
		userAdminDetailsRepository.save(user);
	}

	public static boolean hasBlankVariables(Object obj) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			// Danger!
			String str = (String) field.get(obj);
			if (StringUtils.isEmpty(str)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * public CommissionStructure createCommissionstructure(String merchantPGNme,
	 * String merchantService, int pgAmount, String pgCommissionType, int askAmount,
	 * String askCommissionType, String uuid) throws ValidationExceptions {
	 * 
	 * MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);
	 * 
	 * MerchantPGDetails merchantPGDetails =
	 * merchantPGDetailsRepository.findByMerchantPGName(merchantPGNme);
	 * 
	 * if (merchantDetails == null) { throw new
	 * ValidationExceptions(MERCHNT_NOT_EXISTIS,
	 * FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND); } if (merchantService ==
	 * null) { throw new ValidationExceptions(MERCHANT_SERVICE_TYPE,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); } if (pgCommissionType
	 * == null) { throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); }
	 * 
	 * if (pgAmount <= 0) { throw new ValidationExceptions(MERCHANT_COMM_AMOUNT,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); } if (askCommissionType
	 * == null) { throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); }
	 * 
	 * if (askAmount <= 0) { throw new ValidationExceptions(MERCHANT_COMM_AMOUNT,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); } if (merchantPGDetails
	 * == null) { throw new ValidationExceptions(PG_NOT_PRESENT,
	 * FormValidationExceptionEnums.PG_VLIDATION_ERROR); }
	 * 
	 * CommissionStructure commissionStructure =
	 * commissionStructureRepository.findByMerchantIdAndPgIdAndServiceType(
	 * merchantDetails.getMerchantID(), String.valueOf(merchantPGDetails.getId()),
	 * merchantService);
	 * 
	 * if (commissionStructure != null) { throw new
	 * ValidationExceptions(MERCHANT_COMMISSION_EXISTS,
	 * FormValidationExceptionEnums.DUPLICATE_COMMISSON_FOR_MERCHANT); }
	 * logger.info("Before CommissionStructure get repo "); commissionStructure =
	 * new CommissionStructure();
	 * 
	 * commissionStructure.setPgAmount(pgAmount);
	 * commissionStructure.setPgCommissionType(pgCommissionType);
	 * commissionStructure.setAskAmount(askAmount);
	 * commissionStructure.setAskCommissionType(askCommissionType);
	 * commissionStructure.setMerchantId(merchantDetails.getMerchantID());
	 * commissionStructure.setPgId(String.valueOf(merchantPGDetails.getId()));
	 * commissionStructure.setServiceType(merchantService);
	 * commissionStructure.setStatus(ApprovalStatus.NEW.toString());
	 * 
	 * commissionStructureRepository.save(commissionStructure);
	 * 
	 * return commissionStructure; }
	 */
	/*
	 * public CommissionStructure createCommissionstructureAskTech(String
	 * merchantPGNme, String merchantService, int pgAmount, String pgCommissionType,
	 * int askAmount, String askCommissionType) throws ValidationExceptions {
	 * 
	 * MerchantPGDetails merchantPGDetails =
	 * merchantPGDetailsRepository.findByMerchantPGName(merchantPGNme);
	 * 
	 * if (merchantService == null) { throw new
	 * ValidationExceptions(MERCHANT_SERVICE_TYPE,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); }
	 * 
	 * if (askCommissionType == null) { throw new
	 * ValidationExceptions(MERCHANT_COMMISSION_TYPE,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); }
	 * 
	 * if (askAmount <= 0) { throw new ValidationExceptions(MERCHANT_COMM_AMOUNT,
	 * FormValidationExceptionEnums.FORM_VALIDATION_FILED); } if (merchantPGDetails
	 * == null) { throw new ValidationExceptions(PG_NOT_PRESENT,
	 * FormValidationExceptionEnums.PG_VLIDATION_ERROR); }
	 * 
	 * CommissionStructure commissionStructure = commissionStructureRepository
	 * .checkCommissionAskTech(String.valueOf(merchantPGDetails.getId()),
	 * merchantService);
	 * 
	 * if (commissionStructure != null) { throw new
	 * ValidationExceptions(MERCHANT_COMMISSION_EXISTS,
	 * FormValidationExceptionEnums.DUPLICATE_COMMISSON_FOR_MERCHANT); }
	 * logger.info("Before CommissionStructure get repo "); commissionStructure =
	 * new CommissionStructure();
	 * 
	 * commissionStructure.setAskAmount(askAmount);
	 * commissionStructure.setAskCommissionType(askCommissionType);
	 * commissionStructure.setPgAmount(pgAmount);
	 * commissionStructure.setPgCommissionType(pgCommissionType);
	 * commissionStructure.setPgId(String.valueOf(merchantPGDetails.getId()));
	 * commissionStructure.setServiceType(merchantService);
	 * commissionStructure.setStatus(ApprovalStatus.NEW.toString());
	 * 
	 * commissionStructureRepository.save(commissionStructure);
	 * 
	 * return commissionStructure; }
	 */
	public List<UserDetails> getUserDetails(String custEmailorPhone) throws ValidationExceptions {

		if (custEmailorPhone == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}

		List<UserDetails> userDetails = userDetailsRepository.findAllByEmailIdOrPhoneNumber(custEmailorPhone,
				custEmailorPhone);

		if (userDetails == null) {
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.USER_NOT_FOUND);
		}

		return userDetails;
	}

	public UserAdminDetails createAdminUser(CreateAdminUserRequest createAdminUserRequest) throws ValidationExceptions {

		UserAdminDetails userAdmin = userAdminDetailsRepository.findByEmailId(createAdminUserRequest.getEmailId());

		if (userAdmin != null) {
			throw new ValidationExceptions(ADMIN_USER_EXISTS, FormValidationExceptionEnums.EMAIL_ALREADY_EXISTS);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository
				.findByMerchantEmail(createAdminUserRequest.getEmailId());
		if (merchantDetails != null) {
			throw new ValidationExceptions(ADMIN_USER_EXISTS, FormValidationExceptionEnums.EMAIL_ALREADY_EXISTS);
		}

		UserAdminDetails userAdminDetails = new UserAdminDetails();
		userAdminDetails.setAddress1(createAdminUserRequest.getAddress1());
		userAdminDetails.setAddress2(createAdminUserRequest.getAddress2());
		userAdminDetails.setAddress3(createAdminUserRequest.getAddress3());
		userAdminDetails.setCity(createAdminUserRequest.getCity());
		userAdminDetails.setCompantName(createAdminUserRequest.getCompantName());
		userAdminDetails.setCountry(createAdminUserRequest.getCountry());
		userAdminDetails.setEmailId(createAdminUserRequest.getEmailId());
		userAdminDetails.setPhoneNumber(createAdminUserRequest.getPhoneNumber());
		userAdminDetails.setPincode(createAdminUserRequest.getPincode());
		userAdminDetails.setUserId(createAdminUserRequest.getEmailId());
		userAdminDetails.setUserName(createAdminUserRequest.getUserName());
		userAdminDetails.setUuid(UUID.randomUUID().toString());
		userAdminDetails.setUserType(UserTypes.ADMIN.toString());
		userAdminDetails.setUserStatus(UserStatus.ACTIVE.toString());
		userAdminDetails.setPassword(Encryption.getEncryptedPasswordKMS(Encryption.generateRandomPassword(8)));

		UserAdminDetails userAdminDetailsResponse = userAdminDetailsRepository.save(userAdminDetails);

		userAdminDetailsResponse
				.setPassword(Encryption.getDecryptedPasswordKMS(userAdminDetailsResponse.getPassword()));
		return userAdminDetailsResponse;

	}

	/** @author Modified By abhimanyu start **/

	// create PG
	public SuccessResponseDto createPg(CreatePGDetailsRequest createPGDetailsRequest, String userName)
			throws ValidationExceptions {

		if (userName.isEmpty() || userName == null) {
			throw new ValidationExceptions(USERNAME_EMPTY, FormValidationExceptionEnums.USERNAME_EMPTY);

		}
		if (createPGDetailsRequest.getPgName().isEmpty() || createPGDetailsRequest.getPgName() == null) {
			throw new ValidationExceptions(PGNAME_EMPTY_OR_NULL, FormValidationExceptionEnums.PGNAME_EMPTY_OR_NULL);

		}

		/*
		 * if(createPGDetailsRequest.getPgAppId().isEmpty() ||
		 * createPGDetailsRequest.getPgAppId() == null) { throw new
		 * ValidationExceptions(PGAppId_EMPTY_OR_NULL,
		 * FormValidationExceptionEnums.PGAppId_EMPTY_OR_NULL);
		 * 
		 * } if (createPGDetailsRequest.getPgSecretKey().isEmpty() ||
		 * createPGDetailsRequest.getPgSecretKey() == null) { throw new
		 * ValidationExceptions(PGSecretKey_EMPTY_OR_NULL,
		 * FormValidationExceptionEnums.PGSecretKey_EMPTY_OR_NULL);
		 * 
		 * } if (createPGDetailsRequest.getPgSaltKey().isEmpty() ||
		 * createPGDetailsRequest.getPgSaltKey() == null) { throw new
		 * ValidationExceptions(PGSaltKey_EMPTY_OR_NULL,
		 * FormValidationExceptionEnums.PGSaltKey_EMPTY_OR_NULL);
		 * 
		 * }
		 */

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgName(createPGDetailsRequest.getPgName().toUpperCase());

		logger.info("pgConfigurationDetails ::: 22 : : " + createPGDetailsRequest.getPgName());
		if (pgConfigurationDetails != null) {
			// Making sure that PG must be unique
			throw new ValidationExceptions(PG_ALREADY_CREATED, FormValidationExceptionEnums.PG_ALREADYCREATED);

		} else {
			logger.info("pgConfigurationDetails ::: 23 : : " + createPGDetailsRequest.getPgName());
			// A new PG object will be created now
			pgConfigurationDetails = new PGConfigurationDetails();// Initializing a new empty Object
			pgConfigurationDetails.setCreatedBy(userName);
			pgConfigurationDetails.setUpdatedBy(userName);
			logger.info("pgConfigurationDetails 12::: 23 : : " + createPGDetailsRequest.getPgName());
			System.out.println("23 bool " + createPGDetailsRequest.getPgSecretId());
			if (createPGDetailsRequest.getPgSecretId() != null && createPGDetailsRequest.getPgSecretId().isEmpty()) {
				String pgUuid = null;
				logger.info("pgConfigurationDetails ::: 23.5 : : " + createPGDetailsRequest.getPgName());
				pgUuid = pgConfigurationDetailsRepository.findByPgSecretId(createPGDetailsRequest.getPgSecretId());
				if (pgUuid != null) {
					throw new ValidationExceptions(PG_SECRET_ID_ALREADY_EXISTS,
							FormValidationExceptionEnums.PG_SECRET_ID_ALREADY_EXISTS);
				}
				logger.info("pgConfigurationDetails ::: 24 : : " + createPGDetailsRequest.getPgName());
				pgConfigurationDetails.setPgSecretId(createPGDetailsRequest.getPgSecretId());
			}
			logger.info("pgConfigurationDetails ::: 25 : : " + createPGDetailsRequest.getPgName());
			/*
			 * else {
			 * 
			 * throw new ValidationExceptions(INPUT_EMPTY_NULL,
			 * FormValidationExceptionEnums.INPUT_EMPTY_NULL);
			 * 
			 * }
			 */

			if (createPGDetailsRequest.getPgApi() != null && createPGDetailsRequest.getPgApi().isEmpty()) {
				logger.info("pgConfigurationDetails ::: 25.5 : : " + createPGDetailsRequest.getPgName());
				String pgUuid = null;
				pgUuid = pgConfigurationDetailsRepository.findByPgApi(createPGDetailsRequest.getPgApi());
				if (pgUuid != null) {
					throw new ValidationExceptions(PG_API_EXISTS, FormValidationExceptionEnums.PG_API_EXISTS);
				}
				pgConfigurationDetails.setPgApi(createPGDetailsRequest.getPgApi());
			} /*
				 * else {
				 * 
				 * throw new ValidationExceptions(INPUT_EMPTY_NULL,
				 * FormValidationExceptionEnums.INPUT_EMPTY_NULL);
				 * 
				 * }
				 */
			logger.info("pgConfigurationDetails ::: 26 : : " + createPGDetailsRequest.getPgName());
			if (!createPGDetailsRequest.getPgAppId().isEmpty() && createPGDetailsRequest.getPgAppId() != null) {

				String pgUuid = null;
				pgUuid = pgConfigurationDetailsRepository.findByPgAppId(createPGDetailsRequest.getPgAppId());
				if (pgUuid != null) {
					throw new ValidationExceptions(PG_APP_ID_DUPLICATE,
							FormValidationExceptionEnums.PG_APP_ID_DUPLICATE);
				}
				pgConfigurationDetails.setPgAppId(createPGDetailsRequest.getPgAppId());
			}
			logger.info("pgConfigurationDetails ::: 27 : : " + createPGDetailsRequest.getPgName());
			if (!createPGDetailsRequest.getPgSecretKey().isEmpty() && createPGDetailsRequest.getPgSecretKey() != null) {

				if (!Validator.isValidSecret(createPGDetailsRequest.getPgSecretKey())) {
					throw new ValidationExceptions(PG_SECRET_KEY_NOT_VALID,
							FormValidationExceptionEnums.PG_SECRET_KEY_NOT_VALID);
				}
				String pgUuid = null;
				logger.info("pgConfigurationDetails ::: 28 : : " + createPGDetailsRequest.getPgName());
				pgUuid = pgConfigurationDetailsRepository.findByPgSecret(
						Encryption.encryptCardNumberOrExpOrCvvKMS(createPGDetailsRequest.getPgSecretKey()));
				// here checking secret
				if (pgUuid != null) {
					throw new ValidationExceptions(PG_SECRET_KEY_DUPLICATE,
							FormValidationExceptionEnums.PG_SECRET_KEY_DUPLICATE);
				}
				pgConfigurationDetails.setPgSecret(
						Encryption.encryptCardNumberOrExpOrCvvKMS(createPGDetailsRequest.getPgSecretKey()));
				// pgConfigurationDetails.setPgSecret(createPGDetailsRequest.getPgSecretKey());
				logger.info("pgConfigurationDetails ::: 29 : : " + createPGDetailsRequest.getPgName());
			}
			if (!createPGDetailsRequest.getPgSaltKey().isEmpty() && createPGDetailsRequest.getPgSaltKey() != null) {
				if (!Validator.isValidSecret(createPGDetailsRequest.getPgSaltKey())) {
					throw new ValidationExceptions(PG_SALT_KEY_NOT_VALID,
							FormValidationExceptionEnums.PG_SALT_KEY_NOT_VALID);
				}
				logger.info("pgConfigurationDetails ::: 30 : : " + createPGDetailsRequest.getPgName());
				String pgUuid = null;
				pgUuid = pgConfigurationDetailsRepository.findByPgSaltKey(createPGDetailsRequest.getPgSaltKey());
				if (pgUuid != null) {
					throw new ValidationExceptions(PG_SALT_KEY_DUPLICATE,
							FormValidationExceptionEnums.PG_SALT_KEY_DUPLICATE);
				}

				pgConfigurationDetails.setPgSaltKey(createPGDetailsRequest.getPgSaltKey());
			}
			logger.info("pgConfigurationDetails ::: 31 : : " + createPGDetailsRequest.getPgName());
			// Server side Impl
			if (createPGDetailsRequest.getPgName() != null) {

				pgConfigurationDetails.setPgName(createPGDetailsRequest.getPgName().toUpperCase());
			} else {
				throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);

			}
			pgConfigurationDetails.setPgUuid(UUID.randomUUID().toString());
			pgConfigurationDetails.setStatus(UserStatus.PENDING.toString());
			logger.info("pgConfigurationDetails ::: 32 : : " + createPGDetailsRequest.getPgName());
			if (!createPGDetailsRequest.getPgMerchantLink().isEmpty()) {
				if (!Validator.isValidWebUrl(createPGDetailsRequest.getPgMerchantLink()))
					throw new ValidationExceptions(INVALID_URL, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
				pgConfigurationDetails.setPgMerchantLink(createPGDetailsRequest.getPgMerchantLink());
			}
			logger.info("pgConfigurationDetails ::: 33 : : " + createPGDetailsRequest.getPgName());
			pgConfigurationDetails.setPgAddInfo1(createPGDetailsRequest.getPgAddInfo1());
			pgConfigurationDetails.setPgAddInfo2(createPGDetailsRequest.getPgAddInfo2());
			pgConfigurationDetails.setPgAddInfo3(createPGDetailsRequest.getPgAddInfo3());

			PGConfigurationDetails pGConfigurationDetails = pgConfigurationDetailsRepository
					.save(pgConfigurationDetails);
			logger.info("pgConfigurationDetails ::: 34 : : " + createPGDetailsRequest.getPgName());
			SuccessResponseDto sdto = new SuccessResponseDto();
			sdto.getMsg().add("PG create request Processed Successfully !");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.setStatus(HttpStatus.SC_CREATED);
			sdto.getExtraData().put("pGConfigurationDetails", pGConfigurationDetails);
			return sdto;
		}

	}

	// api/updatePgConfigurationDetails
	public SuccessResponseDto updatePg(UpdatePGDetailsRequest updatePGDetailsRequest, String userName)
			throws ValidationExceptions {
		String pguu = updatePGDetailsRequest.getPgUuid();
		// PGConfigurationDetails pgConfigurationDetails =
		// pgConfigurationDetailsRepository.findByPgName(updatePGDetailsRequest.getPgName());
		logger.info("pgUUid ::: 00 : :" + updatePGDetailsRequest.getPgUuid());
		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pguu);
		try {
			logger.info(Utility.convertDTO2JsonString(pgConfigurationDetails));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String pgUuid = null;
		logger.info("pgUUid ::: 1 :: " + pgConfigurationDetails.getPgName());

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);
		} else {

			pgConfigurationDetails.setCreatedBy(userName);
			pgConfigurationDetails.setUpdated(new Date());
			pgConfigurationDetails.setUpdatedBy(userName);

			if (updatePGDetailsRequest.getPgSecretId() != null && !updatePGDetailsRequest.getPgSecretId().isEmpty()) {
				String pgUuid1 = null;
				pgUuid1 = pgConfigurationDetailsRepository.findByPgSecretId(updatePGDetailsRequest.getPgSecretId());
				logger.info("pgUuid ::: 2 : :", pgUuid != null);
				if (pgUuid1 != null) {
					throw new ValidationExceptions(PG_SECRET_ID_ALREADY_EXISTS,
							FormValidationExceptionEnums.PG_SECRET_ID_ALREADY_EXISTS);
				}
				pgConfigurationDetails.setPgSecretId(
						Encryption.encryptCardNumberOrExpOrCvvKMS(updatePGDetailsRequest.getPgSecretId()));
				pgConfigurationDetails.setPgSecretId(updatePGDetailsRequest.getPgSecretId());

			}
			if (updatePGDetailsRequest.getPgApi() != null && !updatePGDetailsRequest.getPgApi().isEmpty()) {
				String pgUuid1 = null;
				pgUuid1 = pgConfigurationDetailsRepository.findByPgApi(updatePGDetailsRequest.getPgApi());

				logger.info("findByPgApi ::: 3 :");

				if (pgUuid1 != null) {
					throw new ValidationExceptions(PG_API_EXISTS, FormValidationExceptionEnums.PG_API_EXISTS);
				}
				pgConfigurationDetails.setPgApi(updatePGDetailsRequest.getPgApi());
			}
			if (!updatePGDetailsRequest.getPgMerchantLink().isEmpty()
					&& updatePGDetailsRequest.getPgMerchantLink() != null) {

				if (!Validator.isValidWebUrl(updatePGDetailsRequest.getPgMerchantLink())) {
					throw new ValidationExceptions(INVALID_URL, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
				}
				pgConfigurationDetails.setPgMerchantLink(updatePGDetailsRequest.getPgMerchantLink());
			}

			if (!updatePGDetailsRequest.getPgAppId().isEmpty() && updatePGDetailsRequest.getPgAppId() != null) {

				pgUuid = pgConfigurationDetailsRepository.findByPgAppId(updatePGDetailsRequest.getPgAppId());
				logger.info("findByPgAppId ::: 4 : :", pgUuid != null);

				if (pgUuid != null) {

					throw new ValidationExceptions(PG_APP_ID_DUPLICATE,
							FormValidationExceptionEnums.PG_APP_ID_DUPLICATE);
				}
				pgConfigurationDetails.setPgAppId(updatePGDetailsRequest.getPgAppId());

			}
			logger.info("findByPgApi ::: 4.5 :");
			/*
			 * @PutMapping("api/updatePgConfigurationDetails") Request by raguhu sir , do
			 * not change PG Name , it now disabled in server-side if
			 * (!updatePGDetailsRequest.getPgName().isEmpty() && updatePGDetailsRequest !=
			 * null) {
			 * 
			 * PGConfigurationDetails foundDuplicatePGConfigurationDetails =
			 * pgConfigurationDetailsRepository.findByPgName(updatePGDetailsRequest.
			 * getPgName()); if (foundDuplicatePGConfigurationDetails != null) { // Making
			 * sure that PG must be unique, duplicate name for PG not allowed throw new
			 * ValidationExceptions(PG_NAME_ALREADY_CREATED,
			 * FormValidationExceptionEnums.PG_NAME_ALREADY_CREATED);
			 * 
			 * }
			 * 
			 * pgConfigurationDetails.setPgName(updatePGDetailsRequest.getPgName().
			 * toUpperCase());// toUpperCase }
			 */

			if (!updatePGDetailsRequest.getPgDailyLimit().isEmpty()
					&& updatePGDetailsRequest.getPgDailyLimit() != null) {
				pgConfigurationDetails
						.setPgDailyLimit(Utility.convertIndianRupeeToPaise(updatePGDetailsRequest.getPgDailyLimit()));// PGdailylimit-updated
			}
			logger.info("findByPgApi ::: 4.6 :");
			if (!updatePGDetailsRequest.getPgSecretKey().isEmpty() && updatePGDetailsRequest.getPgSecretKey() != null) {

				// if (!Validator.isValidAlphaNumber(updatePGDetailsRequest.getPgSecretKey())) {
				// throw new ValidationExceptions(PG_SECRET_KEY_NOT_VALID,
				// FormValidationExceptionEnums.PG_SECRET_KEY_NOT_VALID);
				// }
				logger.info("findByPgApi ::: 4.7 :");
				// pgUuid =
				// pgConfigurationDetailsRepository.findByPgSecretKey(Encryption.encryptCardNumberOrExpOrCvvKMS(updatePGDetailsRequest.getPgSecretKey()));
				logger.info("findByPgSecretKey ::: 5 : :");

				// if (pgUuid != null) {

				// throw new ValidationExceptions(PG_SECRET_KEY_DUPLICATE,
				// FormValidationExceptionEnums.PG_SECRET_KEY_DUPLICATE);
				// }

				pgConfigurationDetails.setPgSecret(
						Encryption.encryptCardNumberOrExpOrCvvKMS(updatePGDetailsRequest.getPgSecretKey()));

			}
			logger.info("findByPgSecretKey ::: 5.5 : :");
			// pgConfigurationDetails.setPgUuid(UUID.randomUUID().toString());
			// pgConfigurationDetails.setStatus(UserStatus.PENDING.toString());// for update
			// status another api is available
			if (!updatePGDetailsRequest.getPgSaltKey().isEmpty() && updatePGDetailsRequest.getPgSaltKey() != null) {
				if (!Validator.isValidAlphaNumber(updatePGDetailsRequest.getPgSaltKey())) {
					throw new ValidationExceptions(PG_SALT_KEY_NOT_VALID,
							FormValidationExceptionEnums.PG_SALT_KEY_NOT_VALID);
				}

				pgUuid = pgConfigurationDetailsRepository.findByPgSaltKey(updatePGDetailsRequest.getPgSaltKey());

				logger.info("findByPgSaltKey ::: 6 : :", pgUuid != null);
				if (pgUuid != null) {

					throw new ValidationExceptions(PG_SALT_KEY_DUPLICATE,
							FormValidationExceptionEnums.PG_SALT_KEY_DUPLICATE);
				}
				pgConfigurationDetails.setPgSaltKey(updatePGDetailsRequest.getPgSaltKey());

			}
			logger.info("findByPgSaltKey ::: 6.5 : :", pgUuid != null);
			if (!updatePGDetailsRequest.getPgAddInfo1().isEmpty() && updatePGDetailsRequest.getPgAddInfo1() != null) {
				pgConfigurationDetails.setPgAddInfo1(updatePGDetailsRequest.getPgAddInfo1());
			}
			if (!updatePGDetailsRequest.getPgAddInfo2().isEmpty() && updatePGDetailsRequest.getPgAddInfo2() != null) {
				pgConfigurationDetails.setPgAddInfo2(updatePGDetailsRequest.getPgAddInfo2());
			}
			if (!updatePGDetailsRequest.getPgAddInfo3().isEmpty() && updatePGDetailsRequest.getPgAddInfo3() != null) {
				pgConfigurationDetails.setPgAddInfo3(updatePGDetailsRequest.getPgAddInfo3());
			}
			logger.info("findByPgSaltKey ::: 6.6: :", pgUuid != null);
			pgConfigurationDetails = pgConfigurationDetailsRepository.save(pgConfigurationDetails);

			logger.info("pgConfigurationDetails ::: 7 : :", pgConfigurationDetails != null);

			List<MerchantPGDetails> listOfMerchantPGDetails = merchantPGDetailsRepository
					.findAllByMerchantPGId(pgConfigurationDetails.getPgUuid());
			logger.info("listOfMerchantPGDetails ::: 8 : :", listOfMerchantPGDetails != null);

			for (MerchantPGDetails merchantPGDetail : listOfMerchantPGDetails) {

				merchantPGDetail.setUpdatedBy(userName);
				merchantPGDetail.setUpdated(new Date());
				merchantPGDetail.setReason("PG Details Changed as per Request");
				if (!updatePGDetailsRequest.getPgSaltKey().isEmpty() && updatePGDetailsRequest.getPgSaltKey() != null) {
					merchantPGDetail.setMerchantPGSaltKey(updatePGDetailsRequest.getPgSaltKey());
				}
				if (!updatePGDetailsRequest.getPgAppId().isEmpty() && updatePGDetailsRequest.getPgAppId() != null) {
					merchantPGDetail.setMerchantPGAppId(updatePGDetailsRequest.getPgAppId());

				}

				/*
				 * if (!updatePGDetailsRequest.getPgName().isEmpty() && updatePGDetailsRequest
				 * != null) {
				 * merchantPGDetail.setMerchantPGName(updatePGDetailsRequest.getPgName()); }
				 */
				if (!updatePGDetailsRequest.getPgSecretKey().isEmpty()
						&& updatePGDetailsRequest.getPgSecretKey() != null) {
					merchantPGDetail.setMerchantPGSecret(
							Encryption.encryptCardNumberOrExpOrCvvKMS(updatePGDetailsRequest.getPgSecretKey()));
				}
				if (!updatePGDetailsRequest.getPgAddInfo1().isEmpty()
						&& updatePGDetailsRequest.getPgAddInfo1() != null) {
					merchantPGDetail.setMerchantPGAdd1(updatePGDetailsRequest.getPgAddInfo1());
				}
				if (!updatePGDetailsRequest.getPgAddInfo2().isEmpty()
						&& updatePGDetailsRequest.getPgAddInfo2() != null) {
					merchantPGDetail.setMerchantPGAdd2(updatePGDetailsRequest.getPgAddInfo2());
				}
				if (!updatePGDetailsRequest.getPgAddInfo3().isEmpty()
						&& updatePGDetailsRequest.getPgAddInfo3() != null) {
					merchantPGDetail.setMerchantPGAdd3(updatePGDetailsRequest.getPgAddInfo3());
				}

				merchantPGDetailsRepository.save(merchantPGDetail);
			}
		} // End of else
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_OK);
		sdto.getExtraData().put("pgConfigurationDetails", pgConfigurationDetails);
		sdto.getExtraData().put("listOfMerchantPGDetails",
				merchantPGDetailsRepository.findAllByMerchantPGId(pgConfigurationDetails.getPgUuid()));

		return sdto;
	}

	// api/admin/updatePGDetails
	public Object updatePGDetails(String uuid, String pgUuid, String statusUpdate) throws ValidationExceptions {

		if (pgUuid.isEmpty() && pgUuid != null && uuid.isEmpty() && uuid != null && statusUpdate.isEmpty()
				&& statusUpdate != null) {

			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}
		// 1.
		// List<PGServiceDetails> updatePGServiceDetails = new ArrayList<>();
		// listOfPGServiceDetails
		logger.info("Input PgId :: " + pgUuid);

		// 2.
		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		System.out.println("updatePGDetails " + "   " + pgConfigurationDetails);

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);
		}

		// 4. updating status of PG [ACTIVE | BLOCKED]
		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			// case-1
			pgConfigurationDetails.setStatus(UserStatus.ACTIVE.toString());
			pgConfigurationDetails.setUpdatedBy(uuid);
			List<MerchantPGDetails> listMerchantPGDetails = merchantPGDetailsRepository.findAllByMerchantPGId(pgUuid);
			for (MerchantPGDetails merchantPGDetails : listMerchantPGDetails) {
				merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
				merchantPGDetailsRepository.save(merchantPGDetails);
			}

		} else {
			// case-2
			pgConfigurationDetails.setStatus(UserStatus.BLOCKED.toString());
			pgConfigurationDetails.setUpdatedBy(uuid);
			List<MerchantPGDetails> listMerchantPGDetails = merchantPGDetailsRepository.findAllByMerchantPGId(pgUuid);

			for (MerchantPGDetails merchantPGDetails : listMerchantPGDetails) {
				merchantPGDetails.setStatus(UserStatus.BLOCKED.toString());
				merchantPGDetailsRepository.save(merchantPGDetails);
			}
		}
		pgConfigurationDetailsRepository.save(pgConfigurationDetails);

		// If case-2 occurred
		// if
		// (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString()))
		// {
		// List<PGServiceDetails> listPGServiceDetails = pgServiceDetailsRepository
		// .findByPgId(String.valueOf(pgConfigurationDetails.getPgUuid()));

		// for (PGServiceDetails pgServiceDetails : listPGServiceDetails) {
		// pgServiceDetails.setStatus(pgConfigurationDetails.getStatus());// BLOCKED
		// STATUS IS BEING SET
		// pgServiceDetails.setUpdatedBy(uuid);
		// updatePGServiceDetails.add(pgServiceDetails);
		// }
		// pgServiceDetailsRepository.saveAll(updatePGServiceDetails);

		// List<MerchantPGDetails> listMerchantPGDetails =
		// merchantPGDetailsRepository.findAllByMerchantPGId(pgUuid);

		// for (MerchantPGDetails merchantPGDetails : listMerchantPGDetails) {

		// // List<MerchantPGServices> listMerchantPGService =
		// merchantPGServicesRepository
		// // .findAllByPgID(String.valueOf(merchantPGDetails.getId()));

		// // List<MerchantPGServices> listMerchantPGService =
		// //
		// merchantPGServicesRepository.findAllByPgID(String.valueOf(merchantPGDetails.getMerchantPGId()));

		// // for (MerchantPGServices merchantPGService : listMerchantPGService) {
		// // merchantPGService.setStatus(statusUpdate);
		// // merchantPGServicesRepository.save(merchantPGService);
		// // }

		// merchantPGDetails.setStatus(statusUpdate);
		// merchantPGDetailsRepository.save(merchantPGDetails);
		// }
		// }

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_OK);
		sdto.getExtraData().put("pgDetail", pgConfigurationDetails);
		return sdto;
	}

	@Transactional
	public UpdatedResponseOfRechargeRequest updateStatusOfRechargeRequestForMerchantByDistributorToAdmin(String uuid,
			String distributorID, String merchantID, String rechargeRequestUuid, String approval, String status)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		RechargeRequestDetails rechargeRequestDetails = null;
		UpdatedResponseOfRechargeRequest updatedResponseOfRechargeRequest = null;
		// rechargeRequestDetailsRepository
		rechargeRequestDetails = rechargeRequestDetailsRepository.findByDistributorIDAndMerchantIDAndUuid(distributorID,
				merchantID, rechargeRequestUuid);
		if (rechargeRequestDetails != null) {
			// NEW("NEW"),APPROVE("APPROVE"),REJECT("REJECT"),REQUESTED("REQUESTED"),
			// APPROVED("APPROVED");

			if (approval.equalsIgnoreCase("APPROVED")) {
				rechargeRequestDetails.setApproval(ApprovalStatus.APPROVED.toString());
			}
			if (approval.equalsIgnoreCase("REJECT")) {
				rechargeRequestDetails.setApproval(ApprovalStatus.REJECT.toString());
			}

			// INITIATED("INITIATED"),REFUNDED("REFUNDED") , PENDING("PENDING")
			// ,REJECTED("REJECTED"), SUCCESS("SUCCESS"), FAILED("FAILED");

			if (status.equalsIgnoreCase("SUCCESS")) {
				rechargeRequestDetails.setStatus(RechargeStatus.SUCCESS.toString());
			}
			if (status.equalsIgnoreCase("FAILED")) {
				rechargeRequestDetails.setStatus(RechargeStatus.FAILED.toString());
			}
			if (status.equalsIgnoreCase("REJECTED")) {
				rechargeRequestDetails.setStatus(RechargeStatus.REJECTED.toString());
			}
			if (status.equalsIgnoreCase("REFUNDED")) {
				rechargeRequestDetails.setStatus(RechargeStatus.REFUNDED.toString());
			}

			rechargeRequestDetails = rechargeRequestDetailsRepository.save(rechargeRequestDetails);
			updatedResponseOfRechargeRequest = new UpdatedResponseOfRechargeRequest();
			updatedResponseOfRechargeRequest.setRechargeRequestDetails(rechargeRequestDetails);
			updatedResponseOfRechargeRequest.setMessage("Updated successfully");
			updatedResponseOfRechargeRequest.setStatus(HttpStatus.SC_OK);
			return updatedResponseOfRechargeRequest;

		} else {
			updatedResponseOfRechargeRequest = new UpdatedResponseOfRechargeRequest();
			updatedResponseOfRechargeRequest.setRechargeRequestDetails(rechargeRequestDetails);
			updatedResponseOfRechargeRequest.setMessage("No Record Found in DB");
			updatedResponseOfRechargeRequest.setStatus(HttpStatus.SC_EXPECTATION_FAILED);

		}
		return updatedResponseOfRechargeRequest;

	}

	@Transactional
	public AddMerchantByDistributorResponse updateStatusOfAddMerchantRequestByDistributorToAdmin(String uuid,
			String distributorID, String addMerchantRequestUuid, String approval, String flagValue, String status)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		AddMerchantByDistributorResponse addMerchantByDistributorResponse = null;
		AddMerchantByDistributorRequest addMerchantByDistributorRequest = null;

		// fetch from addmerchantby_distributorrequest_details;
		// addMerchantByDistributorRequestRepository
		addMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
				.findByDistributorIDAndUuid(distributorID, addMerchantRequestUuid);
		if (addMerchantByDistributorRequest != null) {

			addMerchantByDistributorRequest.setApproval(approval);
			if (flagValue.equalsIgnoreCase("true")) {
				addMerchantByDistributorRequest.setFlagValue(Boolean.TRUE);
			} else {
				addMerchantByDistributorRequest.setFlagValue(Boolean.FALSE);
			}

			addMerchantByDistributorRequest.setStatus(status);

			addMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
					.save(addMerchantByDistributorRequest);
			addMerchantByDistributorResponse = new AddMerchantByDistributorResponse();
			addMerchantByDistributorResponse.setAddMerchantByDistributorRequest(addMerchantByDistributorRequest);
			addMerchantByDistributorResponse
					.setMessage("Add Merchant Request By Distributor " + distributorID + " is updated successfully");
			addMerchantByDistributorResponse.setStatus(HttpStatus.SC_OK);
			return addMerchantByDistributorResponse;

		} else {
			addMerchantByDistributorResponse = new AddMerchantByDistributorResponse();
			addMerchantByDistributorResponse.setAddMerchantByDistributorRequest(addMerchantByDistributorRequest);
			addMerchantByDistributorResponse
					.setMessage("NO  Add Merchant Request Found for By Distributor " + distributorID);
			addMerchantByDistributorResponse.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}

		return addMerchantByDistributorResponse;

	}

	public FindAllResponseAddMerchantByDistributorRequest findAllRequestsFromAddMerchantRequestByDistributorToAdminByDistributorID(
			String uuid, String distributorID) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = null;
		List<AddMerchantByDistributorRequest> listOfAddMerchantByDistributorRequest = null;

		// addMerchantByDistributorRequestRepository
		listOfAddMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
				.findAllByDistributorID(distributorID);
		if (listOfAddMerchantByDistributorRequest != null) {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setListOfAddMerchantByDistributorRequest(listOfAddMerchantByDistributorRequest);
			findAllResponseAddMerchantByDistributorRequest.setMessage(
					"Add Merchant Requests By Distributor with ID " + distributorID + " are fetched successfully ");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
			return findAllResponseAddMerchantByDistributorRequest;
		} else {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setMessage("Add Merchant Requests By Distributor with ID " + distributorID + ", NO Record Found");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseAddMerchantByDistributorRequest;
	}

	public FindAllResponseAddMerchantByDistributorRequest findAllRequestsFromAddMerchantRequestByDistributorToAdminWith2Parameters(
			String uuid, String distributorID, String status) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = null;
		List<AddMerchantByDistributorRequest> listOfAddMerchantByDistributorRequest = null;

		// addMerchantByDistributorRequestRepository
		listOfAddMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
				.findByDistributorIDAndStatus(distributorID, status);
		if (listOfAddMerchantByDistributorRequest != null) {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setListOfAddMerchantByDistributorRequest(listOfAddMerchantByDistributorRequest);
			findAllResponseAddMerchantByDistributorRequest.setMessage(
					"Add Merchant Requests By Distributor with ID " + distributorID + " are fetched successfully ");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
			return findAllResponseAddMerchantByDistributorRequest;
		} else {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setMessage("Add Merchant Requests By Distributor with ID " + distributorID + ", NO Record Found");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseAddMerchantByDistributorRequest;

	}

	public FindAllResponseAddMerchantByDistributorRequest findAllRequestsFromAddMerchantRequestByDistributorToAdminWith3Parameters(
			String uuid, String distributorID, String fromDate, String upToDate, String status)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = null;
		List<AddMerchantByDistributorRequest> listOfAddMerchantByDistributorRequest = null;

		// addMerchantByDistributorRequestRepository
		listOfAddMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
				.findAllByDistributorIDAndFromDateUpToDateAndStatus(distributorID, fromDate, upToDate, status);
		if (listOfAddMerchantByDistributorRequest != null) {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setListOfAddMerchantByDistributorRequest(listOfAddMerchantByDistributorRequest);
			findAllResponseAddMerchantByDistributorRequest.setMessage(
					"Add Merchant Requests By Distributor with ID " + distributorID + " are fetched successfully ");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
			return findAllResponseAddMerchantByDistributorRequest;
		} else {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setMessage("Add Merchant Requests By Distributor with ID " + distributorID + ", NO Record Found");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseAddMerchantByDistributorRequest;
	}

	public FindAllResponseRechargeRequestDetails findAllFromRechargeRequestForMerchantByDistributorToAdminByDistributorID(
			String uuid, String distributorID) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<RechargeRequestDetails> listOfRechargeRequestDetails = null;
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = null;
		// rechargeRequestDetailsRepository
		listOfRechargeRequestDetails = rechargeRequestDetailsRepository.findAllByDistributorID(distributorID);
		if (listOfRechargeRequestDetails != null) {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("Recharge Request(s) are fetched successfully");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
			return findAllResponseRechargeRequestDetails;
		} else {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("NO Record found for Recharge Request(s) in DB");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseRechargeRequestDetails;
	}

	public FindAllResponseRechargeRequestDetails findAllFromRechargeRequestForMerchantByDistributorToAdminWith3Parameters(
			String uuid, String distributorID, String fromDate, String upToDate, String status)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<RechargeRequestDetails> listOfRechargeRequestDetails = null;
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = null;
		// rechargeRequestDetailsRepository
		listOfRechargeRequestDetails = rechargeRequestDetailsRepository
				.findAllByDistributorIDAndFromDateUpToDateAndStatus(distributorID, fromDate, upToDate, status);
		if (listOfRechargeRequestDetails != null) {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("Recharge Request(s) are fetched successfully");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
			return findAllResponseRechargeRequestDetails;
		} else {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("NO Record found for Recharge Request(s) in DB");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseRechargeRequestDetails;
	}

	public FindAllResponseRechargeRequestDetails findAllFromRechargeRequestForMerchantByDistributorToAdminWith2Parameters(
			String uuid, String distributorID, String status) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<RechargeRequestDetails> listOfRechargeRequestDetails = null;
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = null;
		// rechargeRequestDetailsRepository
		listOfRechargeRequestDetails = rechargeRequestDetailsRepository
				.findAllByDistributorIDAndAndStatus(distributorID, status);
		if (listOfRechargeRequestDetails != null) {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("Recharge Request(s) are fetched successfully");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
			return findAllResponseRechargeRequestDetails;
		} else {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("NO Record found for Recharge Request(s) in DB");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseRechargeRequestDetails;
	}

	public FindAllResponseAddMerchantByDistributorRequest findAllFromAddMerchantRequestByDistributorToAdmin(String uuid)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		FindAllResponseAddMerchantByDistributorRequest findAllResponseAddMerchantByDistributorRequest = null;
		List<AddMerchantByDistributorRequest> listOfAddMerchantByDistributorRequest = null;

		// fetch from addmerchantby_distributorrequest_details;
		// addMerchantByDistributorRequestRepository
		listOfAddMerchantByDistributorRequest = addMerchantByDistributorRequestRepository.findAll();
		if (listOfAddMerchantByDistributorRequest != null) {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setListOfAddMerchantByDistributorRequest(listOfAddMerchantByDistributorRequest);
			findAllResponseAddMerchantByDistributorRequest
					.setMessage("Add Merchant Requests By Distributor are fetched successfully ");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
			return findAllResponseAddMerchantByDistributorRequest;
		} else {
			findAllResponseAddMerchantByDistributorRequest = new FindAllResponseAddMerchantByDistributorRequest();
			findAllResponseAddMerchantByDistributorRequest
					.setMessage("Add Merchant Requests By Distributor, NO Record Found");
			findAllResponseAddMerchantByDistributorRequest.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseAddMerchantByDistributorRequest;
	}

	public FindAllResponseRechargeRequestDetails findAllFromRechargeRequestForMerchantByDistributorToAdmin(String uuid)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		List<RechargeRequestDetails> listOfRechargeRequestDetails = null;
		FindAllResponseRechargeRequestDetails findAllResponseRechargeRequestDetails = null;
		// rechargeRequestDetailsRepository
		listOfRechargeRequestDetails = rechargeRequestDetailsRepository.findAll();
		if (listOfRechargeRequestDetails != null) {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("Recharge Request(s) are fetched successfully");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
			return findAllResponseRechargeRequestDetails;
		} else {
			findAllResponseRechargeRequestDetails = new FindAllResponseRechargeRequestDetails();
			findAllResponseRechargeRequestDetails.setListOfRechargeRequestDetails(listOfRechargeRequestDetails);
			findAllResponseRechargeRequestDetails.setMessage("NO Record found for Recharge Request(s) in DB");
			findAllResponseRechargeRequestDetails.setStatus(HttpStatus.SC_OK);
		}

		return findAllResponseRechargeRequestDetails;
	}

	@Transactional
	public MerchantCreateResponse createMerchantByDistributorID(String uuid, String distributorID,
			String addMerchantRequestUuid) throws ValidationExceptions, NoSuchAlgorithmException {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		// Find Distributor in DB
		DistributorDetails distributorDetails = distributorDetailsRepository.findByDistributorID(distributorID);
		// MerchantCreateRequest merchantCreateRequest = new MerchantCreateRequest();
		MerchantCreateResponse merchantCreateResponse = null;
		MerchantDetails merchantsDetails = null;
		AddMerchantByDistributorRequest addMerchantByDistributorRequest = null;
		DistributorMerchantDetails distributorMerchantDetails = new DistributorMerchantDetails();
		addMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
				.findByDistributorIDAndUuid(distributorID, addMerchantRequestUuid);

		if (addMerchantByDistributorRequest.getMerchantName().isEmpty()
				|| addMerchantByDistributorRequest.getMerchantName() == null
				|| addMerchantByDistributorRequest.getPhoneNumber().isEmpty()
				|| addMerchantByDistributorRequest.getPhoneNumber() == null
				|| addMerchantByDistributorRequest.getKycStatus().isEmpty()
				|| addMerchantByDistributorRequest.getKycStatus() == null
				|| addMerchantByDistributorRequest.getEmailId().isEmpty()
				|| addMerchantByDistributorRequest.getEmailId() == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE,
					FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}

		if (!Validator.isValidEmail(addMerchantByDistributorRequest.getEmailId())) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidPhoneNumber(addMerchantByDistributorRequest.getPhoneNumber())) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		// a.
		// Mandatory to check that Merchant is either a merchant already created with
		// incoming emailID

		// Mandatory to check that Merchant is either a merchant already created with
		// incoming ETC
		merchantsDetails = merchantDetailsRepository.findByMerchantEmail(addMerchantByDistributorRequest.getEmailId());

		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.MERCHANT_ALREADY_EXISTS);
		}

		UserAdminDetails adminUserDetails = userAdminDetailsRepository
				.findByEmailId(addMerchantByDistributorRequest.getEmailId());
		// merchantEmail and adminEmail can not be same
		if (adminUserDetails != null) {
			throw new ValidationExceptions(EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM);
		}

		// b.
		// Mandatory to check that Merchant is either a merchant already created with
		// incoming Phone
		merchantsDetails = merchantDetailsRepository
				.findByPhoneNumber(addMerchantByDistributorRequest.getPhoneNumber());

		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM);
		}

		if (!addMerchantByDistributorRequest.getSupportEmailId().isEmpty()) {
			if (!Validator.isValidEmail(addMerchantByDistributorRequest.getSupportEmailId())) {
				throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		if (!addMerchantByDistributorRequest.getSupportPhoneNo().isEmpty()) {
			if (!Validator.isValidPhoneNumber(addMerchantByDistributorRequest.getSupportPhoneNo())) {
				throw new ValidationExceptions(PHONE_VAIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}

		}
		if (!addMerchantByDistributorRequest.getLogoUrl().isEmpty()) {
			if (!Validator.isValidWebUrl(addMerchantByDistributorRequest.getLogoUrl())) {
				throw new ValidationExceptions(INVALID_URL, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		if (distributorDetails != null && addMerchantByDistributorRequest != null) {
			// A Distributor has requested a add merchant request in DB, else either
			// Distributor is not NOT in DB or Request from this Distributor is not NOT in
			// DB

			// c.
			merchantsDetails = new MerchantDetails();// why this line, re-initialization

			String appId = Utility.generateAppId();
			String secrecKey = Encryption.genSecretKey();

			merchantsDetails.setMerchantID(String.valueOf(Utility.getMerchantsID()));
			merchantsDetails.setMerchantEmail(addMerchantByDistributorRequest.getEmailId());
			merchantsDetails.setMerchantName(addMerchantByDistributorRequest.getMerchantName());
			merchantsDetails.setPhoneNumber(addMerchantByDistributorRequest.getPhoneNumber());

			if (addMerchantByDistributorRequest.getKycStatus().equalsIgnoreCase("YES")
					|| addMerchantByDistributorRequest.getKycStatus().equalsIgnoreCase("yes")) {
				merchantsDetails.setKycStatus(KycStatus.YES.toString());
			}
			if (addMerchantByDistributorRequest.getKycStatus().equalsIgnoreCase("NO")
					|| addMerchantByDistributorRequest.getKycStatus().equalsIgnoreCase("no")) {
				merchantsDetails.setKycStatus(KycStatus.NO.toString());
			}
			if (addMerchantByDistributorRequest.getKycStatus().equalsIgnoreCase("pending")
					|| addMerchantByDistributorRequest.getKycStatus().equalsIgnoreCase("PENDING")) {
				merchantsDetails.setKycStatus(KycStatus.PENDING.toString());
			}
			/*
			 * if(merchantCreateRequest.getKycStatus().equalsIgnoreCase("rejected") ||
			 * merchantCreateRequest.getKycStatus().equalsIgnoreCase("REJECTED")){
			 * merchantsDetails.setKycStatus(KycStatus.REJECTED.toString()); }
			 */
			// merchantsDetails.setAppID(Encryption.encryptCardNumberOrExpOrCvvKMS(appId));
			merchantsDetails.setAppID(appId);
			merchantsDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvvKMS(secrecKey));
			merchantsDetails.setUuid(UUID.randomUUID().toString());
			merchantsDetails.setCreatedBy(uuid);
			merchantsDetails.setSaltKey(UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
			merchantsDetails.setPassword(Encryption.getEncryptedPasswordKMS(Encryption.generateRandomPassword(8)));
			merchantsDetails.setTr_mail_flag("Y");
			merchantsDetails.setCompanyName(addMerchantByDistributorRequest.getCompanyName());
			merchantsDetails.setSupportEmailId(addMerchantByDistributorRequest.getSupportEmailId());
			merchantsDetails.setSupportPhoneNo(addMerchantByDistributorRequest.getSupportPhoneNo());
			merchantsDetails.setMerchantType(addMerchantByDistributorRequest.getMerchantType());
			merchantsDetails.setUserStatus(UserStatus.ACTIVE.toString());
			merchantsDetails.setOtpStatus(OtpStatus.DISABLE.toString());
			merchantsDetails.setLogoUrl(addMerchantByDistributorRequest.getLogoUrl());
			merchantsDetails.setPermenantLink(apiEndPoint + "customerPayment/" + Utility.randomStringGenerator(10));
			merchantsDetails.setPayoutFlag(PayoutUserStatus.ACTIVE.toString());

			// merchantsDetails.setDistributorDetails(distributorDetails);

			merchantsDetails = merchantDetailsRepository.save(merchantsDetails);// ok

			// create anew record DistributorMerchantAssociation Table

			distributorMerchantDetails.setMerchantID(merchantsDetails.getMerchantID());
			// distributorMerchantDetails.setDistributorID(distributorID);
			distributorMerchantDetails.setDistributorID(distributorDetails.getDistributorID());
			distributorMerchantDetails.setApproval(ApprovalStatus.APPROVED.toString());
			distributorMerchantDetails.setCreatedBy(uuid);
			distributorMerchantDetails.setFlagValue(Boolean.TRUE);
			distributorMerchantDetails.setRegion("Default");
			distributorMerchantDetails.setRights("Default");
			distributorMerchantDetails.setStatus(UserStatus.ACTIVE.toString());
			distributorMerchantDetails.setUpdatedBy(uuid);
			distributorMerchantDetails.setUuid(UUID.randomUUID().toString());

			distributorMerchantDetails = distributorMerchantAssociationDetailsRepository
					.save(distributorMerchantDetails);
			// addMerchantByDistributorRequest =
			// addMerchantByDistributorRequestRepository.findByDistributorIDAndUuid(distributorID,addMerchantRequestUuid);

			// update requestTable ask it and do it
			// addmerchantby_distributorrequest_details;

			addMerchantByDistributorRequest.setApproval(ApprovalStatus.APPROVED.toString());
			addMerchantByDistributorRequest.setFlagValue(Boolean.TRUE);
			addMerchantByDistributorRequest.setStatus(UserStatus.ACTIVE.toString());

			addMerchantByDistributorRequest
					.setInfo2("Merchant with ID " + merchantsDetails.getMerchantID() + "Created By Admin ID " + uuid
							+ "On Date:: " + new Date() + " Requested By a Distributor with ID " + distributorID);
			addMerchantByDistributorRequest.setInfo3("");
			addMerchantByDistributorRequest.setInfo4("");
			addMerchantByDistributorRequest.setInfo5("");
			// addMerchantByDistributorRequest.setUpdatedBy();// Error is coming this line
			// check it)
			addMerchantByDistributorRequest = addMerchantByDistributorRequestRepository
					.save(addMerchantByDistributorRequest);
			// this is Remainig task

			/***********************************
			 * Very Important thing
			 **********************************************/
			String secret = Encryption.decryptForFrontEndDataKMS(merchantsDetails.getSecretId());
			PayoutApiUserDetails payUser = new PayoutApiUserDetails();

			payUser.setMerchantId(merchantsDetails.getMerchantID());
			payUser.setToken(secret);
			payUser.setWhitelistedip("");
			payUser.setWalletCheckStatus(MerchantWalletStatus.FALSE.toString());
			payUser.setMerchantStatus(PayoutUserStatus.ACTIVE.toString());
			payoutApiUserDetailsRepo.save(payUser);// ok

			merchantDefaultPgAssociation(merchantsDetails);// ok
			/*********************************
			 * Very Important thing
			 **************************************************/

			merchantCreateResponse = new MerchantCreateResponse();

			MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository
					.findByMerchantID(merchantsDetails.getMerchantID());
			List<MerchantPGServices> merchantPGServices = merchantPGServicesRepository
					.findByMerchantID(merchantsDetails.getMerchantID());

			merchantCreateResponse.setAppId(merchantsDetails.getAppID());
			merchantCreateResponse.setCreatedBy(merchantsDetails.getCreatedBy());
			merchantCreateResponse.setUpdatedBy(merchantsDetails.getCreatedBy());
			merchantCreateResponse.setUuid(merchantsDetails.getUuid());
			merchantCreateResponse.setEmailId(merchantsDetails.getMerchantEmail());
			merchantCreateResponse.setKycStatus(merchantsDetails.getKycStatus());
			merchantCreateResponse.setMerchantId(merchantsDetails.getMerchantID());
			merchantCreateResponse.setMerchantName(merchantsDetails.getMerchantName());
			merchantCreateResponse.setPhoneNumber(merchantsDetails.getPhoneNumber());
			merchantCreateResponse.setSaltKey(Encryption.decryptForFrontEndDataKMS(merchantsDetails.getSaltKey()));
			merchantCreateResponse.setSecretId(Encryption.decryptForFrontEndDataKMS(merchantsDetails.getSecretId()));// Decrypt
																														// it
																														// then
																														// send
			merchantCreateResponse.setMerchantStatus(merchantsDetails.getUserStatus());
			merchantCreateResponse.setUserStatus(merchantsDetails.getUserStatus());
			merchantCreateResponse.setCompanyName(merchantsDetails.getCompanyName());
			merchantCreateResponse.setSupportEmailId(merchantsDetails.getSupportEmailId());
			merchantCreateResponse.setSupportPhoneNo(merchantsDetails.getSupportPhoneNo());
			merchantCreateResponse.setMerchantType(merchantsDetails.getMerchantType());
			merchantCreateResponse.setPermenantLink(merchantsDetails.getPermenantLink());
			merchantCreateResponse.setPassword(merchantsDetails.getPassword());
			merchantCreateResponse.setPayoutFlag(merchantsDetails.getPayoutFlag());
			merchantCreateResponse.setOtpStatus(merchantsDetails.getOtpStatus());
			// merchantCreateResponse.setDefaultMerchantPGDetails(merchantPGDetails);
			// merchantCreateResponse.setDefaultMerchantPGServices(merchantPGServices);
			merchantCreateResponse.setMessage(
					"Merchant created and DistributorMerchantAssociation Done successfully and AddMerchantByDistributorRequestRepository is updated Too, Requesting Distributor Id is "
							+ distributorID);
			// merchantCreateResponse.setDistributorMerchantDetails(distributorMerchantDetails);

			sendMail.sendMailCreateMerchant(merchantsDetails);

			return merchantCreateResponse;

		} else {
			// distributor not found so merchant will be not being added

			merchantCreateResponse = new MerchantCreateResponse();// ok
			merchantCreateResponse.setMessage(
					"Merchant is Not created, Failed, either Distributor is not NOT in DB or Request from this Distributor with ID "
							+ distributorID + " is not NOT in DB");

		}
		return merchantCreateResponse;

	}

	@Transactional
	public void processingRechargeRequestedByDistributorForMerchantOfSomeAmount(String uuid, String distributorID,
			String merchantID, String rechargeRequestUuid) {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		DistributorMerchantDetails distributorMerchantDetails = null;
		RechargeRequestDetails rechargeRequestDetails = null;
		// 1. Find DistributorMerchnatAssociation in DB
		distributorMerchantDetails = distributorMerchantAssociationDetailsRepository
				.findByDistributorIDAndMerchantID(distributorID, merchantID);
		// 2. Find RechargeRequest From Distributor

		rechargeRequestDetails = rechargeRequestDetailsRepository.findByDistributorIDAndMerchantIDAndUuid(distributorID,
				merchantID, rechargeRequestUuid);

		if (distributorMerchantDetails != null && rechargeRequestDetails != null) {
			// process tx
			TransactionDetails transactionDetails = new TransactionDetails();

			// follow package com.asktech.admin.service.payout.walletRecharge;
			Long amount = rechargeRequestDetails.getAmount();

			transactionDetails = transactionDetailsRepository.save(transactionDetails);
			// rechargeRequestDetails.setNotes();
			// set somevalues
			rechargeRequestDetails = rechargeRequestDetailsRepository.save(rechargeRequestDetails);

		} else {
			// Distributor merchant association not found in DB, or No Record in
			// RechargeRequest Table
		}

	}

	// api/createMerchant
	@Transactional
	public MerchantCreateResponse createMerchant(String jsonStringCreateMerchantRequest, String uuid, String pgId,
			String serviceFlag) throws ValidationExceptions, NoSuchAlgorithmException {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		MerchantCreateRequest merchantCreateRequest = new MerchantCreateRequest();
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantsDetails = new MerchantDetails();

		try {
			// JSON String to Java Object Conversion
			merchantCreateRequest = mapper.readValue(jsonStringCreateMerchantRequest, MerchantCreateRequest.class);

		} catch (Exception e) {
			throw new ValidationExceptions(JSON_PARSE_ISSUE_MERCHANT_REQUEST,
					FormValidationExceptionEnums.JSON_PARSE_EXCEPTION);
		}
		/*
		 * if (merchantCreateRequest.getMerchantName().isEmpty() ||
		 * merchantCreateRequest.getMerchantName() == null ||
		 * merchantCreateRequest.getPhoneNumber().isEmpty() ||
		 * merchantCreateRequest.getPhoneNumber() == null ||
		 * merchantCreateRequest.getLogoUrl().isEmpty() ||
		 * merchantCreateRequest.getLogoUrl() == null ||
		 * merchantCreateRequest.getSupportEmailId().isEmpty() ||
		 * merchantCreateRequest.getSupportEmailId() == null ||
		 * merchantCreateRequest.getSupportPhoneNo().isEmpty() ||
		 * merchantCreateRequest.getSupportPhoneNo() == null ||
		 * merchantCreateRequest.getKycStatus().isEmpty() ||
		 * merchantCreateRequest.getKycStatus() == null ||
		 * merchantCreateRequest.getEmailId().isEmpty() ||
		 * merchantCreateRequest.getEmailId() == null ||
		 * merchantCreateRequest.getCompanyName().isEmpty() ||
		 * merchantCreateRequest.getCompanyName() == null ||
		 * merchantCreateRequest.getCompanyType().isEmpty() ||
		 * merchantCreateRequest.getCompanyType() == null ||
		 * merchantCreateRequest.getMerchantType().isEmpty() ||
		 * merchantCreateRequest.getMerchantType() == null) { throw new
		 * ValidationExceptions(INPUT_BLANK_VALUE,
		 * FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS); }
		 */
		if (merchantCreateRequest.getMerchantName().isEmpty() || merchantCreateRequest.getMerchantName() == null
				|| merchantCreateRequest.getPhoneNumber().isEmpty() || merchantCreateRequest.getPhoneNumber() == null
				|| merchantCreateRequest.getKycStatus().isEmpty() || merchantCreateRequest.getKycStatus() == null
				|| merchantCreateRequest.getEmailId().isEmpty() || merchantCreateRequest.getEmailId() == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE,
					FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}
		logger.info("email validator :::", !Validator.isValidEmail(merchantCreateRequest.getEmailId()));
		System.out.println("email validator :::" + !Validator.isValidEmail(merchantCreateRequest.getEmailId()));
		if (!Validator.isValidEmail(merchantCreateRequest.getEmailId())) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		logger.info("email validator ::2");

		if (!Validator.isValidPhoneNumber(merchantCreateRequest.getPhoneNumber())) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		logger.info("email validator :::", !Validator.isValidEmail(merchantCreateRequest.getEmailId()));

		merchantsDetails = merchantDetailsRepository.findByMerchantEmail(merchantCreateRequest.getEmailId());

		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.MERCHANT_ALREADY_EXISTS);
		}
		logger.info("email validator ::3");
		UserAdminDetails adminUserDetails = userAdminDetailsRepository
				.findByEmailId(merchantCreateRequest.getEmailId());
		// merchantemail and adminemail can not be same
		if (adminUserDetails != null) {
			throw new ValidationExceptions(EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM);
		}
		logger.info("email validator ::24");
		merchantsDetails = merchantDetailsRepository.findByPhoneNumber(merchantCreateRequest.getPhoneNumber());
		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM);
		}
		logger.info("email validator ::25" + merchantCreateRequest.getSupportEmailId());
		if (!merchantCreateRequest.getSupportEmailId().isEmpty()) {
			if (!Validator.isValidEmail(merchantCreateRequest.getSupportEmailId())) {
				throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}
		logger.info("email validator ::26");
		if (!merchantCreateRequest.getSupportPhoneNo().isEmpty()) {
			if (!Validator.isValidPhoneNumber(merchantCreateRequest.getSupportPhoneNo())) {
				throw new ValidationExceptions(PHONE_VAIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}

		}
		logger.info("email validator ::27");
		if (!merchantCreateRequest.getLogoUrl().isEmpty()) {
			if (!Validator.isValidWebUrl(merchantCreateRequest.getLogoUrl())) {
				throw new ValidationExceptions(INVALID_URL, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}
		logger.info("email validator ::27");
		merchantsDetails = new MerchantDetails();// why this line, re-initialization

		String appId = Utility.generateAppId();
		String secrecKey = Encryption.genSecretKey();

		merchantsDetails.setMerchantID(String.valueOf(Utility.getMerchantsID()));

		merchantsDetails.setMerchantEmail(merchantCreateRequest.getEmailId());
		merchantsDetails.setMerchantName(merchantCreateRequest.getMerchantName());
		merchantsDetails.setPhoneNumber(merchantCreateRequest.getPhoneNumber());
		if (merchantCreateRequest.getKycStatus().equals("YES") || merchantCreateRequest.getKycStatus().equals("yes")) {
			merchantsDetails.setKycStatus(KycStatus.YES.toString());
		}
		if (merchantCreateRequest.getKycStatus().equals("NO") || merchantCreateRequest.getKycStatus().equals("no")) {
			merchantsDetails.setKycStatus(KycStatus.NO.toString());
		}
		if (merchantCreateRequest.getKycStatus().equals("pending")
				|| merchantCreateRequest.getKycStatus().equals("PENDING")) {
			merchantsDetails.setKycStatus(KycStatus.PENDING.toString());
		}

		// merchantsDetails.setAppID(Encryption.encryptCardNumberOrExpOrCvvKMS(appId));
		merchantsDetails.setAppID(appId);
		merchantsDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvvKMS(secrecKey));
		merchantsDetails.setUuid(UUID.randomUUID().toString());
		merchantsDetails.setCreatedBy(uuid);
		merchantsDetails.setSaltKey(UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());

		merchantsDetails.setPassword(Encryption.getEncryptedPasswordKMS(Encryption.generateRandomPassword(8)));
		merchantsDetails.setInitialPwdChange("Y");
		merchantsDetails.setTr_mail_flag("Y");
		merchantsDetails.setCompanyName(merchantCreateRequest.getCompanyName());
		merchantsDetails.setSupportEmailId(merchantCreateRequest.getSupportEmailId());
		merchantsDetails.setSupportPhoneNo(merchantCreateRequest.getSupportPhoneNo());
		merchantsDetails.setMerchantType(merchantCreateRequest.getMerchantType());
		merchantsDetails.setUserStatus(UserStatus.ACTIVE.toString());
		merchantsDetails.setOtpStatus(OtpStatus.DISABLE.toString());
		merchantsDetails.setLogoUrl(merchantCreateRequest.getLogoUrl());
		merchantsDetails.setPermenantLink(apiEndPoint + "customerPayment/" + Utility.randomStringGenerator(10));
		merchantsDetails.setPayoutFlag(PayoutUserStatus.ACTIVE.toString());
		// DistributorDetails distributorDetails = new DistributorDetails();
		// merchantsDetails.setDistributorDetails(distributorDetails);
		merchantsDetails = merchantDetailsRepository.save(merchantsDetails);

		String secret = Encryption.decryptForFrontEndDataKMS(merchantsDetails.getSecretId());
		PayoutApiUserDetails payUser = new PayoutApiUserDetails();
		payUser.setMerchantId(merchantsDetails.getMerchantID());
		payUser.setToken(secret);
		payUser.setWhitelistedip("");
		payUser.setWalletCheckStatus(MerchantWalletStatus.FALSE.toString());
		payUser.setMerchantStatus(PayoutUserStatus.ACTIVE.toString());
		payoutApiUserDetailsRepo.save(payUser);

		// PayUserDetails pud = new PayUserDetails();
		// pud.setMerchantId(merchantsDetails.getMerchantID());
		// pud.setWhitelistedip("");
		// pud.setWalletCheck("true");
		// pud.setMainWalletid("5c0385d0-f95c-4c89-ad5b-73199d947e58");
		// pud.setStatus("ACTIVE");
		// pud.setName("ANALYTIQ");
		//
		// pud.setAmount("0");
		//
		// payoutMerchant.payoutUser(pud);

		// code for default configuration association for Merchant .
		// merchantDefaultPgAssociation(merchantsDetails);
		logger.info("pgId validator ::28 " + pgId);
		if (pgId != null && !pgId.isEmpty()) {
			merchantPgAssociation(merchantsDetails, pgId, serviceFlag);

		}
		logger.info("merchantPgAssociation validator ::29 " + pgId);
		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository
				.findByMerchantID(merchantsDetails.getMerchantID());
		List<MerchantPGServices> merchantPGServices = merchantPGServicesRepository
				.findByMerchantID(merchantsDetails.getMerchantID());

		merchantCreateResponse.setAppId(appId);
		merchantCreateResponse.setCreatedBy(merchantsDetails.getCreatedBy());
		merchantCreateResponse.setUpdatedBy(merchantsDetails.getCreatedBy());
		merchantCreateResponse.setUuid(merchantsDetails.getUuid());
		merchantCreateResponse.setEmailId(merchantsDetails.getMerchantEmail());
		merchantCreateResponse.setKycStatus(merchantsDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantsDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantsDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantsDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(Encryption.decryptForFrontEndDataKMS(merchantsDetails.getSecretId()));
		merchantCreateResponse.setMerchantStatus(merchantsDetails.getUserStatus());
		merchantCreateResponse.setUserStatus(merchantsDetails.getUserStatus());
		merchantCreateResponse.setPassword(Encryption.decryptForFrontEndDataKMS(merchantsDetails.getPassword()));
		merchantCreateResponse.setCompanyName(merchantsDetails.getCompanyName());
		merchantCreateResponse.setSupportEmailId(merchantsDetails.getSupportEmailId());
		merchantCreateResponse.setSupportPhoneNo(merchantsDetails.getSupportPhoneNo());
		merchantCreateResponse.setMerchantType(merchantsDetails.getMerchantType());
		merchantCreateResponse.setSaltKey(merchantsDetails.getSaltKey());
		merchantCreateResponse.setPermenantLink(merchantsDetails.getPermenantLink());
		merchantCreateResponse.setOtpStatus(merchantsDetails.getOtpStatus());
		merchantCreateResponse.setPayoutFlag(merchantsDetails.getPayoutFlag());
		merchantCreateResponse.setOtpStatus(merchantsDetails.getOtpStatus());
		// merchantCreateResponse.setDefaultMerchantPGDetails(merchantPGDetails);
		// merchantCreateResponse.setDefaultMerchantPGServices(merchantPGServices);
		merchantCreateResponse
				.setMessage("Merchant created successfully with MerchantID " + merchantsDetails.getMerchantID());
		sendMail.sendMailCreateMerchant(merchantsDetails);

		return merchantCreateResponse;
	}

	public PayoutApiUserDetails updateStatusPayoutMerchant(String status, String merchantId)
			throws ValidationExceptions {

		PayoutApiUserDetails payUser = new PayoutApiUserDetails();
		payUser = payoutApiUserDetailsRepo.findByMerchantId(merchantId);
		if (payUser == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		if (PayoutUserStatus.ACTIVE.toString().equals(status)) {
			payUser.setMerchantStatus(PayoutUserStatus.ACTIVE.toString());
			payoutApiUserDetailsRepo.save(payUser);
		}
		if (PayoutUserStatus.BLOCKED.toString().equals(status)) {
			payUser.setMerchantStatus(PayoutUserStatus.BLOCKED.toString());
			payoutApiUserDetailsRepo.save(payUser);
		}

		return payUser;
	}

	// api/createMerchantPGDetails
	// A Merchant is being assigned a new PG
	/*
	 * public MerchantPgDetailRes associatePGDetails(String merchantId, String
	 * pgUuid, String uuid) throws ValidationExceptions {
	 * logger.info("createPGDetails In this Method."); if (merchantId.isEmpty() ||
	 * merchantId == null) { throw new ValidationExceptions(INPUT_EMPTY_NULL,
	 * FormValidationExceptionEnums.INPUT_EMPTY_NULL); } if (pgUuid.isEmpty() ||
	 * pgUuid == null) { throw new ValidationExceptions(INPUT_EMPTY_NULL,
	 * FormValidationExceptionEnums.INPUT_EMPTY_NULL); } if (uuid.isEmpty() || uuid
	 * == null) { throw new ValidationExceptions(INPUT_EMPTY_NULL,
	 * FormValidationExceptionEnums.INPUT_EMPTY_NULL); }
	 * 
	 * //1. MerchantDetails merchantDetails =
	 * merchantDetailsRepository.findByMerchantID(merchantId); // MerchantPGDetails,
	 * this class is keeps association between PG and Merchant
	 * 
	 * //2. MerchantPGDetails merchantPGDetails = new MerchantPGDetails();
	 * 
	 * if (merchantDetails == null) {
	 * 
	 * throw new ValidationExceptions(MERCHNT_NOT_EXISTIS,
	 * FormValidationExceptionEnums.MERCHANT_NOT_FOUND); }
	 * 
	 * if (merchantDetails.getUserStatus().equalsIgnoreCase("BLOCKED")) { throw new
	 * ValidationExceptions(USER_STATUS_BLOCKED,
	 * FormValidationExceptionEnums.USER_STATUS_BLOCKED); }
	 * 
	 * 
	 * //3. PGConfigurationDetails pgConfigurationDetails =
	 * pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
	 * 
	 * if (pgConfigurationDetails != null) { if
	 * (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.
	 * toString())) { throw new ValidationExceptions(PG_NOT_ACTIVE,
	 * FormValidationExceptionEnums.PG_NOT_ACTIVE); } }
	 * 
	 * //4. merchantPGDetails =
	 * merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantDetails.
	 * getMerchantID(), pgUuid);
	 * 
	 * if (merchantPGDetails != null) {
	 * 
	 * throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_EXISTS,
	 * FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_EXISTS); }
	 * 
	 * if (pgConfigurationDetails != null) { merchantPGDetails = new
	 * MerchantPGDetails();// REINITIALIZE
	 * merchantPGDetails.setMerchantID(merchantDetails.getMerchantID());
	 * merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
	 * merchantPGDetails.setMerchantPGId(pgConfigurationDetails.getPgUuid());
	 * merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
	 * merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey())
	 * ; merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
	 * merchantPGDetails.setMerchantPGId(pgUuid);
	 * merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
	 * merchantPGDetails.setMerchantPGAdd1(pgConfigurationDetails.getPgAddInfo1());
	 * merchantPGDetails.setMerchantPGAdd2(pgConfigurationDetails.getPgAddInfo2());
	 * merchantPGDetails.setMerchantPGAdd3(pgConfigurationDetails.getPgAddInfo3());
	 * merchantPGDetails = merchantPGDetailsRepository.save(merchantPGDetails); }
	 * 
	 * 
	 * //String secret =
	 * Encryption.decryptForFrontEndDataKMS(merchantPGDetails.getMerchantPGSecret())
	 * ;
	 * 
	 * MerchantPgDetailRes res = new MerchantPgDetailRes(); if(merchantPGDetails !=
	 * null) { res.setId(merchantPGDetails.getId());
	 * res.setMerchantID(merchantPGDetails.getMerchantID());
	 * res.setMerchantPGAppId(merchantPGDetails.getMerchantPGAppId());
	 * res.setMerchantPGId(merchantPGDetails.getMerchantPGId());
	 * res.setMerchantPGSecret(Encryption.decryptForFrontEndDataKMS(
	 * merchantPGDetails.getMerchantPGSecret()));
	 * res.setMerchantPGSaltKey(merchantPGDetails.getMerchantPGSaltKey());
	 * res.setStatus(merchantPGDetails.getStatus());
	 * res.setMerchantPGId(merchantPGDetails.getMerchantPGId());
	 * res.setMerchantPGName(merchantPGDetails.getMerchantPGName());
	 * res.setMerchantPGAdd1(merchantPGDetails.getMerchantPGAdd1());
	 * res.setMerchantPGAdd2(merchantPGDetails.getMerchantPGAdd2());
	 * res.setMerchantPGAdd3(merchantPGDetails.getMerchantPGAdd3());
	 * res.setCreated(merchantPGDetails.getCreated().toString());
	 * res.setUpdated(merchantPGDetails.getUpdated().toString()); return res; }
	 * return res; }
	 */

	public MerchantPgDetailRes associatePGDetails(String merchantId, String pgUuid, String uuid)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		logger.info("associatePGDetails In this Method.");
		if (merchantId.isEmpty() || merchantId == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (pgUuid.isEmpty() || pgUuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (uuid.isEmpty() || uuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}

		MerchantDetails merchantDetails = null;
		MerchantPGDetails merchantPGDetails = null;
		PGConfigurationDetails pgConfigurationDetails = null;
		MerchantPgDetailRes res = null;
		// 1.
		merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (merchantDetails.getUserStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(MERCHANT_STATUS_BLOCKED,
					FormValidationExceptionEnums.MERCHANT_STATUS_BLOCKED);
		}

		// 2.
		pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);

		if (pgConfigurationDetails != null) {
			if (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
				throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
			}
		} else {
			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}

		// 3.
		merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantDetails.getMerchantID(),
				pgUuid);

		if (merchantPGDetails != null) {

			throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_EXISTS,
					FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_EXISTS);
		}

		if (pgConfigurationDetails != null) {
			// If
			merchantPGDetails = new MerchantPGDetails();// REINITIALIZE
			merchantPGDetails.setMerchantID(merchantDetails.getMerchantID());
			merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
			merchantPGDetails.setMerchantPGId(pgConfigurationDetails.getPgUuid());
			merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
			merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey());
			merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
			merchantPGDetails.setMerchantPGId(pgUuid);
			merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
			merchantPGDetails.setMerchantPGAdd1(pgConfigurationDetails.getPgAddInfo1());
			merchantPGDetails.setMerchantPGAdd2(pgConfigurationDetails.getPgAddInfo2());
			merchantPGDetails.setMerchantPGAdd3(pgConfigurationDetails.getPgAddInfo3());
			merchantPGDetails = merchantPGDetailsRepository.save(merchantPGDetails);
		}

		// String secret =
		// Encryption.decryptForFrontEndDataKMS(merchantPGDetails.getMerchantPGSecret());

		if (merchantPGDetails != null) {
			res = new MerchantPgDetailRes();
			res.setId(merchantPGDetails.getId());
			res.setMerchantID(merchantPGDetails.getMerchantID());
			res.setMerchantPGAppId(merchantPGDetails.getMerchantPGAppId());
			res.setMerchantPGId(merchantPGDetails.getMerchantPGId());
			res.setMerchantPGSecret(merchantPGDetails.getMerchantPGSecret());// BaCipherException during decryption
			res.setMerchantPGSaltKey(merchantPGDetails.getMerchantPGSaltKey());
			res.setStatus(merchantPGDetails.getStatus());
			res.setMerchantPGId(merchantPGDetails.getMerchantPGId());
			res.setMerchantPGName(merchantPGDetails.getMerchantPGName());
			res.setMerchantPGAdd1(merchantPGDetails.getMerchantPGAdd1());
			res.setMerchantPGAdd2(merchantPGDetails.getMerchantPGAdd2());
			res.setMerchantPGAdd3(merchantPGDetails.getMerchantPGAdd3());
			res.setCreated(merchantPGDetails.getCreated().toString());
			res.setUpdated(merchantPGDetails.getUpdated().toString());
			return res;
		}
		return res;
	}

	// api/createMerchantPGServices
	public MerchantPGServiceAssociationResponse associatePGServicesToAMerchant(String pgUuid, String merchantService,
			String uuid, String merchantId) throws ValidationExceptions {
		logger.info("associatePGServices In this Method.");
		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("associatePGServices In this Method. 2222");
		List<MerchantPGServices> merchantPGServices2 = merchantPGServicesRepository
				.getByMerchantIDAndServiceAndPgID(merchantId, merchantService, pgUuid);
		System.out.println("merchantPGServices2 null check :::: " + merchantPGServices2 == null);
		if (merchantPGServices2 != null && merchantPGServices2.size() > 0) {
			throw new ValidationExceptions("SERVICE IS ALREDY IN SYSTEM",
					FormValidationExceptionEnums.PGID_AND_SERVICE_ALREADY_MAPPED_WITH_MERCHANT);
		}

		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<MerchantPGServices> listOfMerchantPGServices = null;
		List<MerchantPGServices> fListOfMerchantPGServices = null;
		MerchantPGServices merchantPGServices = null;
		MerchantDetails merchantDetails = null;
		PGConfigurationDetails pgConfigurationDetails = null;
		MerchantPGDetails merchantPGDetails = null;
		PGServiceDetails pgServiceDetails = null;
		MerchantPGServiceAssociationResponse response = null;

		if (merchantId.isEmpty() || merchantId == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (pgUuid.isEmpty() || pgUuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (uuid.isEmpty() || uuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (merchantService == null) {
			throw new ValidationExceptions(PG_SERVICE_IS_EMPTY, FormValidationExceptionEnums.PG_SERVICE_IS_EMPTY);
		}

		merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		// MerchantPGServices merchantPGServices = new MerchantPGServices();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (merchantDetails.getUserStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}
		if (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
		}

		merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantDetails.getMerchantID(),
				pgUuid);

		if (merchantPGDetails == null) {

			throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_NON_EXISTS,
					FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_NON_EXISTS);
		}

		if (merchantPGDetails.getStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(MERCHANT_PG_STATUS_BLOCKED,
					FormValidationExceptionEnums.MERCHANT_PG_STATUS_BLOCKED);
		}

		// 1.
		pgServiceDetails = pgServiceDetailsRepository.findByPgIdAndPgServices(pgUuid, merchantService.toUpperCase());

		if (pgServiceDetails == null) {
			throw new ValidationExceptions(PG_SERVICE_ASSOCIATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_ASSOCIATION_NOT_FOUND);
		}

		logger.info("Before Checking DB :: " + merchantId + " , PG id :: "
				+ String.valueOf(pgConfigurationDetails.getId()) + " , service :: " + merchantService);
		// 2.
		// merchantPGServices =
		// merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(merchantId,
		// pgUuid, merchantService);
		listOfMerchantPGServices = merchantPGServicesRepository.getByMerchantIDAndServiceAndPgID(merchantId,
				merchantService, pgUuid);

		List<String> listOfService = merchantPGServicesRepository.findAllServiceByMerchantID(merchantId);
		// !merchantPGServices.getStatus().equalsIgnoreCase("BLOCKED")

		if (listOfMerchantPGServices != null && listOfMerchantPGServices.size() > 0) {
			for (MerchantPGServices MPGS : listOfMerchantPGServices) {

				// same serive in Active state but different PG
				// here i just cut same service code && listOfService.contains(merchantService)
				if (listOfService != null && listOfService.contains(merchantService)
						&& MPGS.getStatus().equalsIgnoreCase("ACTIVE") && !MPGS.getPgID().equalsIgnoreCase(pgUuid)) {
					// PG is different but Same Service is in Blocked state

					// allow same Service to be associated with merchantID via Different PG
					merchantPGServices = new MerchantPGServices();// Re Init
					merchantPGServices.setMerchantID(merchantDetails.getMerchantID());

					merchantPGServices.setPgID(pgUuid);// Fixed by abhimanyu
					merchantPGServices.setService(merchantService.toUpperCase());
					merchantPGServices.setStatus(UserStatus.ACTIVE.toString());
					merchantPGServices.setCreatedBy(uuid);
					merchantPGServices.setProcessedBy(uuid);
					merchantPGServices.setUpdated(new Date());
					merchantPGServices.setUpdatedBy(uuid);
					merchantPGServices = merchantPGServicesRepository.save(merchantPGServices);
					System.out.println("merchantPGServices = merchantPGServicesRepository.save(merchantPGServices)");
					response = new MerchantPGServiceAssociationResponse();
					response.setMerchantPGServices(merchantPGServices);
					response.setMessage("This Merchant with ID " + merchantId + " Associated successfully "
							+ merchantService + " as Service ");
					response.setStatus(HttpStatus.SC_OK);
				} else if (listOfService != null && listOfService.contains(merchantService)
						&& MPGS.getStatus().equalsIgnoreCase("ACTIVE") && MPGS.getPgID().equalsIgnoreCase(pgUuid)) {
					System.out.println("This Merchant has already using this Service " + merchantService);
					// stop
					response = new MerchantPGServiceAssociationResponse();
					merchantPGServices = merchantPGServicesRepository
							.findByMerchantIDAndPgIDAndServiceAndStatus(merchantId, pgUuid, merchantService, "ACTIVE");

					response.setMerchantPGServices(merchantPGServices);// db find

					response.setMessage("This Merchant with ID " + merchantId + " is already using this Service "
							+ merchantService + " In Active State " + merchantPGServices.getStatus());
					response.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
				}
				// here is remove condition of same sevice check which is &&
				// listOfService.contains(merchantService)
				// } else if (listOfService != null
				// && MPGS.getStatus().equalsIgnoreCase("BLOCKED") &&
				// MPGS.getPgID().equalsIgnoreCase(pgUuid)) {
				// // System.out.println("This Merchant has already using this Service
				// // "+merchantService);
				// // stop
				// String status = "BLOCKED";
				// merchantPGServices = merchantPGServicesRepository
				// .findByMerchantIDAndPgIDAndServiceAndStatus(merchantId, pgUuid,
				// merchantService, status);
				// merchantPGServices.setUpdated(new Date());
				// merchantPGServices.setUpdatedBy(uuid);
				// merchantPGServices.setStatus("ACTIVE");

				// merchantPGServices = merchantPGServicesRepository.save(merchantPGServices);
				// // System.out.println("merchantPGServices =
				// // merchantPGServicesRepository.save(merchantPGServices)");
				// response = new MerchantPGServiceAssociationResponse();
				// response.setMerchantPGServices(merchantPGServices);
				// response.setMessage("[Updated]This Merchant with ID " + merchantId
				// + " Associated Updated successfully " + merchantService + " as Service ");
				// response.setStatus(HttpStatus.SC_OK);
				// } else if (listOfService != null && listOfService.contains(merchantService)
				// && MPGS.getStatus().equalsIgnoreCase("ACTIVE") &&
				// !MPGS.getPgID().equalsIgnoreCase(pgUuid)) {
				// System.out.println("This Merchant has already using this Service " +
				// merchantService);
				// // stop
				// response = new MerchantPGServiceAssociationResponse();

				// fListOfMerchantPGServices =
				// merchantPGServicesRepository.findByMerchantIDAndService(merchantId,
				// merchantService);
				// // String merchantDiifferentID = (MPGS.getMerchantID()!=pgUuid)
				// String merchantDiifferentPGID = null;
				// for (MerchantPGServices lMPGS : fListOfMerchantPGServices) {
				// if (lMPGS.getPgID() != pgUuid)
				// merchantDiifferentPGID = lMPGS.getPgID();
				// }
				// List<MerchantPGServices> m =
				// merchantPGServicesRepository.findByMerchantIDAndPgID(merchantId,
				// merchantDiifferentPGID);
				// for (MerchantPGServices lMPGS : m) {
				// if (lMPGS.getPgID() != pgUuid)
				// merchantDiifferentPGID = lMPGS.getPgID();
				// }
				// MerchantPGServices s =
				// merchantPGServicesRepository.findByMerchantIDAndPgIDAndStatusAndService(
				// merchantId, merchantDiifferentPGID, "ACTIVE", merchantService);
				// response.setMerchantPGServices(s);// db find

				// response.setMessage("This Merchant with ID " + merchantId + " is already
				// using this Service "
				// + merchantService + " In State Of " + s.getStatus());
				// response.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
				// }
			}
		} else {
			// Brand new association
			merchantPGServices = new MerchantPGServices();// Re Init
			merchantPGServices.setMerchantID(merchantDetails.getMerchantID());

			merchantPGServices.setPgID(pgUuid);// Fixed by abhimanyu
			merchantPGServices.setService(merchantService.toUpperCase());
			merchantPGServices.setStatus(UserStatus.ACTIVE.toString());
			merchantPGServices.setCreatedBy(uuid);
			merchantPGServices.setProcessedBy(uuid);
			merchantPGServices.setUpdated(new Date());
			merchantPGServices.setUpdatedBy(uuid);
			merchantPGServices = merchantPGServicesRepository.save(merchantPGServices);
			System.out.println("merchantPGServices = merchantPGServicesRepository.save(merchantPGServices)");
			response = new MerchantPGServiceAssociationResponse();
			response.setMerchantPGServices(merchantPGServices);
			response.setMessage("This Merchant with ID " + merchantId + " Associated successfully " + merchantService
					+ " as Service ");
			response.setStatus(HttpStatus.SC_OK);
		}

		/*
		 * if(listOfService != null &&
		 * merchantPGServices.getStatus().equalsIgnoreCase("BLOCKED") &&
		 * !merchantPGServices.getPgID().equalsIgnoreCase(pgUuid)) {
		 * 
		 * if(merchantPGDetails != null && listOfService.contains(merchantService)) {
		 * System.out.println("This Merchant has already using this Service "
		 * +merchantService); // stop response = new
		 * MerchantPGServiceAssociationResponse(); //merchantPGServices =
		 * merchantPGServicesRepository.findByMerchantIDAndService(merchantId,
		 * merchantService.toUpperCase());
		 * 
		 * response.setMerchantPGServices(merchantPGServices);//db find
		 * 
		 * response.setMessage("This Merchant with ID "
		 * +merchantId+" is already using this Service "+merchantService);
		 * response.setStatus(HttpStatus.SC_EXPECTATION_FAILED); }else { //do associate
		 * PGService merchantPGServices = new MerchantPGServices();// Re Init
		 * merchantPGServices.setMerchantID(merchantDetails.getMerchantID());
		 * 
		 * merchantPGServices.setPgID(pgUuid);// Fixed by abhimanyu
		 * merchantPGServices.setService(merchantService.toUpperCase());
		 * merchantPGServices.setStatus(ApprovalStatus.NEW.toString());
		 * merchantPGServices.setCreatedBy(uuid);
		 * merchantPGServices.setProcessedBy(uuid); merchantPGServices.setUpdated(new
		 * Date()); merchantPGServices.setUpdatedBy(uuid); merchantPGServices =
		 * merchantPGServicesRepository.save(merchantPGServices); System.out.
		 * println("merchantPGServices = merchantPGServicesRepository.save(merchantPGServices)"
		 * ); response = new MerchantPGServiceAssociationResponse();
		 * response.setMerchantPGServices(merchantPGServices);
		 * response.setMessage("This Merchant with ID "
		 * +merchantId+" associated successfully "+merchantService+" as Service ");
		 * response.setStatus(HttpStatus.SC_OK); } }else { System.out.println("First");
		 * 
		 * }
		 */

		return response;
	}

	// api/admin/updatePGService
	public Object updatePGService(String uuid, String pgUuid, String statusUpdate, String service) throws Exception {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		PGServiceDetails pgServiceDetails = null;
		PGConfigurationDetails pgConfigurationDetails = null;
		SuccessResponseDto sdto = null;

		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		if (uuid.isEmpty() || uuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (pgUuid.isEmpty() || pgUuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (statusUpdate.isEmpty() || statusUpdate == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (service.isEmpty() || service == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}

		if (!Validator.containsEnum(UserStatus.class, statusUpdate.toUpperCase())) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		if (!Validator.containsEnum(PGServices.class, service.toUpperCase())) {
			throw new ValidationExceptions(PG_SERVICE_NOT_FOUND, FormValidationExceptionEnums.PG_SERVICE_NOT_FOUND);
		}

		// 1. find PG in DB by pgUuid
		pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);

		if (pgConfigurationDetails == null) {

			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}

		System.out.println("pgConfigurationDetails.getPgName()  : " + pgConfigurationDetails.getPgName());
		logger.info("pgConfigurationDetails:: " + pgConfigurationDetails.getPgName());

		// Note:- Now we are working with these two values only
		// ACTIVE("ACTIVE"),BLOCKED("BLOCKED")

		// PGConfigurationDetails->>>pgconfiguration_details
		// PGServiceDetails ->>>pgservice_details
		// MerchantPGDetails->>>merchantpgdetails
		// MerchantPGServices->>merchantpgservices

		pgServiceDetails = pgServiceDetailsRepository
				.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getPgUuid()), service);

		if (pgServiceDetails != null) {

			System.out
					.println("BEFORE   :   " + "pgServiceDetails.getPgServices()  : " + pgServiceDetails.getPgServices()
							+ " pgServiceDetails.getStatus() : " + pgServiceDetails.getStatus());

			if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
				pgServiceDetails.setStatus(UserStatus.ACTIVE.toString());

			} else if (statusUpdate.equalsIgnoreCase(UserStatus.BLOCKED.toString())) {
				pgServiceDetails.setStatus(UserStatus.BLOCKED.toString());
			}
			pgServiceDetails.setUpdatedBy(uuid);
			pgServiceDetails.setUpdated(new Date());
			/*** pgServiceDetails */
			pgServiceDetails = pgServiceDetailsRepository.save(pgServiceDetails);
			/*** pgServiceDetails */

			List<MerchantPGDetails> listMerchantPGDetails = merchantPGDetailsRepository
					.findAllByMerchantPGId(pgConfigurationDetails.getPgUuid());

			PGServiceDetails pgServiceDetails2 = pgServiceDetailsRepository
					.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getPgUuid()), service);

			if (listMerchantPGDetails != null) {
				for (MerchantPGDetails merchantPGDetails : listMerchantPGDetails) {

					logger.info("MerchantPGDetails :: " + Utility.convertDTO2JsonString(merchantPGDetails));
					if (pgServiceDetails2.getStatus().equalsIgnoreCase(UserStatus.BLOCKED.toString())) {
						logger.info("Inside Merchant Service :: " + pgConfigurationDetails.getPgUuid());

						List<MerchantPGServices> updMerchantPGServices = new ArrayList<MerchantPGServices>();
						List<MerchantPGServices> listMerchantPGService = merchantPGServicesRepository
								.findAllByPgIDAndService(merchantPGDetails.getMerchantPGId(), service);

						for (MerchantPGServices merchantPGServices : listMerchantPGService) {

							logger.info(
									"Inside Merchant Service :: " + Utility.convertDTO2JsonString(merchantPGServices));
							merchantPGServices.setStatus(UserStatus.BLOCKED.toString());

							// System.out.println("status " + pgServiceDetails2.getStatus());
							updMerchantPGServices.add(merchantPGServices);
						}

						merchantPGServicesRepository.saveAll(updMerchantPGServices);
					} else {
						logger.info("Inside Merchant Service :: " + pgConfigurationDetails.getPgUuid());

						List<MerchantPGServices> updMerchantPGServices = new ArrayList<MerchantPGServices>();
						List<MerchantPGServices> listMerchantPGService = merchantPGServicesRepository
								.findAllByPgIDAndService(merchantPGDetails.getMerchantPGId(), service);

						for (MerchantPGServices merchantPGServices : listMerchantPGService) {

							logger.info(
									"Inside Merchant Service :: " + Utility.convertDTO2JsonString(merchantPGServices));
							merchantPGServices.setStatus(UserStatus.ACTIVE.toString());

							// System.out.println("status " + pgServiceDetails2.getStatus());
							updMerchantPGServices.add(merchantPGServices);
						}

						merchantPGServicesRepository.saveAll(updMerchantPGServices);
					}

				}

			}
			// now send response to controller
			sdto = new SuccessResponseDto();
			sdto.getMsg().add("updatePGService is done successfully!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("pgDetail", pgServiceDetails);
			return sdto;

		} else {
			sdto = new SuccessResponseDto();
			sdto.getMsg().add("updatePGService is Failed!");
			sdto.setSuccessCode(SuccessCode.API_FAILED);
			sdto.getExtraData().put("pgDetail", pgServiceDetails);
		}
		return sdto;
	}

	/// api/admin/changeMechantPassword
	public Map<Object, Object> changeMerchantPassword(String merchantId)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails != null) {

			String password = Utility.randomStringGenerator(8);
			String pass2 = "" + merchantId + password;

			String encodedPassword = Encryption.getEncryptedPasswordKMS(merchantId + password);
			Map<Object, Object> encryptedAndDecryptedDataMap = new HashMap<>();// ok
			// Map<Object, Object> encryptedAndDecryptedDataMap =
			// Utility.RSAEncryptionDecryption(merchantId);//ok
			// Object encryptedData = encryptedAndDecryptedDataMap.get("EncryptedData");
			// Object decryptedData = encryptedAndDecryptedDataMap.get("DecryptedData");
			// System.out.println(encodedPassword);
			merchantDetails.setPassword(encodedPassword);
			merchantDetails = merchantDetailsRepository.save(merchantDetails);
			merchantDetails.setPassword(pass2);
			encryptedAndDecryptedDataMap.put("merchantDetails", merchantDetails);
			return encryptedAndDecryptedDataMap;
		}
		Map<Object, Object> merchantNotFoundMap = new HashMap<>();
		merchantNotFoundMap.put("merchnat not Found with ID : ", merchantId);
		return merchantNotFoundMap;
	}

	// api/createDistributor
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DistributorResponse createDistributor(String createDistributorRequest, String uuid)
			throws ValidationExceptions, NoSuchAlgorithmException {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);

		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		CreateDistributorRequest createDistributorDetailsRequest = new CreateDistributorRequest();
		DistributorDetails distributorDetails = null;

		try {

			createDistributorDetailsRequest = mapper.readValue(createDistributorRequest,
					CreateDistributorRequest.class);

		} catch (Exception e) {
			throw new ValidationExceptions(JSON_PARSE_ISSUE_DISTRIBUTOR_REQUEST,
					FormValidationExceptionEnums.JSON_PARSE_EXCEPTION);
		}

		if (createDistributorDetailsRequest.getDistributorName().isEmpty()
				|| createDistributorDetailsRequest.getDistributorName() == null
				|| createDistributorDetailsRequest.getPhoneNumber().isEmpty()
				|| createDistributorDetailsRequest.getPhoneNumber() == null
				|| createDistributorDetailsRequest.getKycStatus().isEmpty()
				|| createDistributorDetailsRequest.getKycStatus() == null
				|| createDistributorDetailsRequest.getDistributorEmailId().isEmpty()
				|| createDistributorDetailsRequest.getDistributorEmailId() == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE,
					FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}

		distributorDetails = distributorDetailsRepository
				.findByDistributorEMail(createDistributorDetailsRequest.getDistributorEmailId());
		if (distributorDetails != null) {

			throw new ValidationExceptions(EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.EMAIL_ALREADY_EXISTS);
		}
		distributorDetails = distributorDetailsRepository
				.findByPhoneNumber(createDistributorDetailsRequest.getPhoneNumber());
		if (distributorDetails != null) {
			throw new ValidationExceptions(DISTRIBUTOR_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.DISTRIBUTOR_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM);
		}

		if (!Validator.isValidEmail(createDistributorDetailsRequest.getDistributorEmailId())) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidPhoneNumber(createDistributorDetailsRequest.getPhoneNumber())) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		if (!createDistributorDetailsRequest.getSupportEmailId().isEmpty()) {
			if (!Validator.isValidEmail(createDistributorDetailsRequest.getSupportEmailId())) {
				throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		if (!createDistributorDetailsRequest.getSupportPhoneNo().isEmpty()) {
			if (!Validator.isValidPhoneNumber(createDistributorDetailsRequest.getSupportPhoneNo())) {
				throw new ValidationExceptions(PHONE_VAIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}

		}
		if (!createDistributorDetailsRequest.getLogoUrl().isEmpty()) {
			if (!Validator.isValidWebUrl(createDistributorDetailsRequest.getLogoUrl())) {
				throw new ValidationExceptions(INVALID_URL, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		// Insert data
		String appId = Utility.generateAppId();
		String secrecKey = Encryption.genSecretKey();

		distributorDetails = new DistributorDetails();

		distributorDetails.setDistributorID(String.valueOf(Utility.getDistributorID()));
		distributorDetails.setUuid(UUID.randomUUID().toString());
		distributorDetails.setAppID(appId);// ask about this to sir

		distributorDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvv(secrecKey));// ask about this to sir

		distributorDetails.setCompanyName(createDistributorDetailsRequest.getCompanyName());
		distributorDetails.setCreatedBy(uuid);
		distributorDetails.setUpdatedBy(uuid);
		distributorDetails.setDistributorEMail(createDistributorDetailsRequest.getDistributorEmailId());
		distributorDetails.setDistributorName(createDistributorDetailsRequest.getDistributorName());
		distributorDetails.setDistributorType(createDistributorDetailsRequest.getDistributorType());
		distributorDetails.setInitialPwdChange("");// ask about this to sir
		distributorDetails.setPassword(Encryption.getEncryptedPasswordKMS(Encryption.generateRandomPassword(8)));

		if (createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("YES")
				|| createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("yes")) {
			distributorDetails.setKycStatus(KycStatus.YES.toString());
		}
		if (createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("NO")
				|| createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("no")) {
			distributorDetails.setKycStatus(KycStatus.NO.toString());
		}
		if (createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("PENDING")
				|| createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("pending")) {
			distributorDetails.setKycStatus(KycStatus.PENDING.toString());
		}
		/*
		 * if(createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase(
		 * "rejected") ||
		 * createDistributorDetailsRequest.getKycStatus().equalsIgnoreCase("REJECTED")){
		 * distributorDetails.setKycStatus(KycStatus.REJECTED.toString()); }
		 */

		distributorDetails.setKycStatus(createDistributorDetailsRequest.getKycStatus());

		distributorDetails.setLogoUrl(createDistributorDetailsRequest.getLogoUrl());

		distributorDetails.setPhoneNumber(createDistributorDetailsRequest.getPhoneNumber());
		distributorDetails.setSaltKey(UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());

		distributorDetails.setSupportEmailId(createDistributorDetailsRequest.getSupportEmailId());
		distributorDetails.setSupportPhoneNo(createDistributorDetailsRequest.getSupportPhoneNo());
		distributorDetails.setTr_mail_flag("Y");
		distributorDetails.setUserSession(null);
		distributorDetails.setUserStatus(UserStatus.ACTIVE.toString());

		// Set<MerchantDetails> setOfMerchantDetails = new LinkedHashSet<>();
		// MerchantDetails merchantDetails = new MerchantDetails();
		// setOfMerchantDetails.add(merchantDetails);
		// distributorDetails.setSetOfMerchantDetails(setOfMerchantDetails);

		distributorDetails = distributorDetailsRepository.save(distributorDetails);// ok

		/** is required PayoutApiUserDetails then do it */
		/***/

		// Response DTO
		DistributorResponse distributorResponse = new DistributorResponse();
		distributorResponse.setUuid(distributorDetails.getUuid());

		distributorResponse.setAppId(distributorDetails.getAppID());// decrypt it then send

		distributorResponse.setUserStatus(distributorDetails.getUserStatus());
		distributorResponse.setCompanyName(distributorDetails.getCompanyName());
		distributorResponse.setDistributorName(distributorDetails.getDistributorName());
		distributorResponse.setPhoneNumber(distributorDetails.getPhoneNumber());
		distributorResponse.setCreatedBy(distributorDetails.getCreatedBy());
		distributorResponse.setUpdatedBy(distributorDetails.getUpdatedBy());
		distributorResponse.setDistributorEMail(distributorDetails.getDistributorEMail());
		distributorResponse.setDistributorId(distributorDetails.getDistributorID());
		distributorResponse.setDistributorType(distributorDetails.getDistributorType());
		distributorResponse.setKycStatus(distributorDetails.getKycStatus());
		distributorResponse.setLogoUrl(distributorDetails.getLogoUrl());
		distributorResponse.setPassword(Encryption.getDecryptedPasswordKMS(distributorDetails.getPassword()));
		distributorResponse.setSaltKey(distributorDetails.getSaltKey());

		distributorResponse.setSecretId(Encryption.decryptForFrontEndDataKMS(distributorDetails.getSecretId()));
		distributorResponse.setSupportEmailId(distributorDetails.getSupportEmailId());
		distributorResponse.setSupportPhoneNo(distributorDetails.getSupportPhoneNo());
		distributorResponse.setTr_mail_flag(distributorDetails.getTr_mail_flag());

		return distributorResponse;
	}

	// Find all distributors
	public DistributorDetailInformationsResponse findAllDistributorDetailsInformation(String uuid)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);

		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<DistributorDetails> listOfDistributorDetails = distributorDetailsRepository.findAll();

		DistributorDetailInformationsResponse distributorDetailInformationsResponse = new DistributorDetailInformationsResponse(
				HttpStatus.SC_OK, "DistributorDetail Informations fetched successfully!.", listOfDistributorDetails);

		return distributorDetailInformationsResponse;
	}

	@Transactional
	public DistributorMerchantDetailsResponse associateDistributorMerchantDetails(String merchantID,
			String distributorID, String uuid) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByuuid(uuid);

		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantID);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXIST, FormValidationExceptionEnums.MERCHNT_NOT_EXIST);
		}

		DistributorDetails distributorDetails = distributorDetailsRepository.findByDistributorID(distributorID);
		if (distributorDetails == null) {
			throw new ValidationExceptions(DISTRIBUTOR_NOT_EXIST, FormValidationExceptionEnums.DISTRIBUTOR_NOT_EXIST);
		}

		DistributorMerchantDetails distributorMerchantDetailsAssociated = distributorMerchantAssociationDetailsRepository
				.findByDistributorIDAndMerchantID(distributorID, merchantID);
		if (distributorMerchantDetailsAssociated != null) {
			throw new ValidationExceptions(MERCHANT_DISTRIBUTOR_ASSOCIATION_ALREADY_EXIST,
					FormValidationExceptionEnums.MERCHANT_DISTRIBUTOR_ASSOCIATION_ALREADY_EXIST);
		}
		DistributorMerchantDetails distributorMerchantDetails = new DistributorMerchantDetails();
		distributorMerchantDetails.setCreatedBy(uuid);
		distributorMerchantDetails.setUpdatedBy(uuid);
		distributorMerchantDetails.setDistributorID(distributorID);
		distributorMerchantDetails.setMerchantID(merchantID);
		distributorMerchantDetails.setUuid(UUID.randomUUID().toString());// set ne uuid here
		distributorMerchantDetails.setFlagValue(Boolean.TRUE);
		distributorMerchantDetails.setApproval(ApprovalStatus.APPROVED.toString());
		distributorMerchantDetails.setRegion("");
		distributorMerchantDetails.setRights("");
		distributorMerchantDetails.setStatus(UserStatus.ACTIVE.toString());
		distributorMerchantDetails = distributorMerchantAssociationDetailsRepository.save(distributorMerchantDetails);

		// response DTO
		DistributorMerchantDetailsResponse distributorMerchantDetailsResponse = new DistributorMerchantDetailsResponse();
		distributorMerchantDetailsResponse.setMessage("Association of Distributor Merchant is done successfully!.");
		distributorMerchantDetailsResponse.setStatus(HttpStatus.SC_OK);
		distributorMerchantDetailsResponse.setDistributorMerchantDetails(distributorMerchantDetails);
		return distributorMerchantDetailsResponse;
	}

	public DistributorMerchantDetailsInformationResponse findAllDistributorMerchantDetailsInformation(String uuid)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);

		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		// Implement Paging here for backpressure resultset
		List<DistributorMerchantDetails> listOfDistributorMerchantDetailInformations = distributorMerchantAssociationDetailsRepository
				.findAll();
		DistributorMerchantDetailsInformationResponse distributorMerchantDetailsInformationResponse = new DistributorMerchantDetailsInformationResponse();
		distributorMerchantDetailsInformationResponse.setStatus(HttpStatus.SC_OK);
		distributorMerchantDetailsInformationResponse
				.setMessage("Distributor Merchant Association Detail-Informations fetched successfully!.");
		distributorMerchantDetailsInformationResponse
				.setListOfDistributorMerchantDetails(listOfDistributorMerchantDetailInformations);

		return distributorMerchantDetailsInformationResponse;
	}

	/*
	 * public DistributorFromDistributorMerchantDetailsResponse
	 * findDistributorByMerchantID(String uuid,String merchantID) throws
	 * ValidationExceptions {
	 * 
	 * UserAdminDetails userAdminDetails =
	 * userAdminDetailsRepository.findByUuid(uuid);
	 * 
	 * if (userAdminDetails == null) { throw new
	 * ValidationExceptions(USER_NOT_FOUND,
	 * FormValidationExceptionEnums.USER_NOT_FOUND); }
	 * logger.info("User Validation done :: " + userAdminDetails.getEmailId());
	 * 
	 * 
	 * String distributorID =
	 * distributorMerchantAssociationDetailsRepository.findByMerchantID(merchantID);
	 * 
	 * // now find distributor details from table DistributorDetails
	 * distributorDetails =
	 * distributorDetailsRepository.findByDistributorID(distributorID); if
	 * (distributorDetails != null) {
	 * DistributorFromDistributorMerchantDetailsResponse
	 * distributorFromDistributorMerchantDetailsResponse = new
	 * DistributorFromDistributorMerchantDetailsResponse(HttpStatus.SC_OK,
	 * "Distributor Associated With A Merchant By MerchantID " + merchantID +
	 * " fetched successfully!",distributorDetails);
	 * 
	 * return distributorFromDistributorMerchantDetailsResponse; }
	 * DistributorFromDistributorMerchantDetailsResponse
	 * distributorFromDistributorMerchantDetailsResponse = new
	 * DistributorFromDistributorMerchantDetailsResponse();
	 * distributorFromDistributorMerchantDetailsResponse.setDistributorDetails(null)
	 * ; distributorFromDistributorMerchantDetailsResponse.
	 * setMessage("Distributor Associated With A Merchant By MerchantID " +
	 * merchantID + "not found in db!");
	 * distributorFromDistributorMerchantDetailsResponse.setStatus(HttpStatus.
	 * SC_NOT_FOUND); return distributorFromDistributorMerchantDetailsResponse; }
	 */

	public DistributorFromDistributorMerchantDetailsResponse findDistributorByMerchantID(String uuid, String merchantID)
			throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);

		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		String distributorID = distributorMerchantAssociationDetailsRepository.findByMerchantID(merchantID);

		// now find distributor details from table
		DistributorDetails listOfDistributorDetails = distributorDetailsRepository.findByDistributorID(distributorID);
		DistributorFromDistributorMerchantDetailsResponse res = new DistributorFromDistributorMerchantDetailsResponse();
		res.setDistributorDetails(listOfDistributorDetails);
		res.setMessage("Destributor details are");
		res.setStatus(HttpStatus.SC_OK);

		return res;
	}

	public AllMerchantsAssociatedWithADistributorByDistributorIDResponse findAllMerchantsAssociatedWithADistributorByDistributorID(
			String uuid, String distributorID) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		if (userAdminDetails == null) {
			throw new ValidationExceptions(USER_NOT_FOUND, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		List<MerchantDetails> listOfMerchantDetails = null;

		List<String> listOfMerchantID = distributorMerchantAssociationDetailsRepository
				.findAllByDistributorID(distributorID);

		// now find all merchants details from table
		if (listOfMerchantID != null) {
			listOfMerchantDetails = merchantDetailsRepository.findAllByMerchantID(listOfMerchantID);
		}

		AllMerchantsAssociatedWithADistributorByDistributorIDResponse allMerchantsAssociatedWithADistributorByDistributorIDResponse = new AllMerchantsAssociatedWithADistributorByDistributorIDResponse();
		allMerchantsAssociatedWithADistributorByDistributorIDResponse.setStatus(HttpStatus.SC_OK);
		allMerchantsAssociatedWithADistributorByDistributorIDResponse
				.setMessage("All Merchants Associated With A Distributor By DistributorID " + distributorID
						+ " fetched successfully!");

		allMerchantsAssociatedWithADistributorByDistributorIDResponse.setListOfMerchantDetails(listOfMerchantDetails);
		// make fields in repose dto class

		return allMerchantsAssociatedWithADistributorByDistributorIDResponse;
	}

	public UpdateDistributorDetails updateDistributorStatus(String uuid, String distributorID, String status) {

		// 1. find admin
		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		DistributorDetails distributorDetails = distributorDetailsRepository.findByDistributorID(distributorID);
		UpdateDistributorDetails updateDistributorDetails = null;
		if (distributorDetails != null) {

			if (status.equalsIgnoreCase("active") || status.equalsIgnoreCase("ACTIVE")) {
				distributorDetails.setUserStatus(UserStatus.ACTIVE.toString());
			}
			if (status.equalsIgnoreCase("blocked") || status.equalsIgnoreCase("BLOCKED")) {
				distributorDetails.setUserStatus(UserStatus.BLOCKED.toString());
			}
			if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("PENDING")) {
				distributorDetails.setUserStatus(status);
			}

			distributorDetails = distributorDetailsRepository.save(distributorDetails);
			updateDistributorDetails = new UpdateDistributorDetails();
			updateDistributorDetails
					.setMessage("Distributor with DistributorID " + distributorID + " Status is updated successfully");
			updateDistributorDetails.setStatus(HttpStatus.SC_OK);
			updateDistributorDetails.setAppID(distributorDetails.getAppID());
			updateDistributorDetails.setCompanyName(distributorDetails.getCompanyName());
			updateDistributorDetails.setDistributorEMail(distributorDetails.getDistributorEMail());
			updateDistributorDetails.setDistributorName(distributorDetails.getDistributorName());
			updateDistributorDetails.setDistributorType(distributorDetails.getDistributorType());
			updateDistributorDetails.setInitialPwdChange(distributorDetails.getInitialPwdChange());
			updateDistributorDetails.setKycStatus(distributorDetails.getKycStatus());
			updateDistributorDetails.setLogoUrl(distributorDetails.getLogoUrl());
			updateDistributorDetails.setPassword(distributorDetails.getPassword());
			updateDistributorDetails.setPhoneNumber(distributorDetails.getPhoneNumber());

			updateDistributorDetails.setSaltKey(distributorDetails.getSaltKey());
			updateDistributorDetails
					.setSecretId(Encryption.decryptForFrontEndDataKMS(distributorDetails.getSecretId()));

			updateDistributorDetails.setSupportEmailId(distributorDetails.getSupportEmailId());
			updateDistributorDetails.setSupportPhoneNo(distributorDetails.getSupportPhoneNo());
			updateDistributorDetails.setTr_mail_flag(distributorDetails.getTr_mail_flag());
			updateDistributorDetails.setUserStatus(distributorDetails.getUserStatus());
			updateDistributorDetails.setCreatedBy(distributorDetails.getCreatedBy());
			updateDistributorDetails.setUpdatedBy(distributorDetails.getUpdatedBy());
			;
			return updateDistributorDetails;
		} else {
			updateDistributorDetails = new UpdateDistributorDetails();
			updateDistributorDetails.setMessage("Distributor with DistributorID " + distributorID + " not Found in DB");
			updateDistributorDetails.setStatus(HttpStatus.SC_EXPECTATION_FAILED);

		}

		return updateDistributorDetails;

	}

	// issue1
	public DistributorMerchantDetailsResponse updateDistributorMerchantDetailsStatus(String uuid, String distributorID,
			String merchantID, String status) {

		// 1. find admin
		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		DistributorMerchantDetails distributorMerchantDetails = null;
		DistributorMerchantDetailsResponse distributorMerchantDetailsResponse = null;
		distributorMerchantDetails = distributorMerchantAssociationDetailsRepository
				.findByDistributorIDAndMerchantID(distributorID, merchantID);
		if (distributorMerchantDetails != null) {

			if (status.equalsIgnoreCase("active") || status.equalsIgnoreCase("ACTIVE")) {
				distributorMerchantDetails.setStatus(UserStatus.ACTIVE.toString());
			}
			if (status.equalsIgnoreCase("blocked") || status.equalsIgnoreCase("BLOCKED")) {
				distributorMerchantDetails.setStatus(UserStatus.BLOCKED.toString());
			}
			if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("PENDING")) {
				distributorMerchantDetails.setStatus(UserStatus.PENDING.toString());
			}
			// distributorMerchantDetails.setStatus(status);

			distributorMerchantDetails = distributorMerchantAssociationDetailsRepository
					.save(distributorMerchantDetails);

			distributorMerchantDetailsResponse = new DistributorMerchantDetailsResponse();
			distributorMerchantDetailsResponse.setDistributorMerchantDetails(distributorMerchantDetails);
			distributorMerchantDetailsResponse.setMessage("distributor Merchant Details is updated successfully");
			distributorMerchantDetailsResponse.setStatus(HttpStatus.SC_OK);
			return distributorMerchantDetailsResponse;
		} else {
			distributorMerchantDetailsResponse = new DistributorMerchantDetailsResponse();
			distributorMerchantDetailsResponse.setDistributorMerchantDetails(distributorMerchantDetails);
			distributorMerchantDetailsResponse.setMessage("distributor Merchant Details Not Found in DB");
			distributorMerchantDetailsResponse.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
		return distributorMerchantDetailsResponse;
	}

	public MerchantDetailsStatusUpdateResponse updateMerchantStatus(String uuid, String merchantID, String status)
			throws ValidationExceptions {
		// 1. find admin
		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		MerchantDetailsStatusUpdateResponse merchantDetailsStatusUpdateResponse = null;
		MerchantDetails merchantDetails = null;
		List<MerchantPGDetails> listOfMerchantPGDetails = null;
		List<MerchantPGServices> listOfMerchantPGServices = null;

		merchantDetails = merchantDetailsRepository.findByMerchantID(merchantID);
		if (merchantDetails != null) {

			if (status.equalsIgnoreCase("active")) {
				merchantDetails.setUserStatus(UserStatus.ACTIVE.toString());
			}
			if (status.equalsIgnoreCase("blocked")) {
				merchantDetails.setUserStatus(UserStatus.BLOCKED.toString());
			}
			if (status.equalsIgnoreCase("pending")) {
				merchantDetails.setUserStatus(UserStatus.PENDING.toString());
			}

			merchantDetails = merchantDetailsRepository.save(merchantDetails);

			// also update same status in MerchantPGDetails and MerchantPGServiceDetails as
			// in if MerchantDetails

			listOfMerchantPGDetails = merchantPGDetailsRepository.findAllByMerchantID(merchantID);
			List<MerchantPGDetails> listMPG = new ArrayList<>();
			if (listOfMerchantPGDetails != null) {
				for (MerchantPGDetails lMPG : listOfMerchantPGDetails) {

					lMPG.setStatus(status);
					lMPG.setUpdatedBy(uuid);
					lMPG.setUpdated(new Date());
					listMPG.add(lMPG);
				}
				merchantPGDetailsRepository.saveAll(listMPG);
				System.out.println("merchantPGDetailsRepository.saveAll(listMPG);");
			} else {
				throw new ValidationExceptions(MERCHANT_PG_CONFIG_NOT_FOUND,
						FormValidationExceptionEnums.MERCHANT_PG_CONFIG_NOT_FOUND);
			}

			listOfMerchantPGServices = merchantPGServicesRepository.findAllByMerchantID(merchantID);
			List<MerchantPGServices> listMPGService = new ArrayList<>();
			if (listOfMerchantPGServices != null) {

				for (MerchantPGServices lMPGService : listOfMerchantPGServices) {
					lMPGService.setStatus(status);
					lMPGService.setUpdatedBy(uuid);
					lMPGService.setUpdated(new Date());
					listMPGService.add(lMPGService);

				}
				merchantPGServicesRepository.saveAll(listMPGService);
				System.out.println("merchantPGServicesRepository.saveAll(listMPGService)");
			} else {
				throw new ValidationExceptions(MERCHANT_PG_SERVICE_NOT_ASSOCIATED,
						FormValidationExceptionEnums.MERCHANT_PG_SERVICE_NOT_ASSOCIATED);
			}

			merchantDetailsStatusUpdateResponse = new MerchantDetailsStatusUpdateResponse();
			// merchantDetailsStatusUpdateResponse.setMerchantDetails(merchantDetails);

			merchantDetailsStatusUpdateResponse.setAppId(merchantDetails.getAppID());
			merchantDetailsStatusUpdateResponse.setCreatedBy(merchantDetails.getCreatedBy());
			merchantDetailsStatusUpdateResponse.setUpdatedBy(merchantDetails.getCreatedBy());
			merchantDetailsStatusUpdateResponse.setUuid(merchantDetails.getUuid());
			merchantDetailsStatusUpdateResponse.setEmailId(merchantDetails.getMerchantEmail());
			merchantDetailsStatusUpdateResponse.setKycStatus(merchantDetails.getKycStatus());
			merchantDetailsStatusUpdateResponse.setMerchantId(merchantDetails.getMerchantID());
			merchantDetailsStatusUpdateResponse.setMerchantName(merchantDetails.getMerchantName());
			merchantDetailsStatusUpdateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
			merchantDetailsStatusUpdateResponse
					.setSecretId(Encryption.decryptForFrontEndDataKMS(merchantDetails.getSecretId()));// Decrypt it then
			// send
			merchantDetailsStatusUpdateResponse.setMerchantStatus(merchantDetails.getUserStatus());
			merchantDetailsStatusUpdateResponse.setUserStatus(merchantDetails.getUserStatus());
			merchantDetailsStatusUpdateResponse.setCompanyName(merchantDetails.getCompanyName());
			merchantDetailsStatusUpdateResponse.setSupportEmailId(merchantDetails.getSupportEmailId());
			merchantDetailsStatusUpdateResponse.setSupportPhoneNo(merchantDetails.getSupportPhoneNo());
			merchantDetailsStatusUpdateResponse.setMerchantType(merchantDetails.getMerchantType());
			merchantDetailsStatusUpdateResponse.setPermenantLink(merchantDetails.getPermenantLink());
			merchantDetailsStatusUpdateResponse.setMessage("Merchant with ID " + merchantID
					+ " Status updated successfully And all MerchantPGDetails And all MerchantPGServices belongs to This Merchant with ID "
					+ merchantID + " also updated with Status: " + status);
			merchantDetailsStatusUpdateResponse.setListOfMerchantPGDetails(listOfMerchantPGDetails);
			merchantDetailsStatusUpdateResponse.setListOfMerchantPGServices(listOfMerchantPGServices);
			merchantDetailsStatusUpdateResponse.setStatus(HttpStatus.SC_OK);

			return merchantDetailsStatusUpdateResponse;
		} else {
			merchantDetailsStatusUpdateResponse = new MerchantDetailsStatusUpdateResponse();
			// merchantDetailsStatusUpdateResponse.setMerchantDetails(merchantDetails);
			merchantDetailsStatusUpdateResponse.setMessage("Merchant with ID " + merchantID + " Not Found in DB");
			merchantDetailsStatusUpdateResponse.setStatus(HttpStatus.SC_EXPECTATION_FAILED);

		}
		return merchantDetailsStatusUpdateResponse;
	}

	@Transactional
	public DistributorMerchantDetailsResponse updateDistributorMerchantAssociationDetails(String uuid,
			String distributorID, String merchantID,
			UpdateDistributorMerchantAssociationDetails updateDistributorMerchantAssociationDetails) {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		DistributorMerchantDetailsResponse distributorMerchantDetailsResponse = null;
		DistributorMerchantDetails distributorMerchantDetails = null;
		distributorMerchantDetails = distributorMerchantAssociationDetailsRepository
				.findByDistributorIDAndMerchantID(distributorID, merchantID);
		if (distributorMerchantDetails != null) {
			distributorMerchantDetails.setApproval(updateDistributorMerchantAssociationDetails.getApproval());
			distributorMerchantDetails.setFlagValue(updateDistributorMerchantAssociationDetails.getFlagValue());
			distributorMerchantDetails.setRegion(updateDistributorMerchantAssociationDetails.getRegion());
			distributorMerchantDetails.setRights(updateDistributorMerchantAssociationDetails.getRights());
			distributorMerchantDetails.setStatus(updateDistributorMerchantAssociationDetails.getStatus());
			distributorMerchantDetails = distributorMerchantAssociationDetailsRepository
					.save(distributorMerchantDetails);

			distributorMerchantDetailsResponse = new DistributorMerchantDetailsResponse();
			distributorMerchantDetailsResponse.setDistributorMerchantDetails(distributorMerchantDetails);
			distributorMerchantDetailsResponse.setMessage("DistributorMerchantAssociation where distributor id "
					+ distributorID + " and merchant id " + merchantID + "is successfully updated ");
			distributorMerchantDetailsResponse.setStatus(HttpStatus.SC_OK);

			return distributorMerchantDetailsResponse;
		} else {
			distributorMerchantDetailsResponse = new DistributorMerchantDetailsResponse();
			distributorMerchantDetailsResponse.setDistributorMerchantDetails(distributorMerchantDetails);
			distributorMerchantDetailsResponse.setMessage("DistributorMerchantAssociation NOT Found in DB");
			distributorMerchantDetailsResponse.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
		return distributorMerchantDetailsResponse;
	}

	@Transactional
	public UpdateDistributorDetailsResponse updateDistributorDetails(String uuid, String distributorID,
			UpdateDistributorDetails updateDistributorDetails) throws ValidationExceptions {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByUuid(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		DistributorDetails distributorDetails = null;
		UpdateDistributorDetailsResponse updateDistributorDetailsResponse = null;

		distributorDetails = distributorDetailsRepository
				.findByDistributorEMail(updateDistributorDetails.getDistributorEMail());
		if (distributorDetails != null) {

			throw new ValidationExceptions(EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.EMAIL_ALREADY_EXISTS);
		}
		distributorDetails = distributorDetailsRepository.findByPhoneNumber(updateDistributorDetails.getPhoneNumber());
		if (distributorDetails != null) {
			throw new ValidationExceptions(DISTRIBUTOR_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.DISTRIBUTOR_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM);
		}
		if (!Validator.isValidEmail(updateDistributorDetails.getDistributorEMail())) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidPhoneNumber(updateDistributorDetails.getPhoneNumber())) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		if (!Validator.isValidEmail(updateDistributorDetails.getSupportEmailId())) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		if (!Validator.isValidEmail(updateDistributorDetails.getSupportPhoneNo())) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidWebUrl(updateDistributorDetails.getLogoUrl())) {
			throw new ValidationExceptions(INVALID_URL, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		if (updateDistributorDetails.getDistributorName().isEmpty()
				|| updateDistributorDetails.getDistributorName() == null
				|| updateDistributorDetails.getPhoneNumber().isEmpty()
				|| updateDistributorDetails.getPhoneNumber() == null
				|| updateDistributorDetails.getKycStatus().isEmpty() || updateDistributorDetails.getKycStatus() == null
				|| updateDistributorDetails.getDistributorEMail().isEmpty()
				|| updateDistributorDetails.getDistributorEMail() == null

				|| updateDistributorDetails.getUserStatus().isEmpty()
				|| updateDistributorDetails.getUserStatus() == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE,
					FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}

		distributorDetails = distributorDetailsRepository.findByDistributorID(distributorID);
		if (distributorDetails != null) {
			// distributorDetails.setAppID(updateDistributorDetails.getAppID());//ask

			distributorDetails.setCompanyName(updateDistributorDetails.getCompanyName());

			distributorDetails.setDistributorEMail(updateDistributorDetails.getDistributorEMail());
			distributorDetails.setDistributorName(updateDistributorDetails.getDistributorName());
			distributorDetails.setPhoneNumber(updateDistributorDetails.getPhoneNumber());
			if (updateDistributorDetails.getKycStatus().equalsIgnoreCase("y")
					|| updateDistributorDetails.getKycStatus().equalsIgnoreCase("yes")) {
				distributorDetails.setKycStatus(KycStatus.YES.toString());
			}
			if (updateDistributorDetails.getKycStatus().equalsIgnoreCase("n")
					|| updateDistributorDetails.getKycStatus().equalsIgnoreCase("no")) {
				distributorDetails.setKycStatus(KycStatus.NO.toString());
			}
			if (updateDistributorDetails.getKycStatus().equalsIgnoreCase("pending")
					|| updateDistributorDetails.getKycStatus().equalsIgnoreCase("PENDING")) {
				distributorDetails.setKycStatus(KycStatus.PENDING.toString());
			}
			if (updateDistributorDetails.getKycStatus().equalsIgnoreCase("rejected")
					|| updateDistributorDetails.getKycStatus().equalsIgnoreCase("REJECTED")) {
				distributorDetails.setKycStatus(KycStatus.REJECTED.toString());
			}

			distributorDetails.setDistributorType(updateDistributorDetails.getDistributorType());
			// distributorDetails.setInitialPwdChange(updateDistributorDetails.getInitialPwdChange());//ask
			// impl

			distributorDetails.setLogoUrl(updateDistributorDetails.getLogoUrl());
			// distributorDetails.setPassword(updateDistributorDetails.getPassword());

			// distributorDetails.setSaltKey(updateDistributorDetails.getSaltKey());//ask
			// distributorDetails.setSecretId(updateDistributorDetails.getSecretId());//ask
			distributorDetails.setSupportEmailId(updateDistributorDetails.getSupportEmailId());
			distributorDetails.setSupportPhoneNo(updateDistributorDetails.getSupportPhoneNo());
			distributorDetails.setTr_mail_flag(updateDistributorDetails.getTr_mail_flag());
			if (updateDistributorDetails.getUserStatus().equalsIgnoreCase("active")) {
				distributorDetails.setUserStatus(UserStatus.ACTIVE.toString());
			}
			if (updateDistributorDetails.getUserStatus().equalsIgnoreCase("blocked")) {
				distributorDetails.setUserStatus(UserStatus.BLOCKED.toString());
			}
			if (updateDistributorDetails.getUserStatus().equalsIgnoreCase("pending")) {
				distributorDetails.setUserStatus(UserStatus.PENDING.toString());
			}

			distributorDetails.setUpdated(new Date());
			distributorDetails.setUpdatedBy(uuid);
			// AppID,SaltKey,SecretId

			distributorDetails = distributorDetailsRepository.save(distributorDetails);
			updateDistributorDetailsResponse = new UpdateDistributorDetailsResponse();

			// updateDistributorDetailsResponse.setDistributorDetails(distributorDetails);

			// Encryption.decryptForFrontEndDataKMS(distributorDetails.getAppID())

			updateDistributorDetailsResponse
					.setAppId(Encryption.decryptForFrontEndDataKMS(distributorDetails.getAppID()));// fix it
			updateDistributorDetailsResponse.setSaltKey(distributorDetails.getSaltKey());
			updateDistributorDetailsResponse
					.setSecretId(Encryption.decryptForFrontEndDataKMS(distributorDetails.getSecretId()));

			updateDistributorDetailsResponse.setCompanyName(distributorDetails.getCompanyName());
			updateDistributorDetailsResponse.setCreatedBy(distributorDetails.getCreatedBy());
			updateDistributorDetailsResponse.setDistributorEMail(distributorDetails.getDistributorEMail());
			updateDistributorDetailsResponse.setDistributorId(distributorDetails.getDistributorID());
			updateDistributorDetailsResponse.setDistributorName(distributorDetails.getDistributorName());
			updateDistributorDetailsResponse.setDistributorType(distributorDetails.getDistributorType());
			updateDistributorDetailsResponse.setKycStatus(distributorDetails.getKycStatus());
			updateDistributorDetailsResponse.setLogoUrl(distributorDetails.getLogoUrl());
			updateDistributorDetailsResponse.setPhoneNumber(distributorDetails.getPhoneNumber());
			updateDistributorDetailsResponse.setSupportEmailId(distributorDetails.getSupportEmailId());
			updateDistributorDetailsResponse.setSupportPhoneNo(distributorDetails.getSupportPhoneNo());
			updateDistributorDetailsResponse.setTr_mail_flag(distributorDetails.getTr_mail_flag());
			updateDistributorDetailsResponse.setUpdatedBy(distributorDetails.getUpdatedBy());
			updateDistributorDetailsResponse.setUserStatus(distributorDetails.getUserStatus());
			updateDistributorDetailsResponse.setUuid(distributorDetails.getUuid());

			// Find all merchant associated from DistributorMerchantAssociationDeatils table
			// updateDistributorDetailsResponse.setMerchantDetails();

			updateDistributorDetailsResponse
					.setMessage("Details of Distributor with ID " + distributorID + " is updated successfully");
			updateDistributorDetailsResponse.setStatus(HttpStatus.SC_OK);

			return updateDistributorDetailsResponse;

		} else {
			updateDistributorDetailsResponse = new UpdateDistributorDetailsResponse();
			updateDistributorDetailsResponse.setMessage("Distributor not Found in DB");
			updateDistributorDetailsResponse.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}

		return updateDistributorDetailsResponse;

	}

	/** @author Modified By abhimanyu end **/

	public MerchantCreateResponse refreshSecretKey(String uuid) throws ValidationExceptions, NoSuchAlgorithmException {

		logger.info("merchantView In this Method.");
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		String secrecKey = Encryption.genSecretKey();
		String appId = Utility.generateAppId();

		merchantDetails.setAppID(Encryption.encryptCardNumberOrExpOrCvvKMS(appId));
		merchantDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvvKMS(secrecKey));
		merchantDetailsRepository.save(merchantDetails);

		merchantCreateResponse.setAppId(appId);
		merchantCreateResponse.setEmailId(merchantDetails.getMerchantEmail());
		// merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(secrecKey);

		return merchantCreateResponse;
	}

	public void merchantDefaultPgAssociation(MerchantDetails merchantsDetails) throws ValidationExceptions {
		MerchantPGDetails merchantPGDetails = null;
		List<MerchantPGServices> listOfMerchantPGServices = null;
		MerchantPGServices merchantPGServices = null;

		for (PGServiceDetails pgServiceDetails : pgServiceDetailsRepository.findAllByDefaultService("Y")) {
			PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
					.findByPgUuid(pgServiceDetails.getPgId());

			merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(
					merchantsDetails.getMerchantID(), pgConfigurationDetails.getPgUuid());
			if (merchantPGDetails == null) {
				// Merchant PG Association
				merchantPGDetails = new MerchantPGDetails();
				merchantPGDetails.setCreatedBy(merchantsDetails.getCreatedBy());
				merchantPGDetails.setMerchantID(merchantsDetails.getMerchantID());
				merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
				merchantPGDetails.setMerchantPGId(pgConfigurationDetails.getPgUuid());
				merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
				merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey());
				merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
				// merchantPGDetails.setReason("Default PG Configuration");
				merchantPGDetails.setReason("ACTIVE");
				merchantPGDetails.setStatus(ApprovalStatus.APPROVED.toString());
				merchantPGDetailsRepository.save(merchantPGDetails);
			}

			listOfMerchantPGServices = merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(
					merchantsDetails.getMerchantID(), pgConfigurationDetails.getPgUuid(),
					pgServiceDetails.getPgServices());

			if (listOfMerchantPGServices == null) {
				merchantPGServices = new MerchantPGServices();
				merchantPGServices.setCreatedBy(merchantsDetails.getCreatedBy());
				merchantPGServices.setMerchantID(merchantsDetails.getMerchantID());
				merchantPGServices.setProcessedBy(merchantsDetails.getCreatedBy());

				if (pgConfigurationDetails.getStatus().equalsIgnoreCase("ACTIVE")
						&& pgServiceDetails.getStatus().equalsIgnoreCase("ACTIVE")) {
					merchantPGServices.setPgID(pgConfigurationDetails.getPgUuid());

					merchantPGServices.setService(pgServiceDetails.getPgServices());
					merchantPGServices.setStatus("ACTIVE");
					// merchantPGServices.setStatus(ApprovalStatus.NEW.toString());
					merchantPGServices = merchantPGServicesRepository.save(merchantPGServices);
				} else {

					// PG OR PG-Service is not Active
					throw new ValidationExceptions(PG_NOT_ACTIVE_OR_PGSERVICE_NOT_ACTIVE,
							FormValidationExceptionEnums.PG_NOT_ACTIVE_OR_PGSERVICE_NOT_ACTIVE);
				}

			}

		}
	}

	public void merchantPgAssociation(MerchantDetails merchantsDetails, String pgId, String serviceFlag)
			throws ValidationExceptions {
		MerchantPGDetails merchantPGDetails = null;
		List<MerchantPGServices> listOfMerchantPGServices = null;
		MerchantPGServices merchantPGServices = null;

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgId);

		merchantPGDetails = merchantPGDetailsRepository
				.findByMerchantIDAndMerchantPGId(merchantsDetails.getMerchantID(), pgConfigurationDetails.getPgUuid());
		if (merchantPGDetails == null) {
			// Merchant PG Association
			merchantPGDetails = new MerchantPGDetails();
			merchantPGDetails.setCreatedBy(merchantsDetails.getCreatedBy());
			merchantPGDetails.setMerchantID(merchantsDetails.getMerchantID());
			merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
			merchantPGDetails.setMerchantPGId(pgConfigurationDetails.getPgUuid());
			merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
			merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey());
			merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
			// merchantPGDetails.setReason("Default PG Configuration");
			merchantPGDetails.setReason("ACTIVE");
			merchantPGDetails.setStatus(ApprovalStatus.APPROVED.toString());
			merchantPGDetailsRepository.save(merchantPGDetails);

			if (serviceFlag.toLowerCase().equals("true")) {
				for (PGServiceDetails pgServiceDetails : pgServiceDetailsRepository.findByPgId(pgId)) {
					listOfMerchantPGServices = merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(
							merchantsDetails.getMerchantID(), pgConfigurationDetails.getPgUuid(),
							pgServiceDetails.getPgServices());

					if (listOfMerchantPGServices == null) {
						merchantPGServices = new MerchantPGServices();
						merchantPGServices.setCreatedBy(merchantsDetails.getCreatedBy());
						merchantPGServices.setMerchantID(merchantsDetails.getMerchantID());
						merchantPGServices.setProcessedBy(merchantsDetails.getCreatedBy());

						if (pgConfigurationDetails.getStatus().equalsIgnoreCase("ACTIVE")
								&& pgServiceDetails.getStatus().equalsIgnoreCase("ACTIVE")) {
							merchantPGServices.setPgID(pgConfigurationDetails.getPgUuid());

							merchantPGServices.setService(pgServiceDetails.getPgServices());
							merchantPGServices.setStatus(ApprovalStatus.NEW.toString());
							merchantPGServices = merchantPGServicesRepository.save(merchantPGServices);
						}
					}
				}
			}

		} else {

			// PG OR PG-Service is not Active
			throw new ValidationExceptions(PG_NOT_ACTIVE_OR_PGSERVICE_NOT_ACTIVE,
					FormValidationExceptionEnums.PG_NOT_ACTIVE_OR_PGSERVICE_NOT_ACTIVE);
		}

	}

	public Object getPgDetails() {

		return pgConfigurationDetailsRepository.getAllPgDetails();
	}

	public List<PGConfigurationDetails> getAllPGDetails() {

		return pgConfigurationDetailsRepository.findAll();
	}

	public Object getPgServiceList() {

		List<PGServices> list = new ArrayList<>();
		PGServices[] res = PGServices.values();
		for (PGServices l : res) {
			list.add(l);
		}

		return list;
	}

	public SuccessResponseDto getAllPgDetails() {

		List<PGConfigurationDetails> pgConfigurationDetails = pgConfigurationDetailsRepository.findAll();

		List<AllPgDetailsResponse> allpglist = new ArrayList<>();

		for (PGConfigurationDetails pgconf : pgConfigurationDetails) {
			Pgdetails pgdet = new Pgdetails();
			AllPgDetailsResponse allPg = new AllPgDetailsResponse();

			pgdet.setPgAppId(pgconf.getPgAppId());
			pgdet.setStatus(pgconf.getStatus());
			pgdet.setPgId(pgconf.getPgUuid());

			allPg.setCreated(pgconf.getCreatedAt().toString());
			allPg.setPgname(pgconf.getPgName());
			allPg.setPgdetails(pgdet);

			List<PGServiceDetails> pgService = pgServiceDetailsRepository.findByPgId(pgconf.getPgUuid());
			List<Pgservices> serList = new ArrayList<Pgservices>();

			if (pgService.size() > 0) {

				for (PGServiceDetails pgs : pgService) {

					Pgservices pgse = new Pgservices();
					pgse.setServicename(pgs.getPgServices());

					ServiceDetails srcdet = new ServiceDetails();
					srcdet.setDefaultService(pgs.getDefaultService());
					srcdet.setThresholdDay(pgs.getThresoldDay());
					srcdet.setPriority(pgs.getPriority());
					srcdet.setThresholdMonth(pgs.getThresoldMonth());
					srcdet.setThresholdSixMonths(pgs.getThresold6Month());
					srcdet.setThresholdThreeMonths(pgs.getThresold3Month());
					srcdet.setThresholdWeek(pgs.getThresoldWeek());
					srcdet.setThresholdYear(pgs.getThresoldYear());

					pgse.setServiceDetails(srcdet);

					serList.add(pgse);

				}
			}

			allPg.setPgservices(serList);
			allpglist.add(allPg);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetails", allpglist);
		return sdto;
	}

	// api/createPGServices
	public PGServiceDetails createPgServices(String pgUuid, String pgServices, String userId, String defaultTag,
			String thresoldMonth, String thresoldDay, String thresoldWeek, String thresold3Month, String thresold6Month,
			String thresoldYear) throws ValidationExceptions {

		PGServiceDetails pgServiceDetails = null;
		PGConfigurationDetails pgConfigurationDetails = null;

		if (pgUuid.isEmpty() || pgUuid == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (pgServices.isEmpty() || pgServices == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (userId.isEmpty() || userId == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		if (defaultTag.isEmpty() || defaultTag == null) {
			throw new ValidationExceptions(INPUT_EMPTY_NULL, FormValidationExceptionEnums.INPUT_EMPTY_NULL);
		}
		// validate long values IS REAMING

		if (defaultTag.equalsIgnoreCase("Y") || defaultTag.equalsIgnoreCase("N")) {

		} else {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.containsEnum(PGServices.class, pgServices)) {
			throw new ValidationExceptions(PG_SERVICE_NOT_FOUND, FormValidationExceptionEnums.PG_SERVICE_NOT_FOUND);
		}

		pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {

			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);

		}
		// PGServiceDetails pgServiceDetails =
		// pgServiceDetailsRepository.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getPgUuid()),
		// pgServices.toUpperCase());
		pgServiceDetails = pgServiceDetailsRepository.findByPgIdAndPgServices(pgUuid, pgServices.toUpperCase());

		/*****/

		if (pgServiceDetails != null) {
			// error already PG Service Association found
			throw new ValidationExceptions(PG_SERVICE_PRESENT, FormValidationExceptionEnums.PG_SERVICE_PRESENT);
		} else {
			// Do PG Association
			if (defaultTag.toUpperCase().equalsIgnoreCase("Y")) {
				// Find out If input-Service has already set as Y
				PGServiceDetails pgServiceDetailsDefault = pgServiceDetailsRepository
						.findByPgServicesAndDefaultService(pgServices.toUpperCase(), defaultTag.toUpperCase());
				if (pgServiceDetailsDefault != null) {
					// Only One service can have Y value, else error
					throw new ValidationExceptions(PG_DEFAULT_SERVICE_SCOPE,
							FormValidationExceptionEnums.PG_DEFAULT_SERVICE_SCOPE);
				}
			}
			// If N true
			pgServiceDetails = new PGServiceDetails();
			pgServiceDetails.setPgId(pgConfigurationDetails.getPgUuid());
			pgServiceDetails.setPgServices(pgServices.toUpperCase());
			pgServiceDetails.setStatus(UserStatus.ACTIVE.toString());// Set a Active
			pgServiceDetails.setUpdatedBy(userId);
			pgServiceDetails.setCreatedBy(userId);
			// Setting N
			pgServiceDetails.setDefaultService(String.valueOf(defaultTag));
			pgServiceDetails.setThresoldMonth(Utility.StringToLong(thresoldMonth));
			pgServiceDetails.setThresoldDay(Utility.StringToLong(thresoldDay));
			pgServiceDetails.setThresoldWeek(Utility.StringToLong(thresoldWeek));
			pgServiceDetails.setThresold3Month(Utility.StringToLong(thresold3Month));
			pgServiceDetails.setThresold6Month(Utility.StringToLong(thresold6Month));
			pgServiceDetails.setThresoldYear(Utility.StringToLong(thresoldYear));

			pgServiceDetails.setPriority(PGSERVICEPRIORITY.HIGH.getValue());
			PGServiceThresoldCalculation pgServiceThresoldCalculation = pgServiceThresoldCalculationRepository
					.findByPgIdAndServiceType(pgConfigurationDetails.getPgUuid(), pgServices.toUpperCase());
			if (pgServiceThresoldCalculation == null) {
				pgServiceThresoldCalculation = new PGServiceThresoldCalculation();

				pgServiceThresoldCalculation.setCreatedBy(userId);
				pgServiceThresoldCalculation.setDaywiseAmount(Utility.StringToLong(thresoldDay));
				pgServiceThresoldCalculation.setWeekwiseAmount(Utility.StringToLong(thresoldWeek));
				pgServiceThresoldCalculation.setMonthwiseAmount(Utility.StringToLong(thresoldMonth));
				pgServiceThresoldCalculation.setMonth3wiseAmount(Utility.StringToLong(thresold3Month));
				pgServiceThresoldCalculation.setMonth6wiseAmount(Utility.StringToLong(thresold6Month));

				pgServiceThresoldCalculation.setYearwiseAmount(Utility.StringToLong(thresoldYear));
				pgServiceThresoldCalculation.setPgId(pgConfigurationDetails.getPgUuid());
				;
				pgServiceThresoldCalculation.setServiceType(pgServices.toUpperCase());
				;
				pgServiceThresoldCalculation.setUpdated(new Date());
				;
				;

				pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation);
			}

			pgServiceDetails = pgServiceDetailsRepository.save(pgServiceDetails);
		}
		/*****/

		/*
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * if (pgServiceDetails != null) {
		 * pgServiceDetails.setPgServices(pgServices.toUpperCase());
		 * pgServiceDetails.setStatus(UserStatus.PENDING.toString());
		 * pgServiceDetails.setUpdatedBy(userId);
		 * pgServiceDetails.setDefaultService(String.valueOf(defaultTag));
		 * pgServiceDetails.setThresoldMonth(Utility.StringToLong(thresoldYear));
		 * pgServiceDetails.setThresoldDay(Utility.StringToLong(thresoldDay));
		 * pgServiceDetails.setThresoldWeek(Utility.StringToLong(thresoldWeek));
		 * pgServiceDetails.setThresold3Month(Utility.StringToLong(thresold3Month));
		 * pgServiceDetails.setThresold6Month(Utility.StringToLong(thresold6Month));
		 * pgServiceDetails.setThresoldYear(Utility.StringToLong(thresoldYear));
		 * PGServiceThresoldCalculation pgServiceThresoldCalculation =
		 * pgServiceThresoldCalculationRepository.findByPgIdAndServiceType(
		 * pgConfigurationDetails.getPgUuid(), pgServices.toUpperCase()); if
		 * (pgServiceThresoldCalculation == null) {
		 * 
		 * pgServiceThresoldCalculation = new PGServiceThresoldCalculation();
		 * pgServiceThresoldCalculation.setPgId(pgConfigurationDetails.getPgUuid());
		 * pgServiceThresoldCalculation.setServiceType(pgServices.toUpperCase());
		 * pgServiceThresoldCalculation.setCreatedBy(userId);
		 * pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation); }
		 * else {
		 * pgServiceThresoldCalculation.setPgId(pgConfigurationDetails.getPgUuid());
		 * pgServiceThresoldCalculation.setServiceType(pgServices.toUpperCase());
		 * pgServiceThresoldCalculation.setCreatedBy(userId);
		 * pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation);
		 * 
		 * }
		 * 
		 * } else { if (defaultTag.toUpperCase().equalsIgnoreCase("Y")) {
		 * PGServiceDetails pgServiceDetailsDefault =
		 * pgServiceDetailsRepository.findByPgServicesAndDefaultService(pgServices.
		 * toUpperCase(), defaultTag.toUpperCase()); if (pgServiceDetailsDefault !=
		 * null) { throw new
		 * ValidationExceptions(PG_DEFAULT_SERVICE_SCOPE,FormValidationExceptionEnums.
		 * PG_DEFAULT_SERVICE_SCOPE); } } //If N true pgServiceDetails = new
		 * PGServiceDetails();
		 * pgServiceDetails.setPgId(pgConfigurationDetails.getPgUuid());
		 * pgServiceDetails.setPgServices(pgServices.toUpperCase());
		 * pgServiceDetails.setStatus(UserStatus.PENDING.toString());
		 * pgServiceDetails.setUpdatedBy(userId); pgServiceDetails.setCreatedBy(userId);
		 * pgServiceDetails.setDefaultService(String.valueOf(defaultTag));
		 * pgServiceDetails.setThresoldMonth(Utility.StringToLong(thresoldMonth));
		 * pgServiceDetails.setThresoldDay(Utility.StringToLong(thresoldDay));
		 * pgServiceDetails.setThresoldWeek(Utility.StringToLong(thresoldWeek));
		 * pgServiceDetails.setThresold3Month(Utility.StringToLong(thresold3Month));
		 * pgServiceDetails.setThresold6Month(Utility.StringToLong(thresold6Month));
		 * pgServiceDetails.setThresoldYear(Utility.StringToLong(thresoldYear));
		 * 
		 * pgServiceDetails.setPriority(PGSERVICEPRIORITY.HIGH.getValue());
		 * PGServiceThresoldCalculation pgServiceThresoldCalculation =
		 * pgServiceThresoldCalculationRepository.findByPgIdAndServiceType(
		 * pgConfigurationDetails.getPgUuid(), pgServices.toUpperCase()); if
		 * (pgServiceThresoldCalculation == null) { pgServiceThresoldCalculation = new
		 * PGServiceThresoldCalculation();
		 * pgServiceThresoldCalculation.setPgId(pgConfigurationDetails.getPgUuid());
		 * pgServiceThresoldCalculation.setServiceType(pgServices.toUpperCase());
		 * pgServiceThresoldCalculation.setCreatedBy(userId);
		 * pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation); }
		 * }
		 */

		return pgServiceDetails;
	}

	public void userLogout(LogoutRequestDto dto, UserAdminDetails user) throws UserException, ValidationExceptions {
		if (!(user.getUserSession().getSessionToken()).equals(dto.getSessionToken())) {
			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		user.getUserSession().setSessionStatus(0);
		// Log4jLogger.saveLog("User logout success==> " + user.toString());
		userAdminDetailsRepository.save(user);
	}

	public SuccessResponseDto merchantStatusAdmin(UserAdminDetails user) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantDetailsAddRepository.countTotalMerchantDetailsByUserStatusAndCreatedBy(user.getUuid()));
		return sdto;
	}

	public SuccessResponseDto merchantDashboardDets(UserAdminDetails user) {

		MerchantDashboardDet dash = new MerchantDashboardDet();
		String totalMer = merchantDetailsRepository.findTotalNoOfMerchants();
		dash.setTotalMerchants(totalMer);
		List<IMerchantStatus> res = merchantDetailsAddRepository.countAllTotalMerchantDetailsByUserStatu();
		dash.setTotalMerchantStatus(res);
		String adminMer = merchantDetailsAddRepository.countTotalMerchantDetailsByCreatedBy(user.getUuid());
		dash.setMerchantByAdmin(adminMer);
		List<IMerchantStatus> adminMerStatus = merchantDetailsAddRepository
				.countTotalMerchantDetailsByUserStatusAndCreatedBy(user.getUuid());
		dash.setAdminMerchantStatus(adminMerStatus);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", dash);
		return sdto;
	}

	/*
	 * public SuccessResponseDto merchantStatusList(UserAdminDetails user) {
	 * 
	 * SuccessResponseDto sdto = new SuccessResponseDto();
	 * sdto.getMsg().add("Request Processed Successfully !");
	 * sdto.setSuccessCode(SuccessCode.API_SUCCESS);
	 * sdto.getExtraData().put("merchantDetails",
	 * merchantDetailsAddRepository.getCompleteMerchantList(user.getUuid())); return
	 * sdto; }
	 */

	/*
	 * public MerchantDetails merchantByName(String merchantName) {
	 * 
	 * return merchantDetailsRepository.findByMerchantName(merchantName); }
	 */

	public Object merchantStatusTransactionLastDay(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantDetailsAddRepository.getYesterdayTrDetails(userAdminDetails.getUuid()));
		return sdto;
	}

	public Object merchantStatusTransactionDurationWithDate(UserAdminDetails userAdminDetails, String start_date,
			String end_date) throws ValidationExceptions {
		if (txnParam(start_date) == true && txnParam(end_date) == true) {
			dateWiseValidation(start_date, end_date);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantDetailsAddRepository.getDateTrDetails(userAdminDetails.getUuid(), start_date, end_date));
		return sdto;
	}

	public Object merchantStatusTransactionToday(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantDetailsAddRepository.getTodayTrDetails(userAdminDetails.getUuid()));
		return sdto;
	}

	public Object merchantStatusTransactionCurrMonth(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantDetailsAddRepository.getCurrMonthTrDetails(userAdminDetails.getUuid()));
		return sdto;
	}

	public Object merchantStatusTransactionLastMonth(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantDetailsAddRepository.getLastMonthTrDetails(userAdminDetails.getUuid()));
		return sdto;
	}

	public SuccessResponseDto updatMerchantStatus(String uuid, String merchantId, String statusUpdate)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails payUser = payoutApiUserDetailsRepo.findByMerchantId(merchantId);

		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			merchantDetails.setUserStatus(UserStatus.ACTIVE.toString());
		} else {
			merchantDetails.setUserStatus(UserStatus.BLOCKED.toString());
		}

		if (payUser != null) {
			if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
				payUser.setMerchantStatus(UserStatus.ACTIVE.toString());
			} else {
				payUser.setMerchantStatus(UserStatus.BLOCKED.toString());
			}
		}

		merchantDetails.setUserStatus(statusUpdate);
		merchantDetailsRepository.save(merchantDetails);
		payoutApiUserDetailsRepo.save(payUser);

		String secrectKey = Encryption.decryptForFrontEndDataKMS(merchantDetails.getSecretId());

		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		merchantCreateResponse.setAppId(merchantDetails.getAppID());
		merchantCreateResponse.setEmailId(merchantDetails.getMerchantEmail());
		merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(secrectKey);
		merchantCreateResponse.setMerchantStatus(merchantDetails.getUserStatus());
		merchantCreateResponse.setCompanyName(merchantDetails.getCompanyName());
		merchantCreateResponse.setSupportEmailId(merchantDetails.getSupportEmailId());
		merchantCreateResponse.setSupportPhoneNo(merchantDetails.getSupportPhoneNo());
		merchantCreateResponse.setMerchantType(merchantDetails.getMerchantType());
		merchantCreateResponse.setPermenantLink(merchantDetails.getPermenantLink());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantCreateResponse);
		return sdto;
	}

	public SuccessResponseDto updateMerchantPGDetailsStatus(String merchantId, String pgUuid, String statusUpdate)
			throws ValidationExceptions {

		List<MerchantPGServices> updateMerchantPGServices = new ArrayList<MerchantPGServices>();

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (merchantDetails.getUserStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantId,
				pgUuid);

		if (merchantPGDetails == null) {
			throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_EXISTS,
					FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_EXISTS);

		}

		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
		} else {
			merchantPGDetails.setStatus(UserStatus.BLOCKED.toString());
		}

		if (!merchantPGDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			List<MerchantPGServices> listMerchantPGService = merchantPGServicesRepository
					.findByMerchantIDAndPgID(merchantId, merchantPGDetails.getMerchantPGId());
			for (MerchantPGServices merchantPGServices : listMerchantPGService) {
				merchantPGServices.setStatus(merchantPGDetails.getStatus());
				updateMerchantPGServices.add(merchantPGServices);
			}
			merchantPGServicesRepository.saveAll(updateMerchantPGServices);
		}

		merchantPGDetailsRepository.save(merchantPGDetails);

		String secret = Encryption.decryptForFrontEndDataKMS(merchantPGDetails.getMerchantPGSecret());
		MerchantPgDetailRes res = new MerchantPgDetailRes();
		res.setId(merchantPGDetails.getId());
		res.setMerchantID(merchantPGDetails.getMerchantID());
		res.setMerchantPGAppId(merchantPGDetails.getMerchantPGAppId());
		res.setMerchantPGId(merchantPGDetails.getMerchantPGId());
		res.setMerchantPGSecret(secret);
		res.setMerchantPGSaltKey(merchantPGDetails.getMerchantPGSaltKey());
		res.setStatus(merchantPGDetails.getStatus());
		res.setMerchantPGId(merchantPGDetails.getMerchantPGId());
		res.setMerchantPGName(merchantPGDetails.getMerchantPGName());
		res.setMerchantPGAdd1(merchantPGDetails.getMerchantPGAdd1());
		res.setMerchantPGAdd2(merchantPGDetails.getMerchantPGAdd2());
		res.setMerchantPGAdd3(merchantPGDetails.getMerchantPGAdd3());
		res.setCreated(merchantPGDetails.getCreated().toString());
		res.setUpdated(merchantPGDetails.getUpdated().toString());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", res);
		return sdto;
	}

	public Object getTransactiilteronDetailsWithDateF(String merchantId, String dateFrom, String dateTo)
			throws ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		List<TransactionDetails> listTransactionDetals = new ArrayList<TransactionDetails>();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (dateTo.length() != 0) {
			listTransactionDetals = transactionDetailsRepository.getTransactionDateRange(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo));

		} else {
			listTransactionDetals = transactionDetailsRepository.getTransactionDate(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom));
		}

		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : listTransactionDetals) {
			TransactionDetailsDto trd = new TransactionDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setAmount(Float.toString(((float) tr.getAmount() / 100)));
			trd.setPaymentOption(tr.getPaymentOption());
			trd.setOrderID(tr.getOrderID());
			trd.setStatus(tr.getStatus());
			trd.setPaymentMode(tr.getPaymentMode());
			trd.setTxtMsg(tr.getTxtMsg());
			trd.setTransactionTime(tr.getCreated().toString());
			trd.setMerchantOrderId(tr.getMerchantOrderId());
			trd.setMerchantReturnURL(tr.getMerchantReturnURL());
			if (tr.getVpaUPI() != null) {
				trd.setVpaUPI(
						Utility.maskUpiCode(SecurityUtils.decryptSaveDataKMS(tr.getVpaUPI()).replace("\u0000", "")));
			}
			if (tr.getPaymentCode() != null) {
				// trd.setWalletOrBankCode(SecurityUtils.decryptSaveDataKMS(tr.getPaymentCode()).replace("\u0000",
				// ""));
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(tr.getCardNumber()))
						.replace("\u0000", ""));
			}
			trdetails.add(trd);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetail", trdetails);
		return sdto;

		// return listTransactionDetals;
	}

	public Object getSettlementDetailsWithDateF(String merchantId, String dateFrom, String dateTo)
			throws ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		List<MerchantBalanceSheet> listTransactionDetals = new ArrayList<MerchantBalanceSheet>();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (dateTo.length() != 0) {
			listTransactionDetals = merchantBalanceSheetRepository.getSettlementDateRange(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo));

		} else {
			listTransactionDetals = merchantBalanceSheetRepository.getSettlementFrom(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom));
		}

		List<SettlementDetailsDto> trdetails = new ArrayList<SettlementDetailsDto>();
		for (MerchantBalanceSheet tr : listTransactionDetals) {
			Float askcomm = (float) 0;
			Float pgcomm = (float) 0;
			Float assocomm = (float) 0;
			Float tax = (float) 0;
			Float settledamt = (float) 0;
			SettlementDetailsDto trd = new SettlementDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setMerchant_order_id(tr.getMerchantOrderId());
			trd.setTrxamount(Float.toString(((float) tr.getAmount() / 100)));
			trd.setOrder_id(tr.getOrderId());
			trd.setPg_status(tr.getPgStatus());
			if (tr.getSettlementStatus() == null) {
				trd.setSettlement_status("PENDING");
			} else {
				trd.setSettlement_status(tr.getSettlementStatus());
			}
			if (tr.getAskCommission() != null) {
				askcomm = Float.valueOf(tr.getAskCommission());
			}
			if (tr.getAssociateCommission() != null) {
				assocomm = Float.valueOf(tr.getAssociateCommission());
			}
			if (tr.getPgCommission() != null) {
				pgcomm = Float.valueOf(tr.getPgCommission());
			}
			Float service_charge = askcomm + pgcomm + assocomm;
			logger.info("Service Charge" + String.valueOf(service_charge));
			trd.setService_charge(String.valueOf(service_charge / 100));
			tax = (float) ((float) service_charge) * 0.18f;
			logger.info("Tax Charge" + String.valueOf(tax));
			trd.setTax_calc(String.valueOf(GeneralUtils.round((float) tax / 100, 2)));
			logger.info("Tax Charge" + trd.getTax_calc());
			if (tr.getSettleAmountToMerchant() != null) {
				trd.setSettle_amount_to_merchant(String.valueOf((float) tr.getSettleAmountToMerchant() / 100));
			} else {
				trd.setSettle_amount_to_merchant(String.valueOf(settledamt));
			}
			trd.setSettlement_date(String.valueOf(tr.getSettlementDate()));
			trd.setTr_type(tr.getTrType());

			trdetails.add(trd);
		}
		return trdetails;

		// return listTransactionDetals;
	}

	public SuccessResponseDto processsettlement(UserAdminDetails userAdminDetails,
			ProcessSettlementRequest processSettlementRequest) {

		ProcessSettlementResponse processSettlementResponse = new ProcessSettlementResponse();
		List<ProcessSettlement> listProcessSettlement = new ArrayList<ProcessSettlement>();
		boolean flag = false;

		for (ProcessSettlement processSettlement : processSettlementRequest.getProcessSettlement()) {
			MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository
					.findByOrderId(processSettlement.getOrderid());
			if (merchantBalanceSheet == null) {
				processSettlement.setRemarks("OrderID Not Found in System");
				flag = true;

			}

			if ((processSettlement.getCustCommission() + processSettlement.getPgCommission()
					+ processSettlement.getSettlementAmount()) > merchantBalanceSheet.getAmount()) {
				processSettlement
						.setRemarks("Settlemnt Can't be processed due to Amount is less than settlement amount .");
				flag = true;
			}
			if (merchantBalanceSheet.getSettlementStatus().equalsIgnoreCase("PROCESS")) {
				processSettlement.setRemarks("Settlemnt already done .. Can't be process now .");
				flag = true;
			}

			if (!flag) {
				merchantBalanceSheet.setSettlementStatus("PROCESS");
				merchantBalanceSheet.setSettlementDate(new Date());
				processSettlement.setRemarks("Order Processed...");
				merchantBalanceSheet.setAskCommission(processSettlement.getCustCommission());
				merchantBalanceSheet.setPgCommission(processSettlement.getPgCommission());
				merchantBalanceSheet.setSettleAmountToMerchant(processSettlement.getSettlementAmount());
				merchantBalanceSheet.setSettleBy(userAdminDetails.getUuid());
				merchantBalanceSheetRepository.save(merchantBalanceSheet);
			}
			listProcessSettlement.add(processSettlement);
		}
		processSettlementResponse.setProcessSettlement(listProcessSettlement);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("settlementDetail", processSettlementResponse);
		return sdto;
	}

	public SuccessResponseDto updateMerchantPGServiceStatus(String merchantId, String pgUuid, String statusUpdate,
			String service, String uuid) throws ValidationExceptions {

		List<MerchantPGServices> listOfMerchantPGService = null;
		MerchantPGDetails merchantPGDetails = null;
		MerchantDetails merchantDetails = null;
		MerchantPGServices merchantPGServices = null;
		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);

		if (!pgConfigurationDetails.getStatus().equalsIgnoreCase("ACTIVE")) {
			throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
		}

		merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (merchantDetails.getUserStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		logger.info("Input Details :: PGName :: " + pgUuid + " , MerchantId :: " + merchantId);

		merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantId, pgUuid);

		if (merchantPGDetails == null) {
			throw new ValidationExceptions(PG_SERVICE_ASSOCIATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_ASSOCIATION_NOT_FOUND);

		}
		logger.info("MERCHANT PG STATUS:" + merchantPGDetails.getStatus());
		if (!merchantPGDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
		}

		listOfMerchantPGService = merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(merchantId, pgUuid,
				service);

		if (listOfMerchantPGService == null) {
			throw new ValidationExceptions(MERCHANT_PG_SERVICE_NOT_ASSOCIATED,
					FormValidationExceptionEnums.MERCHANT_PG_SERVICE_NOT_ASSOCIATED);

		} else {

			for (MerchantPGServices mpgs : listOfMerchantPGService) {

				if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
					mpgs.setStatus(UserStatus.ACTIVE.toString());
					mpgs.setProcessedBy(uuid);
					mpgs.setUpdated(new Date());
					mpgs.setUpdatedBy(uuid);
					merchantPGServices = merchantPGServicesRepository.save(mpgs);
				} else {
					mpgs.setStatus(UserStatus.BLOCKED.toString());
					mpgs.setProcessedBy(uuid);
					mpgs.setUpdated(new Date());
					mpgs.setUpdatedBy(uuid);
					merchantPGServices = merchantPGServicesRepository.save(mpgs);
				}

			}

		}
		/*
		 * if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
		 * //merchantPGService =
		 * merchantPGServicesRepository.findByMerchantIDAndPgIDAndServiceAndStatus(
		 * merchantId,pgUuid, service, statusUpdate); merchantPGService =
		 * merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(merchantId,
		 * pgUuid, service);
		 * 
		 * 
		 * if (service.equalsIgnoreCase(PGServices.NB.toString())||
		 * service.equalsIgnoreCase(PGServices.WALLET.toString())) {
		 * 
		 * } else {
		 * 
		 * if (merchantPGService != null) { throw new
		 * ValidationExceptions(MERCHANT_SERVICE_PRESENT_AS_ACTIVE,
		 * FormValidationExceptionEnums.MERCHANT_SERVICE_PRESENT_AS_ACTIVE); }
		 * checkMultipleServiceActive(merchantId, pgUuid, service, statusUpdate); }
		 * 
		 * 
		 * merchantPGService.setStatus(UserStatus.ACTIVE.toString());
		 * merchantPGService.setProcessedBy(uuid); merchantPGService =
		 * merchantPGServicesRepository.save(merchantPGService);
		 * 
		 * }else {
		 * 
		 * merchantPGService.setStatus(UserStatus.BLOCKED.toString());
		 * merchantPGService.setProcessedBy(uuid); merchantPGService =
		 * merchantPGServicesRepository.save(merchantPGService); }
		 */
		// merchantPGService =
		// merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(merchantId,merchantPGDetails.getMerchantPGId(),
		// service);

		/*
		 * if (merchantPGService == null) { throw new
		 * ValidationExceptions(MERCHANT_PG_SERVICE_NOT_ASSOCIATED,
		 * FormValidationExceptionEnums.MERCHANT_PG_SERVICE_NOT_ASSOCIATED);
		 * 
		 * } logger.info("Before Checking ...");
		 * 
		 * if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
		 * 
		 * merchantPGService.setStatus(UserStatus.ACTIVE.toString());
		 * 
		 * } else { merchantPGService.setStatus(UserStatus.BLOCKED.toString()); }
		 * merchantPGService.setUpdatedBy(uuid); merchantPGService.setProcessedBy(uuid);
		 */

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		// sdto.getExtraData().put("merchantDetail",
		// merchantPGServicesRepository.save(merchantPGService));
		sdto.getExtraData().put("MerchantPGServicesDetails", merchantPGServices);

		return sdto;
	}

	// api/admin/allMerchantDetailsReport?
	public SuccessResponseDto getAllMerchantDetailsReport() throws JsonProcessingException {
		logger.info("inside sevice start :::  /api/admin/allMerchantDetailsReport");
		// A Merchant can have multiple PG and each PG has some service

		// MerchantDetailsRepository SELECT * FROM pgdbkmsn.merchant_details;
		// MerchantPGServicesRepository SELECT * FROM pgdbkmsn.merchantpgservices;
		// MerchantPGDetailsRepository SELECT * FROM pgdbkmsn.merchantpgdetails;

		// pgdbkmsn.merchant_details md.merchantid
		// pgdbkmsn.merchantpgservices mpgs.merchantid
		// pgdbkmsn.merchantpgdetails mpg.merchantid

		List<IAllMerchantDetailsReport> getMerchantDetailsReport = merchantPGServicesRepository
				.getAllMerchantDetailsReport();
		logger.info("inside sevice getMerchantDetailsReport.size() 0.1 :::  /api/admin/allMerchantDetailsReport",
				getMerchantDetailsReport.size());
		System.out.println(getMerchantDetailsReport.size());

		List<AllMerchantDetails> allmerchants = new LinkedList<>();

		String meremail = "";
		AllMerchantDetails m = null;
		String pgname = "";
		List<MerchantPgdetails> merchantPgdetails = new LinkedList<>();
		List<MerchantServiceDetails> merchantServiceDetails = new LinkedList<>();
		logger.info("inside merchantServiceDetails 0.2 :::  /api/admin/allMerchantDetailsReport");
		for (IAllMerchantDetailsReport mer : getMerchantDetailsReport) {
			// logger.info("inside merchantServiceDetails 0.3 111:::
			// /api/admin/allMerchantDetailsReport getSaltKey:: "
			// + mer.getSaltKey());
			MerchantPgdetails pg = null;
			if (mer.getMerchantId() == null) {
				continue;
			}
			if (meremail.equals(mer.getMerchantEMail())) {
				// logger.info("inside merchantServiceDetails 0.5 :::
				// /api/admin/allMerchantDetailsReport");
				if (m != null) {
					// logger.info("inside merchantServiceDetails 1 :::
					// /api/admin/allMerchantDetailsReport");
					if (pgname.equals(mer.getPGName())) {
						// logger.info("inside merchantServiceDetails 0.6 :::
						// /api/admin/allMerchantDetailsReport");
						MerchantServiceDetails ms = new MerchantServiceDetails();
						ms.setServiceStatus(mer.getServiceStatus());
						ms.setServiceType(mer.getServiceType());
						merchantServiceDetails.add(ms);
					} else {
						// logger.info("inside merchantServiceDetails 4 :::
						// /api/admin/allMerchantDetailsReport");
						// logger.info("PGName :: " + mer.getPGName());
						merchantServiceDetails = new LinkedList<>();
						MerchantServiceDetails ms = new MerchantServiceDetails();
						ms.setServiceStatus(mer.getServiceStatus());
						ms.setServiceType(mer.getServiceType());
						merchantServiceDetails.add(ms);
						logger.info("inside merchantServiceDetails 2 :::  /api/admin/allMerchantDetailsReport");
						pg = new MerchantPgdetails();
						pg.setPguuid(mer.getPGUuid());
						pg.setPgname(mer.getPGName());
						pg.setPgstatus(mer.getPGStatus());
						if (merchantServiceDetails != null) {
							pg.setMerchantservicedetails(merchantServiceDetails);
						}
						pgname = mer.getPGName();
						merchantPgdetails.add(pg);
					}
					m.setMerchantpgdetails(merchantPgdetails);
				}

			} else {
				// logger.info("inside merchantServiceDetails 5 :::
				// /api/admin/allMerchantDetailsReport");
				m = new AllMerchantDetails();
				merchantPgdetails = new LinkedList<>();
				merchantServiceDetails = new LinkedList<MerchantServiceDetails>();
				m.setUuid(mer.getUuid());
				m.setMerchantName(mer.getMerchantName());
				m.setMerchantEMail(mer.getMerchantEMail());
				m.setPhoneNumber(mer.getPhoneNumber());
				m.setMerchantId(mer.getMerchantId());
				m.setMerchantStatus(mer.getUserStatus());
				m.setKycStatus(mer.getKycStatus());
				m.setMerchantAppId(mer.getAppId());
				m.setSaltKey(mer.getSaltKey());
				// String secret = Encryption.decryptForFrontEndDataKMS(mer.getSecretId());
				// m.setMerchantSecretKey(secret);

				pg = new MerchantPgdetails();
				pg.setPguuid(mer.getPGUuid());
				pg.setPgname(mer.getPGName());
				pg.setPgstatus(mer.getPGStatus());
				merchantPgdetails.add(pg);

				MerchantServiceDetails ms = new MerchantServiceDetails();
				ms.setServiceStatus(mer.getServiceStatus());
				ms.setServiceType(mer.getServiceType());
				merchantServiceDetails.add(ms);
				pg.setMerchantservicedetails(merchantServiceDetails);

				m.setMerchantpgdetails(merchantPgdetails);

				meremail = mer.getMerchantEMail();
				pgname = mer.getPGName();
				allmerchants.add(m);
			}
			// logger.info("inside merchantServiceDetails 4.1 :::
			// /api/admin/allMerchantDetailsReport");
		}
		// logger.info("inside service ends ::: /api/admin/allMerchantDetailsReport");
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_OK);
		sdto.getExtraData().put("merchantDetails", allmerchants);
		return sdto;

	}

	public SuccessResponseDto getAllMerchantDetailsAllService() throws JsonProcessingException {

		// A Merchant can have multiple PG and each PG has some service

		// MerchantDetailsRepository SELECT * FROM pgdbkmsn.merchant_details;
		// MerchantPGServicesRepository SELECT * FROM pgdbkmsn.merchantpgservices;
		// MerchantPGDetailsRepository SELECT * FROM pgdbkmsn.merchantpgdetails;

		// pgdbkmsn.merchant_details md.merchantid
		// pgdbkmsn.merchantpgservices mpgs.merchantid
		// pgdbkmsn.merchantpgdetails mpg.merchantid
		List<AllMerchantDetails> allmerchants = new LinkedList<>();

		String meremail = "";
		AllMerchantDetails m = null;
		String pgname = "";
		List<MerchantPgdetails> merchantPgdetails = new LinkedList<>();
		List<MerchantServiceDetails> merchantServiceDetails = new LinkedList<>();
		logger.info(" @@@@ 1 @@@@@");
		List<MerchantDetails> allMerchant = merchantDetailsRepository.findAll();
		System.out.println(allMerchant == null);
		for (MerchantDetails mer : allMerchant) {
			if (mer.getUserStatus().equalsIgnoreCase("ACTIVE")) {
				logger.info(" @@@@ 2 @@@@@");
				List<MerchantPGServices> merPGserv = merchantPGServicesRepository
						.findAllByMerchantID(mer.getMerchantID());
				logger.info(" @@@@ 3 @@@@@");
				if (merPGserv == null) {
					m = new AllMerchantDetails();
					m.setUuid(mer.getUuid());
					m.setMerchantName(mer.getMerchantName());
					m.setMerchantEMail(mer.getMerchantEmail());
					m.setPhoneNumber(mer.getPhoneNumber());
					m.setMerchantId(mer.getMerchantID());
					m.setMerchantStatus(mer.getUserStatus());
					m.setKycStatus(mer.getKycStatus());
					m.setMerchantAppId(mer.getAppID());
					// String secret = Encryption.decryptForFrontEndDataKMS(mer.getSecretId());
					// m.setMerchantSecretKey(secret);
					allmerchants.add(m);
				}
				if (merPGserv != null) {
					for (MerchantPGServices merchantPGServices : merPGserv) {
						logger.info(" @@@@ 4 @@@@@ " + mer.getMerchantID() + "  pgid  " + merchantPGServices.getPgID());
						MerchantPGDetails pg1 = merchantPGDetailsRepository
								.findByMerchantIDAndMerchantPGId(mer.getMerchantID(), merchantPGServices.getPgID());
						logger.info(" @@@@ 5 @@@@@");
						if (pg1 != null) {
							MerchantPgdetails pg = new MerchantPgdetails();

							if (meremail.equals(mer.getMerchantEmail())) {
								if (m != null) {
									if (pgname.equals(pg1.getMerchantPGName())) {
										logger.info(" @@@@ 7 @@@@@");
										MerchantServiceDetails ms = new MerchantServiceDetails();
										ms.setServiceStatus(merchantPGServices.getStatus());
										ms.setServiceType(merchantPGServices.getService());
										merchantServiceDetails.add(ms);
									} else {
										logger.info(" @@@@ 8 @@@@@");
										merchantServiceDetails = new LinkedList<>();
										MerchantServiceDetails ms = new MerchantServiceDetails();
										ms.setServiceStatus(merchantPGServices.getStatus());
										ms.setServiceType(merchantPGServices.getService());
										merchantServiceDetails.add(ms);

										pg = new MerchantPgdetails();
										pg.setPguuid(pg1.getMerchantPGId());
										pg.setPgname(pg1.getMerchantPGName());
										pg.setPgstatus(pg1.getStatus());
										if (merchantServiceDetails != null) {
											pg.setMerchantservicedetails(merchantServiceDetails);
										}
										pgname = pg1.getMerchantPGName();
										merchantPgdetails.add(pg);
									}

								}
							} else {
								logger.info(" @@@@ 6 @@@@@");
								m = new AllMerchantDetails();
								m.setUuid(mer.getUuid());
								m.setMerchantName(mer.getMerchantName());
								m.setMerchantEMail(mer.getMerchantEmail());
								m.setPhoneNumber(mer.getPhoneNumber());
								m.setMerchantId(mer.getMerchantID());
								m.setMerchantStatus(mer.getUserStatus());
								m.setKycStatus(mer.getKycStatus());
								m.setMerchantAppId(mer.getAppID());
								// String secret = Encryption.decryptForFrontEndDataKMS(mer.getSecretId());
								// m.setMerchantSecretKey(secret);

								pg.setPguuid(pg1.getMerchantPGId());
								pg.setPgname(pg1.getMerchantPGName());
								pg.setPgstatus(pg1.getStatus());
								merchantPgdetails.add(pg);

								MerchantServiceDetails ms = new MerchantServiceDetails();
								ms.setServiceStatus(merchantPGServices.getStatus());
								ms.setServiceType(merchantPGServices.getService());
								merchantServiceDetails.add(ms);
								pg.setMerchantservicedetails(merchantServiceDetails);

								m.setMerchantpgdetails(merchantPgdetails);

								meremail = mer.getMerchantEmail();
								pgname = pg1.getMerchantPGName();

								allmerchants.add(m);

							}
						}

					}

				}

			}
		}
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.setStatus(HttpStatus.SC_OK);
		sdto.getExtraData().put("merchantDetails", allmerchants);
		return sdto;

	}

	public List<AllMerchantDetails> getMerchantListByNameDate(String merchantName, String start_date, String end_date)
			throws ValidationExceptions {

		List<MerchantDetails> merchantList = merchantDetailsRepository.findByMerchantName(merchantName, start_date,
				end_date);

		if (merchantList == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		return getMerchantDetailByNameFunc(merchantList, merchantName);
	}

	public BusinessAssociate createBusinessAssociate(BusinessAssociateCreateRequest businessAssociateCreateRequest)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository
				.findByMerchantID(businessAssociateCreateRequest.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		BusinessAssociate businessAssociate = businessAssociateRepository
				.findByMerchantID(businessAssociateCreateRequest.getMerchantId());
		if (businessAssociate != null) {
			throw new ValidationExceptions(MERCHANT_ASSO_WITH_BUSI_ASSO + businessAssociate.getName(),
					FormValidationExceptionEnums.MERCHANT_ASSO_WITH_BUSI_ASSO);
		}

		businessAssociate = new BusinessAssociate();
		businessAssociate.setAddress(businessAssociateCreateRequest.getAddress());
		businessAssociate.setBankAccountNo(businessAssociateCreateRequest.getAccountNumber());
		businessAssociate.setBankName(businessAssociateCreateRequest.getBankName());
		businessAssociate.setEmailId(businessAssociateCreateRequest.getEmailId());
		businessAssociate.setIfscCode(businessAssociateCreateRequest.getIfscCode());
		businessAssociate.setMerchantID(businessAssociateCreateRequest.getMerchantId());
		businessAssociate.setMicrCode(businessAssociateCreateRequest.getMicrCode());
		businessAssociate.setName(businessAssociateCreateRequest.getName());
		businessAssociate.setPhoneNumber(businessAssociateCreateRequest.getPhoneNumber());
		businessAssociate.setUuid(Utility.generateAppId());

		businessAssociate = businessAssociateRepository.save(businessAssociate);

		return businessAssociate;
	}

	public BusinessAssociateCommissionDetails createBusinessAssociateCommission(String busiAssociateuuid,
			String merchantId, String commType, String serviceType, String serviceSubType, double commAmount,
			String createdBy) throws ValidationExceptions {

		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = new BusinessAssociateCommissionDetails();

		BusinessAssociate businessAssociate = businessAssociateRepository.findByUuidAndMerchantID(busiAssociateuuid,
				merchantId);
		if (businessAssociate == null) {
			throw new ValidationExceptions(BUSINESS_ASSOCIATED_NOT_FOUND,
					FormValidationExceptionEnums.BUSINESS_ASSOCIATED_NOT_FOUND);
		}
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (serviceType.equalsIgnoreCase(PGServices.CARD.toString())) {
			businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo
					.findByUuidAndMerchantIDAndPaymentTypeAndPaymentSubTypeAndStatus(busiAssociateuuid, merchantId,
							serviceType, serviceSubType, UserStatus.ACTIVE.toString());
		} else {
			businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo
					.findByUuidAndMerchantIDAndPaymentTypeAndStatus(busiAssociateuuid, merchantId, serviceType,
							UserStatus.ACTIVE.toString());
		}

		if (businessAssociate != null) {
			throw new ValidationExceptions(COMMISSION_WITH_MERCHANT_ALREADY_PRESENT,
					FormValidationExceptionEnums.COMMISSION_WITH_MERCHANT_ALREADY_PRESENT);

		}

		businessAssociateCommissionDetails.setCommissionAmount(commAmount);
		businessAssociateCommissionDetails.setCommissionType(commType);
		businessAssociateCommissionDetails.setCreatedBy(createdBy);
		businessAssociateCommissionDetails.setMerchantID(merchantId);
		businessAssociateCommissionDetails.setPaymentType(serviceType);
		businessAssociateCommissionDetails.setPaymentSubType(serviceSubType);
		businessAssociateCommissionDetails.setUuid(busiAssociateuuid);
		businessAssociateCommissionDetailsRepo.save(businessAssociateCommissionDetails);

		return businessAssociateCommissionDetailsRepo.save(businessAssociateCommissionDetails);
	}

	public BusinessAssociateCommissionDetails updateBusinessAssociateCommission(String busiAssociateuuid, int commId,
			String status, String uuid) throws ValidationExceptions {

		if (!status.equalsIgnoreCase(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(COMMISSION_UPDATE, FormValidationExceptionEnums.COMMISSION_UPDATE);
		}

		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo
				.findByUuidAndIdAndStatus(busiAssociateuuid, Long.valueOf(commId), UserStatus.ACTIVE.toString());

		if (businessAssociateCommissionDetails == null) {
			throw new ValidationExceptions(COMMISSION_NOT_FOUND, FormValidationExceptionEnums.COMMISSION_NOT_FOUND);
		}

		businessAssociateCommissionDetails.setStatus(status.toUpperCase());
		businessAssociateCommissionDetails.setUuid(busiAssociateuuid);

		return businessAssociateCommissionDetailsRepo.save(businessAssociateCommissionDetails);
	}

	public SuccessResponseDto getMerchantCommDetails(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail",
				merchantBalanceSheetRepository.findByAdminCommDetailsTotal(userAdminDetails.getUuid()));
		return sdto;
	}

	public Object getAdminMerchantCommissionPendindSettlement(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantBalanceSheetRepository
				.findByAdminMerchantCommissionPendindSettlement(userAdminDetails.getUuid()));
		return sdto;
	}

	public SuccessResponseDto updateCommissionDetails(UserAdminDetails userAdminDetails, String orderId, int pgComm,
			int custComm, int businessAssocComm, int merchantSettleAmount) throws ValidationExceptions {

		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository
				.findByOrderIdAndSettlementStatus(orderId, UserStatus.PENDING.toString());
		if (merchantBalanceSheet == null) {
			throw new ValidationExceptions(TRANSACTION_NOT_FOUND, FormValidationExceptionEnums.TRANSACTION_NOT_FOUND);
		}

		if (merchantBalanceSheet.getAmount() != (pgComm + custComm + businessAssocComm + merchantSettleAmount)) {
			throw new ValidationExceptions(AMOUNT_NOT_MATCHED_WITH_EDITED_COMM,
					FormValidationExceptionEnums.AMOUNT_NOT_MATCHED_WITH_EDITED_COMM);
		}

		merchantBalanceSheet.setAskCommission(custComm);
		merchantBalanceSheet.setAssociateCommission(businessAssocComm);
		merchantBalanceSheet.setPgCommission(pgComm);
		merchantBalanceSheet.setSettleAmountToMerchant(merchantSettleAmount);
		merchantBalanceSheet.setProcessedBy(userAdminDetails.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantCommissionDetail", merchantBalanceSheetRepository.save(merchantBalanceSheet));
		return sdto;
	}

	public SuccessResponseDto refundRequest(UserAdminDetails userAdminDetails, String orderId, String merchantId)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (txnParam(orderId) == true) {
			if (orderId.length() < 8) {
				throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION,
						FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
			} else {
				if (transactionDetailsRepository.findAllByMerchantOrderId(orderId).isEmpty()) {
					throw new ValidationExceptions(ORDER_ID_NOT_FOUND,
							FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
				}
			}
		}

		List<RefundDetails> refundDetail = refundDetailsRepository.getAllRefundByMerchantOrderId(orderId);
		if (!refundDetail.isEmpty()) {
			throw new ValidationExceptions(REFUND_DETAILS_EXIST, FormValidationExceptionEnums.REFUND_DETAILS_EXIST);
		}

		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository
				.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(merchantId, orderId,
						UserStatus.PENDING.toString());

		if (merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_INITIATE_FAILED, FormValidationExceptionEnums.REFUND_INITIATE_FAILED);
		}

		RefundDetails refundDetails = new RefundDetails();
		refundDetails.setAmount(String.valueOf(merchantBalanceSheet.getAmount()));
		refundDetails.setInitiatedBy(userAdminDetails.getUuid());
		refundDetails.setMerchantId(merchantId);
		refundDetails.setMerchantOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setPaymentCode(merchantBalanceSheet.getPaymentCode());
		refundDetails.setPaymentMode(merchantBalanceSheet.getPaymentMode());
		refundDetails.setPaymentOption(merchantBalanceSheet.getTrType());
		refundDetails.setPgOrderId(merchantBalanceSheet.getPgOrderId());
		refundDetails.setPgStatus(merchantBalanceSheet.getPgStatus());
		refundDetails.setRefOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setStatus(UserStatus.INITIATED.toString());
		refundDetails.setVpaUpi(merchantBalanceSheet.getVpaUPI());
		refundDetails.setUserId(merchantBalanceSheet.getUserId());

		merchantBalanceSheet.setSettlementStatus(UserStatus.INITIATED.toString());
		merchantBalanceSheetRepository.save(merchantBalanceSheet);

		merchantBalanceSheet.setSettlementStatus(UserStatus.INITIATED.toString());
		merchantBalanceSheet.setPgStatus("REFUNDED");
		merchantBalanceSheetRepository.save(merchantBalanceSheet);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundDetailsRepository.save(refundDetails));
		return sdto;
	}

	public SuccessResponseDto refundRequestUpdate(UserAdminDetails userAdminDetails, String orderId, String merchantId,
			String status, String refundTxt) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (txnParam(orderId) == true) {
			if (orderId.length() < 8) {
				throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION,
						FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
			} else {
				if (transactionDetailsRepository.findAllByMerchantOrderId(orderId).isEmpty()) {
					throw new ValidationExceptions(ORDER_ID_NOT_FOUND,
							FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
				}
			}
		}

		RefundDetails refundDetails = refundDetailsRepository.findByMerchantIdAndMerchantOrderIdAndStatus(merchantId,
				orderId, UserStatus.INITIATED.toString());
		if (refundDetails == null) {
			throw new ValidationExceptions(REFUND_UPDATE_FAILED, FormValidationExceptionEnums.REFUND_UPDATE_FAILED);
		}

		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository
				.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(merchantId, orderId,
						UserStatus.INITIATED.toString());
		if (merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_UPDATE_FAILED, FormValidationExceptionEnums.REFUND_UPDATE_FAILED);
		}

		merchantBalanceSheet.setSettlementStatus(UserStatus.CLOSED.toString());
		merchantBalanceSheetRepository.save(merchantBalanceSheet);

		refundDetails.setStatus(status);
		refundDetails.setRefundMsg(refundTxt);
		refundDetails.setUpdatedBy(userAdminDetails.getUuid());
		sendMail.sendMailForMerchantRefundStatus(merchantDetails, refundDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Mail has been Sent to the Registered Merchant E-Mail id !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundDetailsRepository.save(refundDetails));
		return sdto;
	}

	public List<MerchantRefundDto> refundDetail(String refundid, String start_date, String end_date)
			throws ValidationExceptions {

		dateWiseValidation(start_date, end_date);

		List<RefundDetails> refundlist = new ArrayList<RefundDetails>();

		List<MerchantRefundDto> list = new ArrayList<MerchantRefundDto>();

		if (txnParam(refundid) == true) {

			refundlist = refundDetailsRepository.getAllRefundDetailByRefundId(refundid);
			if (refundlist.isEmpty()) {
				throw new ValidationExceptions(REFUND_ID_NOT_FOUND, FormValidationExceptionEnums.REFUND_ID_NOT_FOUND);
			}

		} else {
			refundlist = refundDetailsRepository.getAllRefundDetail(start_date, end_date);
		}

		for (RefundDetails rd : refundlist) {
			MerchantRefundDto dto = new MerchantRefundDto();
			dto.setInitiatedBy(rd.getInitiatedBy());
			dto.setMerchantId(rd.getMerchantId());
			dto.setMerchantOrderId(rd.getMerchantOrderId());
			dto.setAmount(rd.getAmount());
			dto.setPaymentCode(rd.getPaymentCode());
			dto.setPaymentMode(rd.getPaymentMode());
			dto.setPaymentOption(rd.getPaymentOption());
			dto.setPgOrderId(rd.getPgOrderId());
			dto.setPgStatus(rd.getPgStatus());
			dto.setPgTrTime(rd.getPgTrTime());
			dto.setStatus(rd.getStatus());
			dto.setRefOrderId(rd.getRefOrderId());
			dto.setRefundMsg(rd.getRefundMsg());

			list.add(dto);
		}
		return list;

	}

	public List<RefundDetails> refundlist() {
		return refundDetailsRepository.findAll();
	}

	public List<RefundDetails> refundDetailByStatusOrMerchantOrderId(String merchantOrderId, String status)
			throws ValidationExceptions {

		if (txnParam(merchantOrderId) == true) {
			if (merchantOrderId.length() < 8) {
				throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION,
						FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
			} else {
				if (transactionDetailsRepository.findAllByMerchantOrderId(merchantOrderId).isEmpty()) {
					throw new ValidationExceptions(ORDER_ID_NOT_FOUND,
							FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
				}
			}
		}

		if (txnParam(merchantOrderId) == true && txnParam(status) == true) {
			return refundDetailsRepository.getAllRefundByStatusOrMerchantOrderId(merchantOrderId, status);
		} else if (txnParam(merchantOrderId) == false && txnParam(status) == true) {
			return refundDetailsRepository.getAllRefundByStatus(status);
		} else if (txnParam(merchantOrderId) == true && txnParam(status) == false) {
			return refundDetailsRepository.getAllRefundByMerchantOrderId(merchantOrderId);
		}
		return null;

	}

	public boolean checkMultipleServiceActive(String merchantId, String pgUUid, String service, String status)
			throws ValidationExceptions {

		if (status.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			if (service.equalsIgnoreCase(PGServices.CARD.toString())
					|| service.equalsIgnoreCase(PGServices.UPI.toString())
					|| service.equalsIgnoreCase(PGServices.UPI_QR.toString())) {
				MerchantPGServices merchantPGServices = merchantPGServicesRepository
						.findByMerchantIDAndStatusAndService(merchantId, status, service);
				if (merchantPGServices != null) {
					throw new ValidationExceptions(MERCHANT_SERVICE_PRESENT_AS_ACTIVE,
							FormValidationExceptionEnums.MERCHANT_SERVICE_PRESENT_AS_ACTIVE);

				}

			}
		}
		return true;
	}

	public SuccessResponseDto getUniqueWalletList() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("walletDetail", masterWalletListRepository.getUniqueWalletList());
		return sdto;
	}

	public SuccessResponseDto getWalletAssocationReport() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("walletDetail", walletListRepository.findByOrderByPgname());
		return sdto;
	}

	public SuccessResponseDto getWalletList() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("walletDetail", walletListRepository.findAll());
		return sdto;
	}

	public Object getUniqueBankList() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", masterBankListRepository.getUniqueBankList());
		return sdto;
	}

	public Object getBankAssocationReport() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", bankListRepository.findByOrderByPgName());
		return sdto;
	}

	public SuccessResponseDto getBanklist() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", bankListRepository.findAll());
		return sdto;
	}

	public SuccessResponseDto associateBankListToMerchant(UserAdminDetails userAdminDetails,
			RequestMasterBankListAssociation request) throws ValidationExceptions {

		List<ResponseMasterBankListAssociation> listResponseMasterBankListAssociation = new ArrayList<ResponseMasterBankListAssociation>();
		ResponseMasterBankListAssociation responseMasterBankListAssociation = new ResponseMasterBankListAssociation();

		if (request.getBankCode() == null) {
			throw new ValidationExceptions(BANKLIST_EMPTY, FormValidationExceptionEnums.BANKLIST_EMPTY);
		}

		if (request.getMerchantId() == null) {
			throw new ValidationExceptions(MERCHANT_ID_REQUIRED, FormValidationExceptionEnums.MERCHANT_ID_REQUIRED);
		}

		if (request.getPgId() == null) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(request.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgUuid(request.getPgId());
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		String[] arrOfStr = Utility.functionSplit(request.getBankCode(), ",");
		if (arrOfStr.length == 0) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}

		for (String bankCode : arrOfStr) {
			responseMasterBankListAssociation = new ResponseMasterBankListAssociation();
			responseMasterBankListAssociation.setBankCode(bankCode);
			responseMasterBankListAssociation.setMerchantId(request.getMerchantId());
			responseMasterBankListAssociation.setPgId(request.getPgId());

			MasterBankList masterBankList = getMasterBankDetails(request.getPgId(), bankCode);
			if (masterBankList == null) {
				responseMasterBankListAssociation.setErrorMessage(
						"Association Failure :: No records found in Master Configuration, contact Admintrator ");
			} else {
				try {

					BankList bankList = new BankList();
					bankList.setBankcode(bankCode);
					bankList.setBankname(masterBankList.getBankName());
					bankList.setMerchantId(request.getMerchantId());
					bankList.setPgBankCode(masterBankList.getPgBankCode());
					bankList.setPgName(masterBankList.getPgName());
					bankList.setStatus(UserStatus.ACTIVE.toString());
					bankList.setPgId(request.getPgId());
					bankListRepository.save(bankList);
					responseMasterBankListAssociation.setMessage("Association Success");

				} catch (Exception e) {
					logger.error("Exception in BankList Population :: ");
					responseMasterBankListAssociation.setErrorMessage("Association Failure :: Duplicate Entry!!!");
				}
			}
			listResponseMasterBankListAssociation.add(responseMasterBankListAssociation);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", listResponseMasterBankListAssociation);
		return sdto;
	}

	public MasterBankList getMasterBankDetails(String pgId, String bankCode) {

		logger.info("Input Details :: pgId :: " + pgId + " , bankCode :: " + bankCode);

		return masterBankListRepository.findByPgIdAndBankCode(pgId, bankCode);
	}

	public MasterWalletList getMasterWalletDetails(String pgId, String walletCode) {

		logger.info("Input Details :: pgId :: " + pgId + " , walletCode :: " + walletCode);

		return masterWalletListRepository.findByPgIdAndWalletCode(pgId, walletCode);
	}

	public Object updatebankListStatus(UserAdminDetails userAdminDetails, RequestMasterBankListUpdate request)
			throws ValidationExceptions {
		List<ResponseMasterBankListAssociation> listResponseMasterBankListAssociation = new ArrayList<ResponseMasterBankListAssociation>();
		ResponseMasterBankListAssociation responseMasterBankListAssociation = new ResponseMasterBankListAssociation();

		if (request.getBankCode() == null) {
			throw new ValidationExceptions(BANKLIST_EMPTY, FormValidationExceptionEnums.BANKLIST_EMPTY);
		}

		if (request.getMerchantId() == null) {
			throw new ValidationExceptions(MERCHANT_ID_REQUIRED, FormValidationExceptionEnums.MERCHANT_ID_REQUIRED);
		}

		if (request.getPgId() == null) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}
		if (request.getStatus() == null) {
			throw new ValidationExceptions(STATUS_NOT_FOUND, FormValidationExceptionEnums.STATUS_NOT_FOUND);
		}
		if (!Validator.containsEnum(UserStatus.class, request.getStatus())) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(request.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgUuid(request.getPgId());
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		String[] arrOfStr = Utility.functionSplit(request.getBankCode(), ",");
		if (arrOfStr.length == 0) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}

		for (String bankCode : arrOfStr) {
			responseMasterBankListAssociation = new ResponseMasterBankListAssociation();
			responseMasterBankListAssociation.setBankCode(bankCode);
			responseMasterBankListAssociation.setMerchantId(request.getMerchantId());
			responseMasterBankListAssociation.setPgId(request.getPgId());

			BankList bankList = bankListRepository.findByBankcodeAndPgIdAndMerchantId(bankCode, request.getPgId(),
					request.getMerchantId());
			if (bankList == null) {
				responseMasterBankListAssociation.setErrorMessage("Update failed due to non availability of Records");
			} else {
				try {

					bankList.setStatus(request.getStatus());
					bankListRepository.save(bankList);
					responseMasterBankListAssociation.setMessage("Association Success");

				} catch (Exception e) {
					logger.error("Exception in BankList Population :: ");
					responseMasterBankListAssociation.setErrorMessage("Update Failure :: ");
				}
			}

			listResponseMasterBankListAssociation.add(responseMasterBankListAssociation);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetail", listResponseMasterBankListAssociation);
		return sdto;

	}

	public Object associateWalletListToMerchant(UserAdminDetails userAdminDetails,
			RequestMasterWalletListAssociation request) throws ValidationExceptions, JsonProcessingException {

		logger.info("Input Details :: " + Utility.convertDTO2JsonString(request));

		List<ResponseMasterWalletListAssociation> listResponseMasterWalletListAssociation = new ArrayList<ResponseMasterWalletListAssociation>();
		ResponseMasterWalletListAssociation responseMasterWalletListAssociation = new ResponseMasterWalletListAssociation();

		if (request.getWalletCode() == null) {
			throw new ValidationExceptions(WALLETLIST_EMPTY, FormValidationExceptionEnums.WALLETLIST_EMPTY);
		}

		if (request.getMerchantId() == null) {
			throw new ValidationExceptions(MERCHANT_ID_REQUIRED, FormValidationExceptionEnums.MERCHANT_ID_REQUIRED);
		}

		if (request.getPgId() == null) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(request.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgUuid(request.getPgId());
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		String[] arrOfStr = Utility.functionSplit(request.getWalletCode(), ",");
		if (arrOfStr.length == 0) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}

		for (String walletCode : arrOfStr) {
			responseMasterWalletListAssociation = new ResponseMasterWalletListAssociation();
			responseMasterWalletListAssociation.setWalletCode(walletCode);
			responseMasterWalletListAssociation.setMerchantId(request.getMerchantId());
			responseMasterWalletListAssociation.setPgId(request.getPgId());

			MasterWalletList masterWalletList = getMasterWalletDetails(request.getPgId(), walletCode);
			if (masterWalletList == null) {
				responseMasterWalletListAssociation.setErrorMessage(
						"Association Failure :: No records found in Master Configuration, contact Admintrator ");
			} else {
				try {

					WalletList walletList = new WalletList();
					walletList.setPaymentcode(walletCode);
					walletList.setWalletname(masterWalletList.getWalletName());
					walletList.setMerchantId(request.getMerchantId());
					walletList.setPaymentcodepg(masterWalletList.getPgWalletCode());
					walletList.setPgname(masterWalletList.getPgName());
					walletList.setStatus(UserStatus.ACTIVE.toString());
					walletList.setPgId(request.getPgId());
					walletListRepository.save(walletList);
					responseMasterWalletListAssociation.setMessage("Association Success");

				} catch (Exception e) {
					logger.error("Exception in BankList Population :: ");
					responseMasterWalletListAssociation.setErrorMessage("Association Failure :: Duplicate Entry!!!");
				}
			}
			listResponseMasterWalletListAssociation.add(responseMasterWalletListAssociation);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("walletDetail", listResponseMasterWalletListAssociation);
		return sdto;
	}

	public Object updateWalletListStatus(UserAdminDetails userAdminDetails, RequestMasterWalletListUpdate request)
			throws ValidationExceptions {

		List<ResponseMasterWalletListAssociation> listResponseMasterWalletListAssociation = new ArrayList<ResponseMasterWalletListAssociation>();
		ResponseMasterWalletListAssociation responseMasterWalletListAssociation = new ResponseMasterWalletListAssociation();

		if (request.getWalletCode() == null) {
			throw new ValidationExceptions(WALLETLIST_EMPTY, FormValidationExceptionEnums.WALLETLIST_EMPTY);
		}

		if (request.getMerchantId() == null) {
			throw new ValidationExceptions(MERCHANT_ID_REQUIRED, FormValidationExceptionEnums.MERCHANT_ID_REQUIRED);
		}

		if (request.getPgId() == null) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}
		if (request.getStatus() == null) {
			throw new ValidationExceptions(STATUS_NOT_FOUND, FormValidationExceptionEnums.STATUS_NOT_FOUND);
		}
		if (!Validator.containsEnum(UserStatus.class, request.getStatus())) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(request.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgUuid(request.getPgId());
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		String[] arrOfStr = Utility.functionSplit(request.getWalletCode(), ",");
		if (arrOfStr.length == 0) {
			throw new ValidationExceptions(PG_ID_REQUIRED, FormValidationExceptionEnums.PG_ID_REQUIRED);
		}

		for (String walletCode : arrOfStr) {
			responseMasterWalletListAssociation = new ResponseMasterWalletListAssociation();
			responseMasterWalletListAssociation.setWalletCode(walletCode);
			responseMasterWalletListAssociation.setMerchantId(request.getMerchantId());
			responseMasterWalletListAssociation.setPgId(request.getPgId());

			WalletList walletList = walletListRepository.findByPaymentcodeAndPgIdAndMerchantId(walletCode,
					request.getPgId(), request.getMerchantId());
			if (walletList == null) {
				responseMasterWalletListAssociation.setErrorMessage("Update failed due to non availability of Records");
			} else {
				try {

					walletList.setStatus(request.getStatus());
					walletListRepository.save(walletList);
					responseMasterWalletListAssociation.setMessage("Association Success");

				} catch (Exception e) {
					logger.error("Exception in BankList Population :: ");
					responseMasterWalletListAssociation.setErrorMessage("Update Failure :: ");
				}
			}

			listResponseMasterWalletListAssociation.add(responseMasterWalletListAssociation);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("walletDetail", listResponseMasterWalletListAssociation);
		return sdto;

	}

	public Object getDailyReportsMerchants() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantReport", transactionDetailsRepository.getDailyReportsMerchants());
		return sdto;
	}

	@PersistenceUnit
	private EntityManagerFactory emf;

	public Object getTempReport() {

		EntityManager entitymanager = emf.createEntityManager();
		entitymanager.getTransaction().begin();
		String vSql = "select * from transaction_details";
		Query q = entitymanager.createNativeQuery(vSql);
		List<Object[]> authors = q.getResultList();

		entitymanager.close();
		emf.close();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("tempReport", authors);
		return sdto;
	}

	public MerchantBankDetails createBankDetails(MerchantBankDetails merchantBankDetails, String merchantId)
			throws ValidationExceptions {

		logger.info("getDashBoardBalance In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (!Validator.isValidAccountNumber(merchantBankDetails.getAccountNo())) {
			throw new ValidationExceptions(ACCOUNT_NUMBER_VAIDATION_FILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidIfsc(merchantBankDetails.getBankIFSCCode())) {
			throw new ValidationExceptions(IFSC_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidMICR(merchantBankDetails.getMicrCode())) {
			throw new ValidationExceptions(MICR_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		MerchantBankDetails merchantBankDetails2 = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails2 != null) {
			throw new ValidationExceptions(MERCHANT_BANK_DETAIL_PRESENT,
					FormValidationExceptionEnums.MERCHANT_BANK_DETAILS_EXISTS);
		}

		merchantBankDetails.setMerchantID(merchantDetails.getMerchantID());
		merchantBankDetails.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails.setCity(merchantBankDetails.getCity());
		merchantBankDetails.setMicrCode(merchantBankDetails.getMicrCode());
		merchantBankDetailsRepository.save(merchantBankDetails);

		return merchantBankDetails;
	}

	public MerchantBankDetails updateBankDetails(String merchantId, MerchantBankDetails merchantBankDetails)
			throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails2 = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());

		if (merchantBankDetails2 == null) {
			throw new ValidationExceptions(BANK_DETAILS_NOT_FOUND, FormValidationExceptionEnums.BANK_DETAILS_NOT_FOUND);
		}

		if (!Validator.isValidAccountNumber(merchantBankDetails.getAccountNo())) {
			throw new ValidationExceptions(ACCOUNT_NUMBER_VAIDATION_FILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidIfsc(merchantBankDetails.getBankIFSCCode())) {
			throw new ValidationExceptions(IFSC_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidMICR(merchantBankDetails.getMicrCode())) {
			throw new ValidationExceptions(MICR_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		merchantBankDetails2.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails2.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails2.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails2.setCity(merchantBankDetails.getCity());
		merchantBankDetails2.setMicrCode(merchantBankDetails.getMicrCode());

		merchantBankDetailsRepository.save(merchantBankDetails2);

		return merchantBankDetails2;
	}

	public MerchantBankDetails getBankDetails(String merchantId) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails == null) {
			throw new ValidationExceptions(BANK_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_BANK_DETILS_NOT_FOUND);
		}
		return merchantBankDetails;

	}

	public SuccessResponseDto merchantServiceLimit(String uuid, String pgUuid, String service, long thresoldValue,
			String merchantId) throws ValidationExceptions, JsonProcessingException {

		ServiceWisePaymentThresold serviceWisePaymentThresold = new ServiceWisePaymentThresold();
		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);
		}

		logger.info("pgConfigurationDetails::" + pgConfigurationDetails.getPgName());
		PGServiceDetails pgServiceDetails = pgServiceDetailsRepository
				.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getPgUuid()), service);

		if (pgServiceDetails == null) {
			throw new ValidationExceptions(PG_SERVICE_ASSOCIATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_ASSOCIATION_NOT_FOUND);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		serviceWisePaymentThresold.setService(service);
		serviceWisePaymentThresold.setPgid(pgUuid);
		serviceWisePaymentThresold.setMerchantId(merchantId);
		serviceWisePaymentThresold.setStatus(UserStatus.ACTIVE.toString());
		serviceWisePaymentThresold.setThresoldValue(thresoldValue);
		serviceWisePaymentThresold.setCreatedBy(uuid);

		serviceWisePaymentThresoldRepository.save(serviceWisePaymentThresold);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("paymentThresold", serviceWisePaymentThresold);
		return sdto;
	}

	public SuccessResponseDto updateMerchantServiceLimit(String service, long thresoldValue, String merchantId)
			throws ValidationExceptions, JsonProcessingException {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		ServiceWisePaymentThresold val = serviceWisePaymentThresoldRepository.findByMerchantIdAndService(merchantId,
				service);

		val.setThresoldValue(thresoldValue);

		serviceWisePaymentThresoldRepository.save(val);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("PaymentThresold", val);
		return sdto;
	}

	public ServiceWisePaymentThresold getMerchantServiceLimit(String merchantId) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		ServiceWisePaymentThresold val = serviceWisePaymentThresoldRepository
				.findByMerchantId(merchantDetails.getMerchantID());
		if (val == null) {
			throw new ValidationExceptions(SERVICE_LIMIT_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.SERVICE_LIMIT_DETAILS_NOT_FOUND);
		}
		return val;

	}

	public MerchantKycDetailsResponse merchantKycDetails(String merchantId, String merchantLegalName,
			String panCardNumber, String GstId, String webstieUrl, String businessEntityType, String productDescription,
			String tanNumber, String regName, String regAddress, String regPinCode, String regNumber,
			String regEmailAddress, String commName, String commAddress, String commPinCode, String commNumber,
			String commEmailAddress, MultipartFile cancelledChequeOrAccountProof,
			MultipartFile certificateOfIncorporation, MultipartFile businessPAN, MultipartFile certificateOfGST,
			MultipartFile directorKYC, MultipartFile aoa, MultipartFile moa, MultipartFile certficateOfNBFC,
			MultipartFile certficateOfBBPS, MultipartFile certificateOfSEBIOrAMFI)
			throws ValidationExceptions, IllegalAccessException, NoSuchAlgorithmException, IOException {

		validateKycDetail(panCardNumber, GstId, webstieUrl, tanNumber, regPinCode, regNumber, regEmailAddress);

		MerchantKycDetails merchantKycDetails = new MerchantKycDetails();

		MerchantDetails merchantsDetails = new MerchantDetails();
		merchantsDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantsDetails == null) {

			throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		MerchantKycDetails merchantKycDetailsCheck = new MerchantKycDetails();
		merchantKycDetailsCheck = merchantKycDetailsRepository.findByMerchantID(merchantsDetails.getMerchantID());
		if (merchantKycDetailsCheck != null) {
			throw new ValidationExceptions(MERCHANT_KYC_DETAIL_PRESENT,
					FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_PRESENT);
		}

		MerchantKycDetailsResponse res = kycDetailAndDocs(merchantKycDetailsCheck, merchantsDetails.getMerchantID(),
				merchantLegalName, panCardNumber, GstId, webstieUrl, businessEntityType, productDescription, tanNumber,
				regName, regAddress, regPinCode, regNumber, regEmailAddress, commName, commAddress, commPinCode,
				commNumber, commEmailAddress, cancelledChequeOrAccountProof, certificateOfIncorporation, businessPAN,
				certificateOfGST, directorKYC, aoa, moa, certficateOfNBFC, certficateOfBBPS, certificateOfSEBIOrAMFI);

		return res;
	}

	public SuccessResponseDto updatMerchantKycStatus(String merchantId, String statusUpdate)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantKycDetails merchantKycDetails = merchantKycDetailsRepository.findByMerchantID(merchantId);
		if (merchantKycDetails == null) {
			throw new ValidationExceptions(MERCHANT_KYC_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_NOT_FOUND);
		}

		if (statusUpdate.equalsIgnoreCase(KycStatus.YES.toString())) {
			merchantKycDetails.setMerchantKycStatus(KycStatus.YES.toString());
		} else {
			merchantKycDetails.setMerchantKycStatus(KycStatus.NO.toString());
		}

		merchantDetails.setKycStatus(statusUpdate);
		merchantDetailsRepository.save(merchantDetails);

		merchantKycDetails.setMerchantKycStatus(statusUpdate);
		merchantKycDetailsRepository.save(merchantKycDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", merchantKycDetails);
		return sdto;
	}

	public SuccessResponseDto getMerchantKyc(String merchantId, String status) throws ValidationExceptions {

		List<MerchantKycDetails> merchantKycDetails = null;
		if (merchantId != null) {
			MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
			if (merchantDetails == null) {
				throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
			}
			merchantKycDetails = merchantKycDetailsRepository.findAllByMerchantID(merchantId);
			if (merchantKycDetails == null) {
				throw new ValidationExceptions(MERCHANT_KYC_DETAILS_NOT_FOUND,
						FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_NOT_FOUND);
			}
		} else {
			merchantKycDetails = merchantKycDetailsRepository.getKycStatusWise(status);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", merchantKycDetails);
		return sdto;
	}

	public List<AllPgDetailsResponse> getPgDetailsByPGId(String pgId, String start_date, String end_date)
			throws ValidationExceptions {

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuidWithDate(pgId,
				start_date, end_date);

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}

		return getPGDetailFunc(pgConfigurationDetails);
	}

	public List<AllPgDetailsResponse> getPGDetailFunc(PGConfigurationDetails pgConfigurationDetails) {
		List<AllPgDetailsResponse> allpglist = new ArrayList<AllPgDetailsResponse>();
		Pgdetails pgdet = new Pgdetails();
		AllPgDetailsResponse allPg = new AllPgDetailsResponse();

		pgdet.setPgAppId(pgConfigurationDetails.getPgAppId());
		pgdet.setStatus(pgConfigurationDetails.getStatus());
		pgdet.setPgId(pgConfigurationDetails.getPgUuid());

		allPg.setCreated(pgConfigurationDetails.getCreatedAt().toString());
		allPg.setPgname(pgConfigurationDetails.getPgName());
		allPg.setPgdetails(pgdet);

		List<PGServiceDetails> pgService = pgServiceDetailsRepository.findByPgId(pgConfigurationDetails.getPgUuid());
		List<Pgservices> serList = new ArrayList<Pgservices>();

		if (pgService.size() > 0) {

			for (PGServiceDetails pgs : pgService) {

				Pgservices pgse = new Pgservices();
				pgse.setServicename(pgs.getPgServices());

				ServiceDetails srcdet = new ServiceDetails();
				srcdet.setDefaultService(pgs.getDefaultService());
				srcdet.setThresholdDay(pgs.getThresoldDay());
				srcdet.setPriority(pgs.getPriority());
				srcdet.setThresholdMonth(pgs.getThresoldMonth());
				srcdet.setThresholdSixMonths(pgs.getThresold6Month());
				srcdet.setThresholdThreeMonths(pgs.getThresold3Month());
				srcdet.setThresholdWeek(pgs.getThresoldWeek());
				srcdet.setThresholdYear(pgs.getThresoldYear());

				pgse.setServiceDetails(srcdet);

				serList.add(pgse);

			}
		}

		allPg.setPgservices(serList);
		allpglist.add(allPg);
		return allpglist;
	}

	public List<AllPgDetailsResponse> getPgDetailsByPGNameAndPgId(String pgName, String pgId, String start_date,
			String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);

		if (txnParam(pgId) == true) {
			return getPgDetailsByPGId(pgId, start_date, end_date);
		} else if (txnParam(pgName) == true && txnParam(pgId) == false) {
			return getPgDetailsByPGName(pgName, start_date, end_date);
		}

		return getAllPgDetailsDateWise(start_date, end_date);
	}

	public List<AllPgDetailsResponse> findPgDetailsByPGNameAndPgId(String pgName, String pgId)
			throws ValidationExceptions {

		if (txnParam(pgId) == true) {
			return pgDetailsByPGId(pgId);
		} else if (txnParam(pgName) == true && txnParam(pgId) == false) {
			return pgDetailsByPGName(pgName);
		}

		return null;
	}

	public List<AllPgDetailsResponse> getPgDetailsByPGName(String pgName, String start_date, String end_date)
			throws ValidationExceptions {

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgNameWithDate(pgName,
				start_date, end_date);

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}

		return getPGDetailFunc(pgConfigurationDetails);
	}

	public List<AllMerchantDetails> getPgDetailsByMerchantIdAndMerchantNameDate(String merchantId, String merchantName,
			String start_date, String end_date) throws ValidationExceptions, JsonProcessingException {
		dateWiseValidation(start_date, end_date);

		if (txnParam(merchantId) == true) {
			return getMerchantByIdDate(merchantId, start_date, end_date);
		} else if (merchantId == null && merchantName != null && merchantName.isBlank() == false) {
			return getMerchantListByNameDate(merchantName, start_date, end_date);
		}

		return getAllMerchantDetailsReportDateWise(start_date, end_date);
	}

	public List<AllMerchantDetails> getMerchantIdAndMerchantName(String merchantId, String merchantName)
			throws ValidationExceptions {

		if (txnParam(merchantId) == true) {
			return getMerchantById(merchantId);
		} else if (merchantId == null && txnParam(merchantName) == true) {
			return getMerchantListByName(merchantName);
		}

		return null;
	}

	public List<AllMerchantDetails> getMerchantIdAndMerchantName2(String merchantId, String merchantName)
			throws ValidationExceptions {

		if (txnParam(merchantId) == true) {
			return getMerchantById2(merchantId);
		} else if (merchantId == null && txnParam(merchantName) == true) {
			return getMerchantListByName2(merchantName);
		}

		return null;
	}

	public List<AllMerchantDetails> getMerchantByIdDate(String merchantid, String start_date, String end_date)
			throws ValidationExceptions {

		MerchantDetails merchant = merchantDetailsRepository.findByMerchantIDWithDate(merchantid, start_date, end_date);

		if (merchant == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		return getMerchantDetailsFunc(merchant);

	}

	public List<AllMerchantDetails> getMerchantDetailsFunc(MerchantDetails merchant) {

		List<AllMerchantDetails> allmerchants = new LinkedList<>();

		AllMerchantDetails merchantByName = new AllMerchantDetails();
		merchantByName.setMerchantName(merchant.getMerchantName());
		merchantByName.setMerchantStatus(merchant.getUserStatus());
		merchantByName.setKycStatus(merchant.getKycStatus());
		merchantByName.setMerchantEMail(merchant.getMerchantEmail());
		merchantByName.setMerchantId(merchant.getMerchantID());
		merchantByName.setPhoneNumber(merchant.getPhoneNumber());

		List<MerchantPgdetails> merchantPgdetails = new ArrayList<>();
		List<MerchantPGDetails> merpglist = merchantPGDetailsRepository.findAll();

		for (MerchantPGDetails mpgd : merpglist) {
			if (mpgd.getMerchantID().equals(merchant.getMerchantID())) {
				MerchantPgdetails merpgdet = new MerchantPgdetails();

				merpgdet.setPgname(mpgd.getMerchantPGName());
				merpgdet.setPgstatus(mpgd.getStatus());
				merpgdet.setPguuid(mpgd.getMerchantPGId());

				List<MerchantServiceDetails> merchantServiceDetails = new ArrayList<>();
				List<MerchantPGServices> merserlist = merchantPGServicesRepository
						.findByMerchantIDAndPgID(merchant.getMerchantID(), mpgd.getMerchantPGId());

				for (MerchantPGServices mpgser : merserlist) {
					if (mpgd.getMerchantID().equals(mpgser.getMerchantID())) {
						MerchantServiceDetails msd = new MerchantServiceDetails();

						msd.setServiceStatus(mpgser.getStatus());
						msd.setServiceType(mpgser.getService());

						merchantServiceDetails.add(msd);
					}
				}

				merpgdet.setMerchantservicedetails(merchantServiceDetails);
				merchantPgdetails.add(merpgdet);
			}
		}

		merchantByName.setMerchantpgdetails(merchantPgdetails);
		allmerchants.add(merchantByName);

		return allmerchants;

	}

	public List<AllMerchantDetails> getMerchantDetailsWithAllPG(MerchantDetails merchant) {

		List<AllMerchantDetails> allmerchants = new LinkedList<>();

		AllMerchantDetails merchantByName = new AllMerchantDetails();
		merchantByName.setMerchantName(merchant.getMerchantName());
		merchantByName.setMerchantStatus(merchant.getUserStatus());
		merchantByName.setKycStatus(merchant.getKycStatus());
		merchantByName.setMerchantEMail(merchant.getMerchantEmail());
		merchantByName.setMerchantId(merchant.getMerchantID());
		merchantByName.setPhoneNumber(merchant.getPhoneNumber());

		List<MerchantPgdetails> merchantPgdetails = new ArrayList<>();
		List<MerchantPGDetails> merpglist = merchantPGDetailsRepository.findAll();

		for (MerchantPGDetails mpgd : merpglist) {

			MerchantPgdetails merpgdet = new MerchantPgdetails();

			merpgdet.setPgname(mpgd.getMerchantPGName());
			merpgdet.setPgstatus(mpgd.getStatus());
			merpgdet.setPguuid(mpgd.getMerchantPGId());

			List<MerchantServiceDetails> merchantServiceDetails = new ArrayList<>();
			List<MerchantPGServices> merserlist = merchantPGServicesRepository
					.findByMerchantIDAndPgID(merchant.getMerchantID(), mpgd.getMerchantPGId());

			for (MerchantPGServices mpgser : merserlist) {

				MerchantServiceDetails msd = new MerchantServiceDetails();

				msd.setServiceStatus(mpgser.getStatus());
				msd.setServiceType(mpgser.getService());

				merchantServiceDetails.add(msd);

				merpgdet.setMerchantservicedetails(merchantServiceDetails);
				merchantPgdetails.add(merpgdet);
			}
		}

		merchantByName.setMerchantpgdetails(merchantPgdetails);
		allmerchants.add(merchantByName);

		return allmerchants;

	}

	public List<TransactionDetails> txnWithParameters(String merchant_id, String payment_option, String pgType,
			String merchant_order_id, String merchant_order_ids, String trId, String pg_id, String start_date,
			String end_date, String oder_id, String trx_msg, String status) throws ValidationExceptions {
		logger.info("oderId :::::   1  " + oder_id);
		if (txnParam(merchant_order_id) == true) {
			if (merchant_order_id.length() < 8) {
				throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION,
						FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
			} else {
				if (transactionDetailsRepository.findAllByMerchantOrderId(merchant_order_id).isEmpty()) {
					throw new ValidationExceptions(ORDER_ID_NOT_FOUND,
							FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
				}
			}
		}

		if (txnParam(merchant_id) == true) {
			if (transactionDetailsRepository.findAllByMerchantId(merchant_id).isEmpty()) {
				throw new ValidationExceptions(MERCHANT_ID_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
			}
		}

		if (txnParam(start_date) == true && txnParam(end_date) == true) {
			dateWiseValidation(start_date, end_date);
		}

		if (txnParam(merchant_id) && txnParam(payment_option) && txnParam(pgType) && txnParam(merchant_order_id)
				&& txnParam(trId) && txnParam(pg_id)) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOptionAndMerIdAndMerOrAndTrIdAndPgId(merchant_id,
					payment_option, pgType, merchant_order_id, pg_id, trId);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByMerchantId(merchant_id, start_date + " 00:00:00", end_date + " 23:59:59");
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByMerIdAndPayOption(merchant_id, payment_option);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == false && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByMerIdAndPgType(merchant_id, pgType);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndMerOrId(merchant_id, merchant_order_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndPayOptAndMerOrd(merchant_id, payment_option,
					merchant_order_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == true && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndPgTyAndMerOr(merchant_id, pgType,
					merchant_order_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == true && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndPgTyAndPayOpAndMerOr(merchant_id, pgType,
					payment_option, merchant_order_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndTrIdAndPgId(merchant_id, trId, pg_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndTrId(merchant_id, trId);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndPgId(merchant_id, pg_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndMerOrIdAndTrIdAndPgId(merchant_id,
					merchant_order_id, trId, pg_id);
		} else if (txnParam(merchant_id) == true && txnParam(pgType) == true && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByMerIdAndPgTyAndPayOp(merchant_id, pgType,
					payment_option);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == true && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOptionAndMerOrAndTrIdAndPgId(payment_option,
					pgType, merchant_order_id, pg_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == true && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOptionAndMerOrAndTrIdAndPgId(payment_option,
					pgType, merchant_order_id, pg_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndMerIdAndMerOrAndTrIdAndPgId(pgType, merchant_order_id,
					pg_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByMerOrAndTrIdAndPgId(merchant_order_id, pg_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == true && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPayOptionAndMerOrAndPgId(payment_option, merchant_order_id,
					pg_id);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByPgTypeAndMerOrAndTrId(pgType, merchant_order_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByPgTypeAndMerOrAndTrIdAndPgId(pgType, merchant_order_id);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == true && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOptionAndMerIdAndMerOrAndPgId(merchant_id,
					payment_option, pgType, merchant_order_id, pg_id);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == true && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOptionAndMerIdAndMerOrAndTrId(merchant_id,
					payment_option, pgType, merchant_order_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByMerOrAndPgId(merchant_order_id, pg_id);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == true && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByMerOrAndTrId(merchant_order_id, trId);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == false) {
			return transactionDetailsRepository.findAllByTrDateByPayOpAndMerOr(payment_option, merchant_order_id);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == true
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPayOpAndPgId(payment_option, pg_id, start_date,
					end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(pg_id) == true
				&& txnParam(payment_option) == false && txnParam(merchant_order_id) == false && txnParam(trId) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.getTransactionByPgId(pg_id, start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == true && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == true
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPgTyAndPgId(pgType, pg_id, start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == true && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == true
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPgTyAndPayOpAndPgId(pgType, payment_option, pg_id,
					start_date, end_date);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == false && txnParam(pgType) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndMerIdAndMerOrAndPgId(merchant_id, pgType,
					merchant_order_id, pg_id);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == false && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == true
				&& start_date != null && end_date != null) {
			return transactionDetailsRepository.findAllByMerIdAndMerOrAndPgId(merchant_id, merchant_order_id, pg_id,
					start_date, end_date);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == true && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPayOptionAndMerIdAndMerOrAndPgId(merchant_id, payment_option,
					merchant_order_id, pg_id);
		} else if (txnParam(merchant_id) == true && txnParam(payment_option) == true && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOptionAndMerIdAndPgId(merchant_id, payment_option,
					pgType, pg_id);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == false && txnParam(pgType) == true
				&& txnParam(merchant_order_id) == true && txnParam(trId) == false && txnParam(pg_id) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndMerOrAndPgId(pgType, merchant_order_id, pg_id);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == true && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByPgTypeAndPayOption(pgType, payment_option, start_date,
					end_date);
		} else if (txnParam(merchant_id) == false && txnParam(payment_option) == true && txnParam(pgType) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByPaymentOption(payment_option, start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == true && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == false && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByPgType(pgType, start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(trId) == true
				&& txnParam(payment_option) == false && txnParam(merchant_order_id) == false && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.getTransactionByTrId(trId, start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == true
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByTrIdAndPgId(trId, pg_id, start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == true && txnParam(payment_option) == false
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == true
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPgTyAndTrIdAndPgId(pgType, trId, pg_id, start_date,
					end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == true && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPgTyAndPayOpAndTrId(pgType, payment_option, trId,
					start_date, end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == false
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPayOpAndTrId(payment_option, trId, start_date,
					end_date);
		} else if (txnParam(merchant_id) == false && txnParam(pgType) == false && txnParam(payment_option) == true
				&& txnParam(merchant_order_id) == false && txnParam(trId) == true && txnParam(pg_id) == true
				&& txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDateByPayOpAndTrIdAndPgId(payment_option, trId, pg_id,
					start_date, end_date);
		} else if (txnParam(pg_id) == true) {
			return transactionDetailsRepository.getTransactionByPgIdWithOutDate(pg_id);
		} else if (txnParam(trId)) {
			return transactionDetailsRepository.getTransactionByTrIdWithOutDate(trId);
		} else if (txnParam(merchant_order_ids) == true) {
			List<String> oderIdsListInString = new ArrayList<>(Arrays.asList(merchant_order_ids.split(",")));
			List<String> oderIdsListInString2 = new ArrayList<>();
			for (String string1 : oderIdsListInString) {
				if (string1.trim().length() > 5) {
					oderIdsListInString2.add(string1);
				}

			}
			return transactionDetailsRepository.findBymerchantOrderIdIn(oderIdsListInString2);
			// return
			// transactionDetailsRepository.findAllByMerchantOrderId(merchant_order_id);
		} else if (txnParam(pgType) == true) {
			return transactionDetailsRepository.findAllByPgTypeWithOutDate(pgType);
		} else if (txnParam(payment_option) == true) {
			return transactionDetailsRepository.findAllByPaymentOptionWithOutDate(payment_option);
		} else if (txnParam(start_date) == true && txnParam(end_date) == true) {
			return transactionDetailsRepository.findAllByTrDate(start_date + " 00:00:00", end_date + " 23:59:59");
		} else if (txnParam(start_date) == true && txnParam(end_date) == false) {
			validateDateFormat(start_date);
			return transactionDetailsRepository.getDataStartDate(start_date);
		} else if (txnParam(start_date) == false && txnParam(end_date) == true) {
			validateDateFormat(end_date);
			return transactionDetailsRepository.getDataEndDate(end_date);
		} else if (txnParam(start_date) == true && txnParam(end_date) == true && txnParam(trx_msg) == true) {
			validateDateFormat(end_date);
			return transactionDetailsRepository.findAllByTrx_msg_Like(trx_msg, start_date, end_date);
		} else if (txnParam(oder_id) == true) {
			List<String> oderIdsListInString = new ArrayList<>(Arrays.asList(oder_id.split(",")));
			List<String> oderIdsListInString2 = new ArrayList<>();
			for (String string1 : oderIdsListInString) {
				if (string1.trim().length() > 5) {
					oderIdsListInString2.add(string1);
				}
			}
			return transactionDetailsRepository.findByorderIDIn(oderIdsListInString2);
			// return transactionDetailsRepository.findByOrderID(oder_id);
		} else if (txnParam(status) == true && txnParam(start_date) == true && txnParam(end_date) == true) {

			return transactionDetailsRepository.findAllByStatusDateWise(status, start_date, end_date);

		}

		return null;
	}

	public List<AllMerchantDetails> getAllMerchantDetailsReportDateWise(String start_date, String end_date)
			throws JsonProcessingException, ValidationExceptions {
		dateWiseValidation(start_date, end_date);

		List<AllMerchantDetails> allmerchants = new LinkedList<AllMerchantDetails>();

		List<MerchantDetails> merchant = merchantDetailsRepository.getAllMerchantDetailsReportDateWise(start_date,
				end_date);

		for (MerchantDetails mer : merchant) {

			AllMerchantDetails merchantByName = new AllMerchantDetails();
			merchantByName.setMerchantName(mer.getMerchantName());
			merchantByName.setKycStatus(mer.getKycStatus());
			merchantByName.setMerchantEMail(mer.getMerchantEmail());
			merchantByName.setMerchantId(mer.getMerchantID());
			merchantByName.setPhoneNumber(mer.getPhoneNumber());

			List<MerchantPgdetails> merchantPgdetails = new ArrayList<MerchantPgdetails>();
			List<MerchantPGDetails> merpglist = merchantPGDetailsRepository.findAll();

			for (MerchantPGDetails mpgd : merpglist) {
				if (mpgd.getMerchantID().equals(mer.getMerchantID())) {
					MerchantPgdetails merpgdet = new MerchantPgdetails();

					merpgdet.setPgname(mpgd.getMerchantPGName());
					merpgdet.setPgstatus(mpgd.getStatus());
					merpgdet.setPguuid(mpgd.getMerchantPGId());

					List<MerchantServiceDetails> merchantServiceDetails = new ArrayList<MerchantServiceDetails>();
					List<MerchantPGServices> merserlist = merchantPGServicesRepository.findAll();

					for (MerchantPGServices mpgser : merserlist) {
						if (mpgd.getMerchantID().equals(mpgser.getMerchantID())) {
							MerchantServiceDetails msd = new MerchantServiceDetails();

							msd.setServiceStatus(mpgser.getStatus());
							msd.setServiceType(mpgser.getService());

							merchantServiceDetails.add(msd);
						}
					}

					merpgdet.setMerchantservicedetails(merchantServiceDetails);
					merchantPgdetails.add(merpgdet);
				}
			}

			merchantByName.setMerchantpgdetails(merchantPgdetails);
			allmerchants.add(merchantByName);
		}

		return allmerchants;
	}

	public List<AllPgDetailsResponse> getAllPgDetailsDateWise(String start_date, String end_date)
			throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);

		List<PGConfigurationDetails> pgConfigurationDetails = pgConfigurationDetailsRepository
				.getPgListDateWise(start_date, end_date);

		List<AllPgDetailsResponse> allpglist = new ArrayList<AllPgDetailsResponse>();

		for (PGConfigurationDetails pgconf : pgConfigurationDetails) {
			Pgdetails pgdet = new Pgdetails();
			AllPgDetailsResponse allPg = new AllPgDetailsResponse();

			pgdet.setPgAppId(pgconf.getPgAppId());
			pgdet.setStatus(pgconf.getStatus());
			pgdet.setPgId(pgconf.getPgUuid());

			allPg.setCreated(pgconf.getCreatedAt().toString());
			allPg.setPgname(pgconf.getPgName());
			allPg.setPgdetails(pgdet);

			List<PGServiceDetails> pgService = pgServiceDetailsRepository.findByPgId(pgconf.getPgUuid());
			List<Pgservices> serList = new ArrayList<Pgservices>();

			if (pgService.size() > 0) {

				for (PGServiceDetails pgs : pgService) {

					Pgservices pgse = new Pgservices();
					pgse.setServicename(pgs.getPgServices());

					ServiceDetails srcdet = new ServiceDetails();
					srcdet.setDefaultService(pgs.getDefaultService());
					srcdet.setThresholdDay(pgs.getThresoldDay());
					srcdet.setPriority(pgs.getPriority());
					srcdet.setThresholdMonth(pgs.getThresoldMonth());
					srcdet.setThresholdSixMonths(pgs.getThresold6Month());
					srcdet.setThresholdThreeMonths(pgs.getThresold3Month());
					srcdet.setThresholdWeek(pgs.getThresoldWeek());
					srcdet.setThresholdYear(pgs.getThresoldYear());

					pgse.setServiceDetails(srcdet);

					serList.add(pgse);

				}
			}

			allPg.setPgservices(serList);
			allpglist.add(allPg);
		}

		return allpglist;
	}

	public SuccessResponseDto updateKycDetails(String merchantId, String merchantLegalName, String panCardNumber,
			String GstId, String webstieUrl, String businessEntityType, String productDescription, String tanNumber,
			String regName, String regAddress, String regPinCode, String regNumber, String regEmailAddress,
			String commName, String commAddress, String commPinCode, String commNumber, String commEmailAddress,
			MultipartFile cancelledChequeOrAccountProof, MultipartFile certificateOfIncorporation,
			MultipartFile businessPAN, MultipartFile certificateOfGST, MultipartFile directorKYC, MultipartFile aoa,
			MultipartFile moa, MultipartFile certficateOfNBFC, MultipartFile certficateOfBBPS,
			MultipartFile certificateOfSEBIOrAMFI) throws ValidationExceptions, NoSuchAlgorithmException, IOException {

		validateKycDetail(panCardNumber, GstId, webstieUrl, tanNumber, regPinCode, regNumber, regEmailAddress);

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantKycDetails merchantKycDetails = merchantKycDetailsRepository.findByMerchantID(merchantId);
		if (merchantKycDetails == null) {
			throw new ValidationExceptions(MERCHANT_KYC_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_NOT_FOUND);
		}

		if (merchantKycDetails.getMerchantKycStatus().equalsIgnoreCase((KycStatus.APPROVED.toString()))) {
			throw new ValidationExceptions(MERCHANT_KYC_HAS_BEEN_APPROVED, FormValidationExceptionEnums.KYC_STATUS);
		}

		MerchantKycDetailsResponse res = kycDetailAndDocs(merchantKycDetails, merchantDetails.getMerchantID(),
				merchantLegalName, panCardNumber, GstId, webstieUrl, businessEntityType, productDescription, tanNumber,
				regName, regAddress, regPinCode, regNumber, regEmailAddress, commName, commAddress, commPinCode,
				commNumber, commEmailAddress, cancelledChequeOrAccountProof, certificateOfIncorporation, businessPAN,
				certificateOfGST, directorKYC, aoa, moa, certficateOfNBFC, certficateOfBBPS, certificateOfSEBIOrAMFI);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Kyc Status updated!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKycStatus", res);
		return sdto;
	}

	public void validateKycDetail(String panCardNumber, String GstId, String webstieUrl, String tanNumber,
			String regPinCode, String regNumber, String regEmailAddress) throws ValidationExceptions {
		if (!Validator.isValidEmail(regEmailAddress)) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidPhoneNumber(regNumber)) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (!Validator.isValidPinCode(regPinCode)) {
			throw new ValidationExceptions(PIN_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		if (txnParam(GstId)) {
			if (!Validator.isValidGstNumber(GstId)) {
				throw new ValidationExceptions(GSTID_VALIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		if (txnParam(panCardNumber)) {
			if (!Validator.isValidPANNumber(panCardNumber)) {
				throw new ValidationExceptions(PAN_VALIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		if (txnParam(tanNumber)) {
			if (!Validator.isValidAlphaNumber(tanNumber)) {
				throw new ValidationExceptions(TAN_VALIDATION_FAILED,
						FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
			}
		}

		if (!Validator.isValidWebUrl(webstieUrl)) {
			throw new ValidationExceptions(WEBSITE_URL_VALIDATION_FAILED,
					FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
	}

	public MerchantKycDetailsResponse kycDetailAndDocs(MerchantKycDetails merchantKycDetails, String merchantId,
			String merchantLegalName, String panCardNumber, String GstId, String webstieUrl, String businessEntityType,
			String productDescription, String tanNumber, String regName, String regAddress, String regPinCode,
			String regNumber, String regEmailAddress, String commName, String commAddress, String commPinCode,
			String commNumber, String commEmailAddress, MultipartFile cancelledChequeOrAccountProof,
			MultipartFile certificateOfIncorporation, MultipartFile businessPAN, MultipartFile certificateOfGST,
			MultipartFile directorKYC, MultipartFile aoa, MultipartFile moa, MultipartFile certficateOfNBFC,
			MultipartFile certficateOfBBPS, MultipartFile certificateOfSEBIOrAMFI)
			throws NoSuchAlgorithmException, ValidationExceptions, IOException {

		if (merchantKycDetails == null) {
			merchantKycDetails = new MerchantKycDetails();
		}

		validateKycDetail(panCardNumber, GstId, webstieUrl, tanNumber, regPinCode, regNumber, regEmailAddress);

		MerchantKycDetailsResponse merchantKycDetailsResponse = new MerchantKycDetailsResponse();

		merchantKycDetails.setMerchantID(merchantId);
		merchantKycDetails.setMerchantLegalName(merchantLegalName);
		merchantKycDetails.setPanCardNumber(panCardNumber);
		merchantKycDetails.setGstId(GstId);
		merchantKycDetails.setWebstieUrl(webstieUrl);
		merchantKycDetails.setBusinessEntityType(businessEntityType);
		merchantKycDetails.setProductDescription(productDescription);
		merchantKycDetails.setTanNumber(tanNumber);
		merchantKycDetails.setMerchantKycStatus(KycStatus.PENDING.toString());
		merchantKycDetails.setRegName(regName);
		merchantKycDetails.setRegAddress(regAddress);
		merchantKycDetails.setRegEmailAddress(regEmailAddress);
		merchantKycDetails.setRegNumber(regNumber);
		merchantKycDetails.setRegPinCode(regPinCode);
		merchantKycDetails.setCommName(commName);
		merchantKycDetails.setCommAddress(commAddress);
		merchantKycDetails.setCommEmailAddress(commEmailAddress);
		merchantKycDetails.setCommNumber(commNumber);
		merchantKycDetails.setCommPinCode(commPinCode);
		merchantKycDetails.setBusinessEntityType(businessEntityType);

		if (cancelledChequeOrAccountProof != null) {
			UploadFileResponse uploadedCancelledChequeOrAccountProof = kycFileUpload(cancelledChequeOrAccountProof,
					merchantId);
			merchantKycDetails
					.setCancelledChequeOrAccountProof(uploadedCancelledChequeOrAccountProof.getFileDownloadLink());
		} else {
			merchantKycDetails.setCancelledChequeOrAccountProof(null);
		}
		if (certficateOfBBPS != null) {
			UploadFileResponse uploadedCertficateOfBBPS = kycFileUpload(certficateOfBBPS, merchantId);
			merchantKycDetails.setCertficateOfBBPS(uploadedCertficateOfBBPS.getFileDownloadLink());
		} else {
			merchantKycDetails.setCertficateOfBBPS(null);
		}
		if (certficateOfNBFC != null) {
			UploadFileResponse uploadedCertficateOfNBFC = kycFileUpload(certficateOfNBFC, merchantId);
			merchantKycDetails.setCertficateOfNBFC(uploadedCertficateOfNBFC.getFileDownloadLink());
		} else {
			merchantKycDetails.setCertficateOfNBFC(null);
		}
		if (certificateOfGST != null) {
			UploadFileResponse uploadedCertificateOfGST = kycFileUpload(certificateOfGST, merchantId);
			merchantKycDetails.setCertificateOfGST(uploadedCertificateOfGST.getFileDownloadLink());
		} else {
			merchantKycDetails.setCertificateOfGST(null);
		}
		if (certificateOfIncorporation != null) {
			UploadFileResponse uploadedCertificateOfIncorporation = kycFileUpload(certificateOfIncorporation,
					merchantId);
			merchantKycDetails.setCertificateOfIncorporation(uploadedCertificateOfIncorporation.getFileDownloadLink());
		} else {
			merchantKycDetails.setCertificateOfIncorporation(null);
		}
		if (certificateOfSEBIOrAMFI != null) {
			UploadFileResponse uploadedCertificateOfSEBIOrAMFI = kycFileUpload(certificateOfSEBIOrAMFI, merchantId);
			merchantKycDetails.setCertificateOfSEBIOrAMFI(uploadedCertificateOfSEBIOrAMFI.getFileDownloadLink());
		} else {
			merchantKycDetails.setCertificateOfSEBIOrAMFI(null);
		}
		if (aoa != null) {
			UploadFileResponse uploadedAoa = kycFileUpload(aoa, merchantId);
			merchantKycDetails.setAoa(uploadedAoa.getFileDownloadLink());
		} else {
			merchantKycDetails.setAoa(null);
		}
		if (moa != null) {
			UploadFileResponse uploadedMoa = kycFileUpload(moa, merchantId);
			merchantKycDetails.setMoa(uploadedMoa.getFileDownloadLink());
		} else {
			merchantKycDetails.setMoa(null);
		}
		if (businessPAN != null) {
			UploadFileResponse uploadedBusinessPAN = kycFileUpload(businessPAN, merchantId);
			merchantKycDetails.setBusinessPAN(uploadedBusinessPAN.getFileDownloadLink());
		} else {
			merchantKycDetails.setBusinessPAN(null);
		}
		if (directorKYC != null) {
			UploadFileResponse uploadedDirectorKYC = kycFileUpload(directorKYC, merchantId);
			merchantKycDetails.setDirectorKYC(uploadedDirectorKYC.getFileDownloadLink());
		} else {
			merchantKycDetails.setDirectorKYC(null);
		}

		merchantKycDetails = merchantKycDetailsRepository.save(merchantKycDetails);

		merchantKycDetailsResponse.setMerchantLegalName(merchantKycDetails.getMerchantLegalName());
		merchantKycDetailsResponse.setPanCardNumber(merchantKycDetails.getPanCardNumber());
		merchantKycDetailsResponse.setGstId(merchantKycDetails.getGstId());
		merchantKycDetailsResponse.setWebstieUrl(merchantKycDetails.getWebstieUrl());
		merchantKycDetailsResponse.setBusinessEntityType(merchantKycDetails.getBusinessEntityType());
		merchantKycDetailsResponse.setProductDescription(merchantKycDetails.getProductDescription());
		merchantKycDetailsResponse.setTanNumber(merchantKycDetails.getTanNumber());
		merchantKycDetailsResponse.setRegName(merchantKycDetails.getRegName());
		merchantKycDetailsResponse.setRegAddress(merchantKycDetails.getRegAddress());
		merchantKycDetailsResponse.setRegEmailAddress(merchantKycDetails.getRegEmailAddress());
		merchantKycDetailsResponse.setRegNumber(merchantKycDetails.getRegNumber());
		merchantKycDetailsResponse.setRegPinCode(merchantKycDetails.getRegPinCode());
		merchantKycDetailsResponse.setCommName(merchantKycDetails.getCommName());
		merchantKycDetailsResponse.setCommAddress(merchantKycDetails.getCommAddress());
		merchantKycDetailsResponse.setCommEmailAddress(merchantKycDetails.getCommEmailAddress());
		merchantKycDetailsResponse.setCommNumber(merchantKycDetails.getCommNumber());
		merchantKycDetailsResponse.setCommPinCode(merchantKycDetails.getCommPinCode());
		merchantKycDetailsResponse.setBusinessEntityType(merchantKycDetails.getBusinessEntityType());
		merchantKycDetailsResponse
				.setCancelledChequeOrAccountProof(merchantKycDetails.getCancelledChequeOrAccountProof());
		merchantKycDetailsResponse.setCertficateOfBBPS(merchantKycDetails.getCertficateOfBBPS());
		merchantKycDetailsResponse.setCertficateOfNBFC(merchantKycDetails.getCertficateOfNBFC());
		merchantKycDetailsResponse.setCertificateOfGST(merchantKycDetails.getCertificateOfGST());
		merchantKycDetailsResponse.setCertificateOfIncorporation(merchantKycDetails.getCertificateOfIncorporation());
		merchantKycDetailsResponse.setCertificateOfSEBIOrAMFI(merchantKycDetails.getCertificateOfSEBIOrAMFI());
		merchantKycDetailsResponse.setAoa(merchantKycDetails.getAoa());
		merchantKycDetailsResponse.setMoa(merchantKycDetails.getMoa());
		merchantKycDetailsResponse.setBusinessPAN(merchantKycDetails.getBusinessPAN());
		merchantKycDetailsResponse.setDirectorKYC(merchantKycDetails.getDirectorKYC());

		return merchantKycDetailsResponse;
	}

	public SuccessResponseDto getMerchantsKyc(String start_date, String end_date) throws ValidationExceptions {

		dateWiseValidation(start_date, end_date);
		List<MerchantKycDetails> merchantKycDetails = merchantKycDetailsRepository.getKycDateWise(start_date, end_date);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchants Kyc Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKycDetails", merchantKycDetails);
		return sdto;
	}

	public SuccessResponseDto merchantsKycApproveOrReject(String merchantId, String status, String kycReason)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		MerchantKycDetails merchantKycDetails = merchantKycDetailsRepository.findByMerchantID(merchantId);
		if (merchantKycDetails == null) {
			throw new ValidationExceptions(MERCHANT_KYC_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_NOT_FOUND);
		}

		merchantDetails.setKycStatus(status);
		merchantDetailsRepository.save(merchantDetails);
		// merchantDetailsRepository.getMerchantKycStatusChange(merchantId, status);
		merchantKycDetails.setMerchantKycStatus(status);
		merchantKycDetails.setKycComment(kycReason);
		MerchantKycDetails merchantKycStatus = merchantKycDetailsRepository.save(merchantKycDetails);
		sendMail.sendMailForMerchantKycStatus(merchantDetails, merchantKycDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add(
				"Merchants Kyc Status Updated Successfully, Mail has been Sent to the Registered Merchant E-Mail id!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKycDetails", merchantKycStatus);
		return sdto;
	}

	public boolean validateDateFormat(String dateToValdate) throws ValidationExceptions {

		if (dateToValdate.isBlank() || dateToValdate.isEmpty()) {
			throw new ValidationExceptions(DATE_PARAMETER_IS_MANDATORY,
					FormValidationExceptionEnums.DATE_PARAMETER_IS_MANDATORY);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setLenient(false);
		Date parsedDate = null;
		try {
			parsedDate = formatter.parse(dateToValdate);
			// System.out.println("++validated DATE TIME ++"+formatter.format(parsedDate));

		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public void dateWiseValidation(String start_date, String end_date) throws ValidationExceptions {
		boolean checkStartFormat = validateDateFormat(start_date);
		boolean checkEndFormat = validateDateFormat(end_date);

		if (checkStartFormat == false || checkEndFormat == false || start_date.trim().contains(" ")
				|| end_date.trim().contains(" ") || start_date.matches(".*[a-zA-Z]+.*")
				|| end_date.matches(".*[a-zA-Z]+.*")) {
			throw new ValidationExceptions(DATE_FORMAT, FormValidationExceptionEnums.DATE_FORMAT);
		}

	}

	public boolean txnParam(String val) {
		if (val == null || val.isBlank() || val.isEmpty()) {
			return false;
		}
		return true;
	}

	public List<AllMerchantDetails> getMerchantById(String merchantid) throws ValidationExceptions {

		MerchantDetails merchant = merchantDetailsRepository.findByMerchantID(merchantid);

		if (merchant == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		return getMerchantDetailsFunc(merchant);
	}

	public List<AllMerchantDetails> getMerchantListByName(String merchantName) throws ValidationExceptions {

		List<MerchantDetails> merchantList = merchantDetailsRepository.findAll();

		if (merchantList == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		return getMerchantDetailByNameFunc(merchantList, merchantName);

	}

	public List<AllMerchantDetails> getMerchantById2(String merchantid) throws ValidationExceptions {

		MerchantDetails merchant = merchantDetailsRepository.findByMerchantID(merchantid);

		if (merchant == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		return getMerchantDetailsWithAllPG(merchant);
	}

	public List<AllMerchantDetails> getMerchantListByName2(String merchantName) throws ValidationExceptions {

		List<MerchantDetails> merchantList = merchantDetailsRepository.findAll();

		if (merchantList == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		return getMerchantDetailByNameFunc2(merchantList, merchantName);

	}

	public List<AllMerchantDetails> getMerchantDetailByNameFunc(List<MerchantDetails> merchantList,
			String merchantName) {
		List<AllMerchantDetails> allmerchants = new LinkedList<>();

		for (MerchantDetails mer : merchantList) {

			if (merchantName.equals(mer.getMerchantName())) {

				AllMerchantDetails merchantlistByName = new AllMerchantDetails();
				merchantlistByName.setMerchantName(mer.getMerchantName());
				merchantlistByName.setKycStatus(mer.getKycStatus());
				merchantlistByName.setMerchantStatus(mer.getUserStatus());
				merchantlistByName.setMerchantEMail(mer.getMerchantEmail());
				merchantlistByName.setMerchantId(mer.getMerchantID());
				merchantlistByName.setPhoneNumber(mer.getPhoneNumber());

				List<MerchantPgdetails> merchantPgdetails = new ArrayList<>();
				List<MerchantPGDetails> merpglist = merchantPGDetailsRepository.findAll();

				for (MerchantPGDetails mpgd : merpglist) {
					if (mpgd.getMerchantID().equals(mer.getMerchantID())) {
						MerchantPgdetails merpgdet = new MerchantPgdetails();

						merpgdet.setPgname(mpgd.getMerchantPGName());
						merpgdet.setPgstatus(mpgd.getStatus());
						merpgdet.setPguuid(mpgd.getMerchantPGId());

						List<MerchantServiceDetails> merchantServiceDetails = new ArrayList<>();
						List<MerchantPGServices> merserlist = merchantPGServicesRepository.findAll();

						for (MerchantPGServices mpgser : merserlist) {
							if (mpgd.getMerchantID().equals(mpgser.getMerchantID())) {
								MerchantServiceDetails msd = new MerchantServiceDetails();

								msd.setServiceStatus(mpgser.getStatus());
								msd.setServiceType(mpgser.getService());

								merchantServiceDetails.add(msd);
							}
						}

						merpgdet.setMerchantservicedetails(merchantServiceDetails);
						merchantPgdetails.add(merpgdet);
					}
				}

				merchantlistByName.setMerchantpgdetails(merchantPgdetails);
				allmerchants.add(merchantlistByName);
			}

		}

		return allmerchants;
	}

	public List<AllMerchantDetails> getMerchantDetailByNameFunc2(List<MerchantDetails> merchantList,
			String merchantName) {
		List<AllMerchantDetails> allmerchants = new LinkedList<>();

		for (MerchantDetails mer : merchantList) {

			if (merchantName.equals(mer.getMerchantName())) {

				AllMerchantDetails merchantlistByName = new AllMerchantDetails();
				merchantlistByName.setMerchantName(mer.getMerchantName());
				merchantlistByName.setKycStatus(mer.getKycStatus());
				merchantlistByName.setMerchantStatus(mer.getUserStatus());
				merchantlistByName.setMerchantEMail(mer.getMerchantEmail());
				merchantlistByName.setMerchantId(mer.getMerchantID());
				merchantlistByName.setPhoneNumber(mer.getPhoneNumber());

				List<MerchantPgdetails> merchantPgdetails = new ArrayList<>();
				List<MerchantPGDetails> merpglist = merchantPGDetailsRepository.findAll();

				for (MerchantPGDetails mpgd : merpglist) {
					if (mpgd.getMerchantID().equals(mer.getMerchantID())) {
						MerchantPgdetails merpgdet = new MerchantPgdetails();

						merpgdet.setPgname(mpgd.getMerchantPGName());
						merpgdet.setPgstatus(mpgd.getStatus());
						merpgdet.setPguuid(mpgd.getMerchantPGId());

						List<MerchantServiceDetails> merchantServiceDetails = new ArrayList<>();
						List<MerchantPGServices> merserlist = merchantPGServicesRepository.findAll();

						for (MerchantPGServices mpgser : merserlist) {

							MerchantServiceDetails msd = new MerchantServiceDetails();

							msd.setServiceStatus(mpgser.getStatus());
							msd.setServiceType(mpgser.getService());

							merchantServiceDetails.add(msd);

						}

						merpgdet.setMerchantservicedetails(merchantServiceDetails);
						merchantPgdetails.add(merpgdet);
					}
				}

				merchantlistByName.setMerchantpgdetails(merchantPgdetails);
				allmerchants.add(merchantlistByName);
			}

		}

		return allmerchants;
	}

	public List<AllPgDetailsResponse> pgDetailsByPGId(String pgId) throws ValidationExceptions {

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgId);

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}

		return getPGDetailFunc(pgConfigurationDetails);
	}

	public List<AllPgDetailsResponse> pgDetailsByPGName(String pgName) throws ValidationExceptions {

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgName(pgName);

		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_INFORMATION_NOT_FOUND);
		}

		return getPGDetailFunc(pgConfigurationDetails);
	}

	public SuccessResponseDto totalTransaction(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails",
				transactionDetailsRepository.getAllTxnDateWise(start_date, end_date));
		return sdto;
	}

	public Object totalPayOptTransaction(String payment_option, String start_date, String end_date)
			throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		if (payment_option.equalsIgnoreCase("UPI")) {
			return transactionDetailsRepository.getAllUPITxnDateWise(payment_option, start_date, end_date);
		} else if (payment_option.equalsIgnoreCase("WALLET")) {
			return transactionDetailsRepository.getAllWalletTxnDateWise(payment_option, start_date, end_date);
		} else if (payment_option.equalsIgnoreCase("NB")) {
			return transactionDetailsRepository.getAllNBTxnDateWise(payment_option, start_date, end_date);
		} else if (payment_option.equalsIgnoreCase("CARD")) {
			return transactionDetailsRepository.getAllCardTxnDateWise(payment_option, start_date, end_date);
		}
		return transactionDetailsRepository.getAllTxnDateWise(start_date, end_date);
	}

	public Object getAllSumByPaymentOption(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);

		return transactionDetailsRepository.getAllSumByPaymentOption(start_date, end_date);
	}

	public SuccessResponseDto totalRefund(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetails", refundDetailsRepository.getAllRefundDateWise(start_date, end_date));
		return sdto;
	}

	public SuccessResponseDto totalCancelledTransaction(String start_date, String end_date)
			throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails",
				transactionDetailsRepository.getAllCancelledTxnDateWise(start_date, end_date));
		return sdto;
	}

	public List<TransactionDetails> getTrxTop100() {
		return transactionDetailsRepository.getTrxTop100();
	}

	public SuccessResponseDto totalSettledAndUnsettledAmount(String start_date, String end_date, String status)
			throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		String res = "";
		if (status.equalsIgnoreCase("SETTLED")) {
			res = merchantBalanceSheetRepository.getTotalSettled(start_date, end_date);
		} else if (status.equalsIgnoreCase("PENDING")) {
			res = merchantBalanceSheetRepository.getTotalUnSettled(start_date, end_date);
		} else {
			throw new ValidationExceptions("Provided status input is not accepted!",
					FormValidationExceptionEnums.FATAL_EXCEPTION);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("settlementDetails", res);
		return sdto;
	}

	public SuccessResponseDto totalUnSettledAmount(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("settlementDetails",
				merchantBalanceSheetRepository.getTotalUnSettled(start_date, end_date));
		return sdto;
	}

	public SuccessResponseDto totalCardPaymentAmount(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("cardDetails",
				cardPaymentDetailsRepository.getAllCardPaymentDateWise(start_date, end_date));
		return sdto;
	}

	public SuccessResponseDto totalhitTransaction(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails",
				transactionDetailsRepository.getAllHitTxnDateWise(start_date, end_date));
		return sdto;
	}

	public SuccessResponseDto totalCapturedTransaction(String start_date, String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails",
				transactionDetailsRepository.getAllCapturedTxnDateWise(start_date, end_date));
		return sdto;
	}

	public String totalTxnWithStatus(String status) {
		if (status.equalsIgnoreCase("SUCCESS")) {
			return transactionDetailsRepository.getAllTxnWithSuccessStatus(status);
		} else if (status.equalsIgnoreCase("FAILED")) {
			return transactionDetailsRepository.getAllTxnWithFailedStatus(status);
		} else if (status.equalsIgnoreCase("Captured")) {
			return transactionDetailsRepository.getAllTxnWithCapturedStatus(status);
		} else if (status.equalsIgnoreCase("Cancelled")) {
			return transactionDetailsRepository.getAllTxnWithCancelledStatus(status);
		}

		return null;
	}

	public SuccessResponseDto totalNumberOfMerchants() {
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", merchantDetailsRepository.findTotalNoOfMerchants());
		return sdto;
	}

	public Object totalPayModeTransaction(String payment_mode, String start_date, String end_date)
			throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);
		if (payment_mode.equalsIgnoreCase("UPI")) {
			return transactionDetailsRepository.getAllUPITxnModeDateWise(payment_mode, start_date, end_date);
		} else if (payment_mode.equalsIgnoreCase("WALLET")) {
			return transactionDetailsRepository.getAllWalletTxnModeDateWise(payment_mode, start_date, end_date);
		} else if (payment_mode.equalsIgnoreCase("NB")) {
			return transactionDetailsRepository.getAllNBTxnModeDateWise(payment_mode, start_date, end_date);
		} else if (payment_mode.equalsIgnoreCase("CARD")) {
			return transactionDetailsRepository.getAllCardTxnModeDateWise(payment_mode, start_date, end_date);
		}
		return transactionDetailsRepository.getAllTxnDateWise(start_date, end_date);
	}

	public SuccessResponseDto getTopTxnByMerchantId(String merchantid) throws ValidationExceptions {

		MerchantDetails merchant = merchantDetailsRepository.findByMerchantID(merchantid);

		if (merchant == null) {
			throw new ValidationExceptions(MERCHANT_INFORMATION_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", transactionDetailsRepository.findAllTopTxnByMerchantId(merchantid));
		return sdto;
	}

	public List<TicketComplaintDetails> getComplaint(String status, String complaintid, String start_date,
			String end_date) throws ValidationExceptions {
		dateWiseValidation(start_date, end_date);

		if (txnParam(status) == true && txnParam(complaintid) == false) {
			return ticketComplaintDetailsRepository.getComplaintByStatusWithDate(status, start_date, end_date);
		} else if (txnParam(status) == false && txnParam(complaintid) == true) {
			return ticketComplaintDetailsRepository.getComplaintById(complaintid, start_date, end_date);
		} else if (txnParam(status) == true && txnParam(complaintid) == true) {
			return ticketComplaintDetailsRepository.getComplaintByIdAndStatus(status, complaintid, start_date,
					end_date);
		}

		return ticketComplaintDetailsRepository.getComplaintByDate(start_date, end_date);
	}

	public SuccessResponseDto getTopMerchantTxn() {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", transactionDetailsRepository.findAllTopMerchantTxn());
		return sdto;
	}

	public MerchantKycDocRes merchantKycDocs(String merchantId, MultipartFile cancelledChequeOrAccountProof,
			MultipartFile certificateOfIncorporation, MultipartFile businessPAN, MultipartFile certificateOfGST,
			MultipartFile directorKYC, MultipartFile aoa, MultipartFile moa, MultipartFile certficateOfNBFC,
			MultipartFile certficateOfBBPS, MultipartFile certificateOfSEBIOrAMFI)
			throws ValidationExceptions, IllegalAccessException, NoSuchAlgorithmException, IOException {

		// MerchantKycDetailsRequest merchantKycDetailsRequest = new
		// MerchantKycDetailsRequest();
		MerchantKycDocRes merchantKycDetailsResponse = new MerchantKycDocRes();
		MerchantKycDetails merchantKycDetails = new MerchantKycDetails();

		MerchantDetails merchantsDetails = new MerchantDetails();
		merchantsDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantsDetails == null) {

			throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		MerchantKycDetails merchantKycDetailsCheck = new MerchantKycDetails();
		merchantKycDetailsCheck = merchantKycDetailsRepository.findByMerchantID(merchantsDetails.getMerchantID());
		if (merchantKycDetailsCheck == null) {
			throw new ValidationExceptions(MERCHANT_KYC_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_NOT_FOUND);
		}

		UploadFileResponse uploadedCancelledChequeOrAccountProof = kycFileUpload(cancelledChequeOrAccountProof,
				merchantId);
		UploadFileResponse uploadedCertficateOfBBPS = kycFileUpload(certficateOfBBPS, merchantId);
		UploadFileResponse uploadedCertficateOfNBFC = kycFileUpload(certficateOfNBFC, merchantId);
		UploadFileResponse uploadedCertificateOfGST = kycFileUpload(certificateOfGST, merchantId);
		UploadFileResponse uploadedCertificateOfIncorporation = kycFileUpload(certificateOfIncorporation, merchantId);
		UploadFileResponse uploadedCertificateOfSEBIOrAMFI = kycFileUpload(certificateOfSEBIOrAMFI, merchantId);
		UploadFileResponse uploadedAoa = kycFileUpload(aoa, merchantId);
		UploadFileResponse uploadedMoa = kycFileUpload(moa, merchantId);
		UploadFileResponse uploadedBusinessPAN = kycFileUpload(businessPAN, merchantId);
		UploadFileResponse uploadedDirectorKYC = kycFileUpload(directorKYC, merchantId);

		merchantKycDetailsCheck
				.setCancelledChequeOrAccountProof(uploadedCancelledChequeOrAccountProof.getFileDownloadLink());
		merchantKycDetailsCheck.setCertficateOfBBPS(uploadedCertficateOfBBPS.getFileDownloadLink());
		merchantKycDetailsCheck.setCertficateOfNBFC(uploadedCertficateOfNBFC.getFileDownloadLink());
		merchantKycDetailsCheck.setCertificateOfGST(uploadedCertificateOfGST.getFileDownloadLink());
		merchantKycDetailsCheck.setCertificateOfIncorporation(uploadedCertificateOfIncorporation.getFileDownloadLink());
		merchantKycDetailsCheck.setCertificateOfSEBIOrAMFI(uploadedCertificateOfSEBIOrAMFI.getFileDownloadLink());
		merchantKycDetailsCheck.setAoa(uploadedAoa.getFileDownloadLink());
		merchantKycDetailsCheck.setMoa(uploadedMoa.getFileDownloadLink());
		merchantKycDetailsCheck.setBusinessPAN(uploadedBusinessPAN.getFileDownloadLink());
		merchantKycDetailsCheck.setDirectorKYC(uploadedDirectorKYC.getFileDownloadLink());

		merchantKycDetails = merchantKycDetailsRepository.save(merchantKycDetailsCheck);

		merchantKycDetailsResponse
				.setCancelledChequeOrAccountProof(merchantKycDetails.getCancelledChequeOrAccountProof());
		merchantKycDetailsResponse.setCertficateOfBBPS(merchantKycDetails.getCertficateOfBBPS());
		merchantKycDetailsResponse.setCertficateOfNBFC(merchantKycDetails.getCertficateOfNBFC());
		merchantKycDetailsResponse.setCertificateOfGST(merchantKycDetails.getCertificateOfGST());
		merchantKycDetailsResponse.setCertificateOfIncorporation(merchantKycDetails.getCertificateOfIncorporation());
		merchantKycDetailsResponse.setCertificateOfSEBIOrAMFI(merchantKycDetails.getCertificateOfSEBIOrAMFI());
		merchantKycDetailsResponse.setAoa(merchantKycDetails.getAoa());
		merchantKycDetailsResponse.setMoa(merchantKycDetails.getMoa());
		merchantKycDetailsResponse.setBusinessPAN(merchantKycDetails.getBusinessPAN());
		merchantKycDetailsResponse.setDirectorKYC(merchantKycDetails.getDirectorKYC());

		return merchantKycDetailsResponse;

	}

	public UploadFileResponse kycFileUpload(MultipartFile file, String merchantid)
			throws ValidationExceptions, NoSuchAlgorithmException, IOException {
		logger.info("kycFileUpload Service");
		if (file.getSize() > 104857600) {
			throw new ValidationExceptions(INVALID_FILE_SIZE, FormValidationExceptionEnums.INVALID_FILE_SIZE);
		}

		String fileName = fileStorageService.storeFile(file,
				Utility.randomStringGenerator(10) + "_" + merchantid + "_" + file.getOriginalFilename());

		logger.info(fileName);
		String[] fl = fileName.split("\\|");
		FileLoading fileLoading = new FileLoading();
		fileLoading.setFileName(fl[0]);
		fileLoading.setFileHash(fl[1]);
		fileLoading.setFileSize(String.valueOf(file.getSize()));
		fileLoading.setFileStatus("UPLOADED");
		fileLoading.setFileType(file.getContentType());
		fileLoading.setPurpose("MERCHANT_KYC_DOC");
		fileLoading.setMerchantid(merchantid);
		fileUploadRepo.save(fileLoading);
		logger.info("Sent for Processing");

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/admin/downloadFile/")
				.path(fl[0]).toUriString();

		return new UploadFileResponse(fl[0], fileDownloadUri, "UPLOAD SUCCESS");

	}

	public AdminDetailDto adminDetails(String uuid) throws UserException {

		AdminDetailDto admin = new AdminDetailDto();
		UserAdminDetails user = userAdminDetailsRepository.findByuuid(uuid);
		if (user == null) {
			logger.error("User does not exist");
			throw new UserException("User does not exist", FormValidationExceptionEnums.USER_NOT_FOUND);
		}

		admin.setUserName(user.getUserName());
		admin.setPhoneNumber(user.getPhoneNumber());
		admin.setEmailId(user.getEmailId());

		return admin;
	}

	public UploadFileResponse txnreport(String start_date, String end_date) {
		List<String> data = new ArrayList<>();
		UploadFileResponse res = new UploadFileResponse();
		data.add("id" + " , " + "created" + " , " + "updated" + " , " + "amount" + " , " + "email_id" + " , "
				+ "merchant_alerturl" + " , " + "order_note" + " , " + "recon_status" + " , " + "source" + " , "
				+ "card_number" + " , " + "cust_order_id" + " , " + "merchant_id" + " , " + "merchant_order_id" + " , "
				+ "merchant_returnurl" + " , " + "orderid" + " , " + "payment_code" + " , " + "payment_mode" + " , "
				+ "payment_option" + " , " + "pg_id" + " , " + "pg_orderid" + " , " + "pg_type" + " , " + "status"
				+ " , " + "txt_msg" + " , " + "txtpgtime" + " , " + "userid" + " , " + "vpaupi");
		try {
			Connection con = null;
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://ls-c9b9fb86b0521860e0266ba424ee2b6e197b8631.cahohxo65owr.ap-south-1.rds.amazonaws.com:3306/pgdbnprod",
					"dbmasteruser", "8lT;$`rGd|?|,F?Cws1b1H+rC.)6]gE}");

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(
					"select tr.id, tr.created, tr.updated, tr.amount,tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
							+ " from transaction_details tr where date(created) between '" + start_date + "' and '"
							+ end_date + "'");

			while (rs.next()) {
				String id = rs.getString("id");
				String created = rs.getString("created");
				String updated = rs.getString("updated");
				String amount = rs.getString("amount");
				String email_id = rs.getString("email_id");
				String merchant_alerturl = rs.getString("merchant_alerturl");
				String order_note = rs.getString("order_note");
				String recon_status = rs.getString("recon_status");
				String source = rs.getString("source");
				String card_number = rs.getString("card_number");
				String cust_order_id = rs.getString("cust_order_id");
				String merchant_id = rs.getString("merchant_id");
				String merchant_order_id = rs.getString("merchant_order_id");
				String merchant_returnurl = rs.getString("merchant_returnurl");
				String orderid = rs.getString("orderid");
				String payment_code = rs.getString("payment_code");
				String payment_mode = rs.getString("payment_mode");
				String payment_option = rs.getString("payment_option");
				String pg_id = rs.getString("pg_id");
				String pg_orderid = rs.getString("pg_orderid");
				String pg_type = rs.getString("pg_type");
				String status = rs.getString("status");
				String txt_msg = rs.getString("txt_msg");
				String txtpgtime = rs.getString("txtpgtime");
				String userid = rs.getString("userid");
				String vpaupi = rs.getString("vpaupi");
				data.add(id + " , " + created + " , " + updated + " , " + amount + " , " + email_id + " , "
						+ merchant_alerturl + " , " + order_note + " , " + recon_status + " , " + source + " , "
						+ card_number + " , " + cust_order_id + " , " + merchant_id + " , " + merchant_order_id + " , "
						+ merchant_returnurl + " , " + orderid + " , " + payment_code + " , " + payment_mode + " , "
						+ payment_option + " , " + pg_id + " , " + pg_orderid + " , " + pg_type + " , " + status + " , "
						+ txt_msg + " , " + txtpgtime + " , " + userid + " , " + vpaupi);

			}

			// logger.info(data+"");

			res = writeToFile(data, fileStorage + "/transactionReport"
					+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv");
			rs.close();
			st.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return res;
	}

	private static UploadFileResponse writeToFile(List<String> list, String path) {
		FileWriter writedata = null;
		String filename = "";
		String fileDownloadUri = "";
		try {
			logger.info("\n\n\n " + path);
			writedata = new FileWriter(path);
			for (Object s : list) {
				writedata.write((String) s);
				writedata.write("\n");
			}
			writedata.close();

			String[] fl = path.split("\\/");
			filename = fl[fl.length - 1];
			logger.info("file has been created " + filename);

			fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/admin/downloadFile/")
					.path(filename).toUriString();
			logger.info("\n\n " + fileDownloadUri);
		} catch (IOException e) {
			throw new FileStorageException("Could not store file.",
					FormValidationExceptionEnums.FILE_STORAGE_EXCEPTION);
		}

		return new UploadFileResponse(filename, fileDownloadUri, "UPLOAD SUCCESS");

	}

	@Autowired
	TransactionChangeRequestRepo transactionChangeRequestRepo;
	int scount = 0;
	int fcount = 0;

	@SuppressWarnings("deprecation")
	public TransactionChangeResponceListDto updateTransactionStatus(
			TransactionChangeRequestDto transactionChangeRequestDto, String uuid) throws ValidationExceptions {
		TransactionChangeResponceListDto resDto = new TransactionChangeResponceListDto();
		List<UpdateTransactionDetailsRequestDto> updateSuccessDataDto = new ArrayList<>();
		List<UpdateTransactionDetailsRequestDto> updateFailedDataDto = new ArrayList<>();
		List<UpdateTransactionDetailsRequestDto> updatePendingDataDto = new ArrayList<>();
		List<UpdateTransactionDetailsRequestDto> updateRefundDataDto = new ArrayList<>();
		List<String> successOrderIds = new ArrayList<>();
		List<String> failedOrderIds = new ArrayList<>();
		List<String> pendingOrderIds = new ArrayList<>();
		List<String> refundOrderIds = new ArrayList<>();
		transactionChangeRequestDto.getUpdateDataDto().forEach(o -> {
			if (o.getTransactionStatus().equals("SUCCESS")) {
				updateSuccessDataDto.add(o);
				successOrderIds.add(o.getInternalOrderId());
			}
			if (o.getTransactionStatus().equals("FAILURE")) {
				updateFailedDataDto.add(o);
				failedOrderIds.add(o.getInternalOrderId());
			}
			if (o.getTransactionStatus().equals("PENDING")) {
				updatePendingDataDto.add(o);
				pendingOrderIds.add(o.getInternalOrderId());
			}
			if (o.getTransactionStatus().equals("REFUND")) {
				updateRefundDataDto.add(o);
				refundOrderIds.add(o.getInternalOrderId());
			}
		});
		if (!updateSuccessDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updateSuccessDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", successOrderIds));
			transactionChangeRequest.setStatus(updateSuccessDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updateSuccessDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updateSuccessDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(successOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(successOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setSuccessDataTransactionChangeResponce(transactionChangeResponce);
		}
		if (!updateFailedDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updateFailedDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", failedOrderIds));
			transactionChangeRequest.setStatus(updateFailedDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updateFailedDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updateFailedDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(failedOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(failedOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setFailedDataTransactionChangeResponce(transactionChangeResponce);
		}
		if (!updatePendingDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updatePendingDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", pendingOrderIds));
			transactionChangeRequest.setStatus(updatePendingDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updatePendingDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updatePendingDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(pendingOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(pendingOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setPendingDataTransactionChangeResponce(transactionChangeResponce);
		}
		if (!updateRefundDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updateRefundDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", refundOrderIds));
			transactionChangeRequest.setStatus(updateRefundDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updateRefundDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updateRefundDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(refundOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(refundOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setRefundDataTransactionChangeResponce(transactionChangeResponce);
		}
		return resDto;
	}

	public List<TransactionChangeRequest> getallTransactionChangeRequest() {
		return transactionChangeRequestRepo.findAll();
	}

	public SuccessResponseDto merchantStatusList() {

		List<IMerchantList> listOfIMerchantList = merchantDetailsAddRepository.getCompleteMerchantList();
		/*
		 * List<MerchantDetails> listOfMerchantDetails = new ArrayList<>();
		 * for(IMerchantList l: listOfIMerchantList) { MerchantDetails merchantDetails =
		 * new MerchantDetails(); merchantDetails.setMerchantID(l.getMerchantId());
		 * merchantDetails.setMerchantEmail(l.getMerchantEMail());
		 * merchantDetails.setPhoneNumber(l.getPhone_number());
		 * merchantDetails.setMerchantName(l.getMerchant_name());
		 * merchantDetails.setSecretId(Encryption.decryptCardNumberOrExpOrCvvKMS(l.
		 * getSecret_id())); merchantDetails.setAppID(l.getAppid());
		 * merchantDetails.setUserStatus(l.getUser_status());
		 * merchantDetails.setKycStatus(l.getKyc_status());
		 * merchantDetails.setSaltKey(l.getSalt_key());
		 * merchantDetails.setUuid(l.getUuid());
		 * listOfMerchantDetails.add(merchantDetails);
		 * 
		 * }
		 */
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", listOfIMerchantList);
		return sdto;
	}

	public List<MerchantPGServices> getMerchantPGServiceDetailsByID(String uuid, String merchantId) {
		return merchantPGServicesRepository.findByMerchantID(merchantId);

	}
	// PayIn merchant block api

	public String payInMerchantBlock(String status, String merchantId) throws ValidationExceptions {
		ArrayList<String> satuslist = new ArrayList<>();

		satuslist.add("ACTIVE");
		satuslist.add("BLOCKED");

		if (!satuslist.contains(status)) {
			throw new ValidationExceptions("plz enter valid Status", FormValidationExceptionEnums.COMPLAIN_STATUS);
		}
		MerchantDetails merchantDetails = new MerchantDetails();
		merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		merchantDetails.setPayinFlag(status);
		merchantDetails = merchantDetailsRepository.save(merchantDetails);
		return "merchantId::" + merchantDetails.getMerchantID() + " || PayInStatus::" + merchantDetails.getPayinFlag();

	}

	public List<TransactionDetails> findByStatusAndDateAndMerchantID(String start_date, String status) {
		return transactionDetailsRepository.findAllByStatusAndDateAndMerchantID(start_date, status);
	}

	public void getAdminSession(UserSessionDto userSessionDto, UserAdminDetails userAdminDetails)
			throws ValidationExceptions {
		UserSession session = sessionRepo.findBysessionToken(userSessionDto.getSessionToken());
		if (session == null) {
			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		if (session.getSessionStatus() == 0) {
			throw new ValidationExceptions(SESSION_DEAD, FormValidationExceptionEnums.SESSION_DEAD);
		}
	}

}
