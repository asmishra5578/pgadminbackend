package com.asktech.admin.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.customInterface.ICustomerAPIReport;
import com.asktech.admin.customInterface.IMerchantDetailsReport;
import com.asktech.admin.customInterface.IMerchantList;
import com.asktech.admin.customInterface.IMerchantSettlement;
import com.asktech.admin.customInterface.IMerchantTransaction;
import com.asktech.admin.customInterface.ISettlementBalanceReport;
import com.asktech.admin.customInterface.IUserDetails;
import com.asktech.admin.customInterface.payout.IMerchantWalletDetails;
import com.asktech.admin.customInterface.payout.MerchanSerectAppId;
import com.asktech.admin.dto.merchant.DashBoardDetails;
import com.asktech.admin.dto.merchant.MerchantResponse;
import com.asktech.admin.dto.merchant.MerchantSettlement;
import com.asktech.admin.dto.merchant.TransactionDetailsDto;
import com.asktech.admin.dto.payout.beneficiary.AssociateBankDetails;
import com.asktech.admin.dto.payout.beneficiary.CreateBeneficiaryRequest;
import com.asktech.admin.dto.payout.beneficiary.DeleteBeneficiaryRequest;
import com.asktech.admin.dto.payout.beneficiary.VerifyBankAccount;
import com.asktech.admin.dto.payout.beneficiary.VerifyBankAccountResponse;
import com.asktech.admin.dto.report.UserDetailsReport;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.UserStatus;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.mail.MailIntegration;
import com.asktech.admin.model.MerchantBalanceSheet;
import com.asktech.admin.model.MerchantBankDetails;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantRequest4Customer;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.model.payout.MerchantBeneficiaryDetails;
import com.asktech.admin.repository.CommissionStructureRepository;
import com.asktech.admin.repository.MerchantBalanceSheetRepository;
import com.asktech.admin.repository.MerchantBankDetailsRepository;
import com.asktech.admin.repository.MerchantDashBoardBalanceRepository;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.MerchantPGDetailsRepository;
import com.asktech.admin.repository.MerchantPGServicesRepository;
import com.asktech.admin.repository.MerchantRequest4CustomerRepository;
import com.asktech.admin.repository.TransactionDetailsRepository;
import com.asktech.admin.repository.UserDetailsRepository;
import com.asktech.admin.repository.payout.MerchantBeneficiaryDetailsRepo;
import com.asktech.admin.security.Encryption;
import com.asktech.admin.service.payout.PayoutMerchant;
import com.asktech.admin.util.EncryptSignature;
import com.asktech.admin.util.SecurityUtils;
import com.asktech.admin.util.UserDetailsUtils;
import com.asktech.admin.util.Utility;
import com.asktech.admin.util.ValidationUtils;
import com.asktech.admin.util.Validator;
import com.asktech.admin.util.payout.PayoutWalletUtilityServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentMerchantService implements ErrorValues {

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
	MerchantRequest4CustomerRepository merchantRequest4CustomerRepository;
	@Autowired
	MailIntegration sendMail;
	@Autowired
	MerchantBeneficiaryDetailsRepo merchantBeneficiaryDetailsRepo;
	@Autowired
	PayoutWalletUtilityServices payoutWalletUtilityServices;
	@Autowired
	PayoutMerchant payoutMerchant;

	ObjectMapper mapper = new ObjectMapper();

	static Logger logger = LoggerFactory.getLogger(PaymentMerchantService.class);
	@Value("${apiEndPoint}")
	String apiEndPoint;
	@Value("${apiCustomerNotifyUrl}")
	String apiCustomerNotifyUrl;
	@Value("${apiCustomerExclude}")
	String apiCustomerExclude;
	@Value("${resendEmailCounter}")
	int resendEmailCounter;

	public MerchantResponse merchantView(String uuid) throws ValidationExceptions {
		logger.info("merchantView In this Method.");
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantResponse merchantResponse = new MerchantResponse();

		merchantResponse.setMerchantAppId(Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getAppID()));
		merchantResponse.setMerchantEmail(merchantDetails.getMerchantEmail());
		merchantResponse.setMerchantKyc(merchantDetails.getKycStatus());
		merchantResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantResponse.setMerchantPhone(merchantDetails.getPhoneNumber());
		merchantResponse.setMerchantSecret(Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()));

		return merchantResponse;
	}
	
	public List<TransactionDetailsDto> getTransactionDetails(String uuid,int pageNo,int pageRecords) throws ValidationExceptions {
		
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		logger.info("Merchantid" + merchantDetails);
		
		 Pageable paging = PageRequest.of(pageNo, pageRecords);
		 Page<TransactionDetails> pageTuts;
		 
		 pageTuts = transactionDetailsRepository.findByMerchantIdContaining(merchantDetails.getMerchantID(), paging);
		 List<TransactionDetails> listTransactionDetails = pageTuts.getContent();
		 
		return populateTransactionDetails(listTransactionDetails);
	}

	public List<TransactionDetailsDto> getTransactionDetails(String uuid) throws ValidationExceptions {

		logger.info("merchantView In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		logger.info("Merchantid" + merchantDetails);
		List<TransactionDetails> listTransactionDetails = transactionDetailsRepository
				.findAllTopByMerchantId(merchantDetails.getMerchantID());
		/*
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : listTransactionDetails) {
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
				trd.setVpaUPI(SecurityUtils.decryptSaveData(tr.getVpaUPI()).replace("\u0000", ""));
			}
			if (tr.getPaymentCode() != null) {
				trd.setWalletOrBankCode(SecurityUtils.decryptSaveData(tr.getPaymentCode()).replace("\u0000", ""));
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveData(tr.getCardNumber()))
						.replace("\u0000", ""));
			}
			trdetails.add(trd);
		}
		*/
		return populateTransactionDetails(listTransactionDetails);
	}
	
	public List<TransactionDetailsDto> populateTransactionDetails(List<TransactionDetails> listTransactionDetails){
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : listTransactionDetails) {
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
				trd.setVpaUPI(SecurityUtils.decryptSaveDataKMS(tr.getVpaUPI()).replace("\u0000", ""));
			}
			if (tr.getPaymentCode() != null) {
				//trd.setWalletOrBankCode(SecurityUtils.decryptSaveDataKMS(tr.getPaymentCode()).replace("\u0000", ""));
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(tr.getCardNumber()))
						.replace("\u0000", ""));
			}
			trdetails.add(trd);
		}
		
		return trdetails;
	}

	public List<MerchantSettlement> getSettleDetails(String uuid) throws ValidationExceptions {
		logger.info("getUnSettleDetails In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<ISettlementBalanceReport> merchantBalanceSheet = merchantBalanceSheetRepository
				.getSettlementBalanceSheet("SETTLED", merchantDetails.getMerchantID());
		List<MerchantSettlement> merchantBalSheet = new ArrayList<MerchantSettlement>();
		for (ISettlementBalanceReport mer : merchantBalanceSheet) {
			MerchantSettlement m = new MerchantSettlement();
			m.setAmount(mer.getAmount());
			m.setCreated(mer.getCreated());
			m.setMerchant_id(mer.getMerchant_id());
			m.setMerchant_order_id(mer.getMerchant_order_id());
			m.setSettlement_status(mer.getSettlement_status());
			m.setTr_type(mer.getTr_type());
			m.setSettledAmount(mer.getSettle_amount_to_merchant());
			if (mer.getVpaupi() != null) {
				m.setVpaupi(SecurityUtils.decryptSaveDataKMS(mer.getVpaupi()).replace("\u0000", ""));
			}
			if (mer.getPayment_code() != null) {
				m.setWalletOrBankCode(SecurityUtils.decryptSaveDataKMS(mer.getPayment_code()).replace("\u0000", ""));
			}
			if (mer.getCard_number() != null) {
				m.setCard_number(Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(mer.getCard_number()))
						.replace("\u0000", ""));
			}

			merchantBalSheet.add(m);

		}
		return merchantBalSheet;
	}

	public List<TransactionDetailsDto> getLast3DaysTransaction(String uuid) throws ValidationExceptions {
		logger.info("getLast3DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<TransactionDetails> transactionDetails = transactionDetailsRepository
				.findLast3DaysTransaction(merchantDetails.getMerchantID());
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : transactionDetails) {
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
				trd.setVpaUPI(SecurityUtils.decryptSaveDataKMS(tr.getVpaUPI()).replace("\u0000", ""));
			}
			if (tr.getPaymentCode() != null) {
				//trd.setWalletOrBankCode(SecurityUtils.decryptSaveDataKMS(tr.getPaymentCode()).replace("\u0000", ""));
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(tr.getCardNumber()))
						.replace("\u0000", ""));
			}
			trdetails.add(trd);
		}
		return trdetails;
	}

	public List<MerchantBalanceSheet> getSettleDetailsLat7Days(String uuid) throws ValidationExceptions {
		logger.info("getLast3DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<MerchantBalanceSheet> merchantBalanceSheet = merchantBalanceSheetRepository
				.findLast7DaysSettleTransaction(merchantDetails.getMerchantID());

		return merchantBalanceSheet;
	}

	public List<MerchantSettlement> getUnSettleDetails(String uuid) throws ValidationExceptions {
		logger.info("getUnSettleDetails In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<ISettlementBalanceReport> merchantBalanceSheet = merchantBalanceSheetRepository
				.getSettlementBalanceSheet("PENDING", merchantDetails.getMerchantID());
		List<MerchantSettlement> merchantBalSheet = new ArrayList<MerchantSettlement>();
		for (ISettlementBalanceReport mer : merchantBalanceSheet) {
			MerchantSettlement m = new MerchantSettlement();
			m.setAmount(mer.getAmount());
			m.setCreated(mer.getCreated());
			m.setMerchant_id(mer.getMerchant_id());
			m.setMerchant_order_id(mer.getMerchant_order_id());
			m.setSettlement_status(mer.getSettlement_status());
			m.setTr_type(mer.getTr_type());

			if (mer.getVpaupi() != null) {
				m.setVpaupi(SecurityUtils.decryptSaveDataKMS(mer.getVpaupi()).replace("\u0000", ""));
			}
			if (mer.getPayment_code() != null) {
				m.setWalletOrBankCode(SecurityUtils.decryptSaveDataKMS(mer.getPayment_code()).replace("\u0000", ""));
			}
			if (mer.getCard_number() != null) {
				m.setCard_number(Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(mer.getCard_number()))
						.replace("\u0000", ""));
			}

			merchantBalSheet.add(m);

		}
		return merchantBalSheet;
	}

	public DashBoardDetails getDashBoardBalance(String uuid) throws ValidationExceptions {
		logger.info("getDashBoardBalance In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		DashBoardDetails dashBoardDetails = new DashBoardDetails();

		String amt = "0";
		String unsettledamt = "0";
		String settled = "0";
		/*
		List<IMerchantTransaction> tda = transactionDetailsRepository
				.getTodayTrDetails(merchantDetails.getMerchantID());
		float a = 0;
		if (tda.isEmpty()) {
			a = 0;
		} else {
			a = (float) tda.get(0).getAmount() / 100;
		}*/
		logger.info("Merchant ID::"+merchantDetails.getMerchantID());
		String a = transactionDetailsRepository.getTodayTr(merchantDetails.getMerchantID());
		
		amt = a;
		if((amt == null)) {
			amt = "0";
		}
		logger.info("Todays Amount for dashboard :: " + amt);
		dashBoardDetails.setTodaysTransactions(amt);

		unsettledamt = merchantBalanceSheetRepository.getPendingSettlementTotal(merchantDetails.getMerchantID());

		logger.info("Unsettled Amount for dashboard :: " + unsettledamt);

		dashBoardDetails.setUnsettledAmount(unsettledamt);

		settled = merchantBalanceSheetRepository.getSettledTotal(merchantDetails.getMerchantID());
		logger.info("Settled Amount for dashboard :: " + settled);
		if (settled == null) {
			dashBoardDetails.setLastSettlements("0");
		} else {
			dashBoardDetails.setLastSettlements(settled);
		}
		return dashBoardDetails;
	}

	public MerchantBankDetails createBankDetails(MerchantBankDetails merchantBankDetails, String uuid)
			throws ValidationExceptions {

		logger.info("getDashBoardBalance In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
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

	public MerchantBankDetails getBankDetails(String uuid) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails == null) {
			throw new ValidationExceptions(BANK_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_BANK_DETILS_NOT_FOUND);
		}
		return merchantBankDetails;

	}

	public MerchantBankDetails updateBankDetails(String uuid, MerchantBankDetails merchantBankDetails)
			throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails2 = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());

		merchantBankDetails2.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails2.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails2.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails2.setCity(merchantBankDetails.getCity());
		merchantBankDetails2.setMicrCode(merchantBankDetails.getMicrCode());

		merchantBankDetailsRepository.save(merchantBankDetails2);

		return merchantBankDetails2;
	}

	public List<IMerchantSettlement> getMerchantLastDaySettlement(String merchantID) {

		return merchantBalanceSheetRepository.getLastDaySettlement(merchantID);
	}

	public Object getMerchantCurrDaySettlement(String merchantID) {
		return merchantBalanceSheetRepository.getCurrDaySettlement(merchantID);
	}

	public Object getMerchantLast7DaySettlement(String merchantID) {
		return merchantBalanceSheetRepository.getLast7DaySettlement(merchantID);
	}

	public Object getMerchantCurrMonthSettlement(String merchantID) {
		return merchantBalanceSheetRepository.getCurrMonthSettlement(merchantID);
	}

	public Object getMerchantLastMonthSettlement(String merchantID) {
		return merchantBalanceSheetRepository.getLastMonthSettlement(merchantID);
	}

	public Object getMerchantLast90DaySettlement(String merchantID) {
		return merchantBalanceSheetRepository.getLast90DaySettlement(merchantID);
	}

	public List<IMerchantDetailsReport> getMerchantDetailsReport(String merchantId) {

		List<IMerchantDetailsReport> getMerchantDetailsReport = merchantPGServicesRepository
				.getMerchantDetailsReport(merchantId, UserStatus.ACTIVE.toString());

		return getMerchantDetailsReport;
	}

	public List<IMerchantTransaction> merchantStatusTransactionLastDay(MerchantDetails user) {

		return transactionDetailsRepository.getYesterdayTrDetails(user.getMerchantID());
	}

	public List<IMerchantTransaction> merchantStatusTransactionToday(MerchantDetails user) {
		return transactionDetailsRepository.getTodayTrDetails(user.getMerchantID());
	}

	public List<IMerchantTransaction> merchantStatusTransactionCurrMonth(MerchantDetails user) {
		return transactionDetailsRepository.getCurrMonthTrDetails(user.getMerchantID());
	}

	public List<IMerchantTransaction> merchantStatusTransactionLastMonth(MerchantDetails user) {
		return transactionDetailsRepository.getLastMonthTrDetails(user.getMerchantID());
	}

	

	public MerchantRequest4Customer merchantCreateApiForCustomer(String uuid, MerchantDetails merchantDetails,
			String custName, String custPhone, String custEmail, String custAmount, int linkExpiry, String orderNote,String source)
			throws ValidationExceptions, ParseException, InvalidKeyException, NoSuchAlgorithmException,
			UnsupportedEncodingException {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		ZonedDateTime expirationTime = ZonedDateTime.now().plus(linkExpiry, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		logger.info("Expiry Time:" + date.toString());
		String orderId = String.valueOf(Utility.getEpochTIme());
		
		

		if (!Validator.isValidCardUserName(custName)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidPhoneNumber(custPhone)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidEmail(custEmail)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		if (!Validator.validDouble(custAmount)) {
			throw new ValidationExceptions(AMOUNT_VALIDATION_ERROR,
					FormValidationExceptionEnums.AMOUNT_VALIDATION_ERROR);
		}
		
		MerchantRequest4Customer merchantRequest4Customer = 
				merchantRequest4CustomerRepository.findByOrderIdAndMerchantId(orderId,merchantDetails.getMerchantID());
		if(merchantRequest4Customer !=null) {
			throw new ValidationExceptions(ORDER_ID_EXITS_WITH_LINK,FormValidationExceptionEnums.ORDER_ID_EXITS_WITH_LINK);
		}
		
		merchantRequest4Customer = new MerchantRequest4Customer();
		merchantRequest4Customer.setAmount(custAmount);
		merchantRequest4Customer.setCreatedBy(uuid);
		merchantRequest4Customer.setCustEmail(custEmail);
		merchantRequest4Customer.setCustName(custName);
		merchantRequest4Customer.setCustPhone(custPhone);
		merchantRequest4Customer.setLinkCustomer(apiEndPoint + "customerRequest/" + Utility.randomStringGenerator(10));
		merchantRequest4Customer.setLinkExpiry(linkExpiry);
		merchantRequest4Customer.setLinkExpiryTime(date);
		merchantRequest4Customer.setStatus(UserStatus.PENDING.toString());
		merchantRequest4Customer.setMerchantId(merchantDetails.getMerchantID());
		merchantRequest4Customer.setOrderCurrency("INR");
		merchantRequest4Customer.setOrderId(orderId);
		merchantRequest4Customer.setOrderNote(orderNote);
		merchantRequest4Customer.setReturnUrl(apiCustomerNotifyUrl);
		merchantRequest4Customer.setAppId(Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getAppID()));
		
		parameters.put("customerEmail", custEmail);
		parameters.put("customerName", custName);
		parameters.put("customerPhone", custPhone);
		parameters.put("customerid", merchantRequest4Customer.getAppId());
		parameters.put("notifyUrl", apiCustomerNotifyUrl);
		parameters.put("orderAmount", custAmount);
		parameters.put("orderCurrency", "INR");
		parameters.put("orderid", orderId);
		parameters.put("orderNote", orderNote);

		String data = EncryptSignature
				.encryptSignature(Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()), parameters);
		merchantRequest4Customer.setSignature(data.trim());

		String[] arrOfStr = apiCustomerExclude.split(",");
		List<String> integerList= Arrays.asList(arrOfStr);
		if(!integerList.contains(source)){
			merchantRequest4Customer.setSource(source);
			merchantRequest4Customer.setEmailCounter(1);
			logger.info("Inside mail block");
			sendMail.sendGeneratedMailToCustomer(custName, custEmail, custPhone,
					merchantRequest4Customer.getLinkCustomer());
		}else {
			merchantRequest4Customer.setSource("");
		}
		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		return merchantRequest4Customer;

	}

	public MerchantRequest4Customer merchantCreateApiForCustomer(String uuid, MerchantDetails merchantDetails,
			String custName, String custPhone, String custEmail, String custAmount, int linkExpiry, String orderNote,
			String returnUrl, String orderId, String source) throws ValidationExceptions, ParseException, InvalidKeyException,
			NoSuchAlgorithmException, UnsupportedEncodingException {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		ZonedDateTime expirationTime = ZonedDateTime.now().plus(linkExpiry, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		if (orderId.strip().length() < 5) {
			orderId = String.valueOf(Utility.getEpochTIme());
		}

		if (!Validator.isValidCardUserName(custName)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidPhoneNumber(custPhone)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidEmail(custEmail)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		if (!Validator.validDouble(custAmount)) {
			throw new ValidationExceptions(AMOUNT_VALIDATION_ERROR,
					FormValidationExceptionEnums.AMOUNT_VALIDATION_ERROR);
		}
		
		MerchantRequest4Customer merchantRequest4Customer = 
				merchantRequest4CustomerRepository.findByOrderIdAndMerchantId(orderId,merchantDetails.getMerchantID());
		if(merchantRequest4Customer !=null) {
			throw new ValidationExceptions(ORDER_ID_EXITS_WITH_LINK,FormValidationExceptionEnums.ORDER_ID_EXITS_WITH_LINK);
		}
		
		merchantRequest4Customer = new MerchantRequest4Customer();
		merchantRequest4Customer.setAmount(custAmount);
		merchantRequest4Customer.setCreatedBy(uuid);
		merchantRequest4Customer.setCustEmail(custEmail);
		merchantRequest4Customer.setCustName(custName);
		merchantRequest4Customer.setCustPhone(custPhone);
		merchantRequest4Customer.setLinkCustomer(apiEndPoint + "customerRequest/" + Utility.randomStringGenerator(10));
		merchantRequest4Customer.setLinkExpiry(linkExpiry);
		merchantRequest4Customer.setLinkExpiryTime(date);
		merchantRequest4Customer.setStatus(UserStatus.PENDING.toString());
		merchantRequest4Customer.setMerchantId(merchantDetails.getMerchantID());
		merchantRequest4Customer.setOrderCurrency("INR");
		merchantRequest4Customer.setOrderId(orderId);
		merchantRequest4Customer.setOrderNote(orderNote);
		merchantRequest4Customer.setReturnUrl(returnUrl);
		merchantRequest4Customer.setAppId(Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getAppID()));
		merchantRequest4Customer.setSource(source);
		

		parameters.put("customerEmail", custEmail);
		parameters.put("customerName", custName);
		parameters.put("customerPhone", custPhone);
		parameters.put("customerid", merchantRequest4Customer.getAppId());
		parameters.put("notifyUrl", returnUrl);
		parameters.put("orderAmount", custAmount);
		parameters.put("orderCurrency", "INR");
		parameters.put("orderid", orderId);
		parameters.put("orderNote", orderNote);

		String data = EncryptSignature
				.encryptSignature(Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()), parameters);
		merchantRequest4Customer.setSignature(data.trim());

		String[] arrOfStr = apiCustomerExclude.split(",");
		List<String> integerList= Arrays.asList(arrOfStr);
		if(!integerList.contains(source)){
			merchantRequest4Customer.setEmailCounter(1);
			sendMail.sendGeneratedMailToCustomer(custName, custEmail, custPhone,
					merchantRequest4Customer.getLinkCustomer());
		}else {
			merchantRequest4Customer.setSource("");
		}

		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		return merchantRequest4Customer;

	}
	
	public String merchantReEmailSend(String orderId,MerchantDetails merchantDetails) throws ValidationExceptions {
		
		MerchantRequest4Customer merchantRequest4Customer = 
				merchantRequest4CustomerRepository.findByOrderIdAndMerchantId(orderId,merchantDetails.getMerchantID());
		if(merchantRequest4Customer == null) {
			throw new ValidationExceptions(ORDER_ID_NOT_FOUND,FormValidationExceptionEnums.ORDER_ID_NOT_FOUND);
		}
		
		
		if(merchantRequest4Customer.getEmailCounter()==0 ) {
			throw new ValidationExceptions(RESEND_EMAIL_NOT_POSSIBLE,FormValidationExceptionEnums.RESEND_EMAIL_NOT_POSSIBLE) ;
		}
		
		if(merchantRequest4Customer.getEmailCounter() >= resendEmailCounter) {
			throw new ValidationExceptions(EXITS_RESEND_EMAIL_LINK_COUNTER,FormValidationExceptionEnums.EXITS_RESEND_EMAIL_LINK_COUNTER);
		}
		
		sendMail.sendGeneratedMailToCustomer(merchantRequest4Customer.getCustName(), 
											merchantRequest4Customer.getCustEmail(), 
											merchantRequest4Customer.getCustPhone(),
											merchantRequest4Customer.getLinkCustomer());
		

		merchantRequest4Customer.setEmailCounter(merchantRequest4Customer.getEmailCounter()+1);
		 merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		 return "Email resend works successfully for order id :: "+orderId;
	}

	public List<ICustomerAPIReport> getCustomerApiRequestReport(MerchantDetails user) throws ValidationExceptions {		
		
		String source = "";
		return merchantRequest4CustomerRepository.getCustomerRequestReport(user.getMerchantID(),source);
	}

	public Object addBeneficiaryBankAccount(MerchantDetails user, CreateBeneficiaryRequest createBeneficiaryRequest) throws ValidationExceptions, JsonProcessingException, ParseException {
		
		MerchantBeneficiaryDetails merchantBeneficiaryDetailsDup =
				merchantBeneficiaryDetailsRepo.findByMerchantOrderId(createBeneficiaryRequest.getMerchantOrderId());
		if(merchantBeneficiaryDetailsDup !=null) {
			throw new ValidationExceptions(DUPLICATE_ORDER_ID_E0208, FormValidationExceptionEnums.E0208);
		}
		
		IMerchantWalletDetails iMerchantWalletDetails = merchantBeneficiaryDetailsRepo.getMerchatWalletDetails(user.getMerchantID());
		if(iMerchantWalletDetails == null) {
			throw new ValidationExceptions(MERCHANT_DISBRUSHMENT_ACCOUNT_NOT_FOUND_E0200, FormValidationExceptionEnums.E0200);
		}
		logger.info("Merchant Wallet Disbrushment Account :: "+iMerchantWalletDetails.getWalletGuuid());
		
		if(ValidationUtils.validateBeneficiaryAddRequest(createBeneficiaryRequest)) {
			MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo.
					findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndStatus(
							createBeneficiaryRequest.getBeneficiaryAccountNo(),createBeneficiaryRequest.getBeneficiaryIFSCCode(),"ACTIVE");
			
			if(merchantBeneficiaryDetails != null) {
				if(merchantBeneficiaryDetails.getMerchantId().equalsIgnoreCase(user.getMerchantID())) {		
					if(merchantBeneficiaryDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
						throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_EXITS_E0204, FormValidationExceptionEnums.E0204);
					}
				}
				
			}
			return merchantBeneficiaryDetailsRepo.save(
			payoutWalletUtilityServices.populateMerchnatBeneficiary(user, createBeneficiaryRequest, iMerchantWalletDetails.getWalletGuuid()));
		}
		
		return null;
	}

	public MerchantBeneficiaryDetails deleteBeneficiaryBankAccount(MerchantDetails user,DeleteBeneficiaryRequest deleteBeneficiaryRequest) throws ValidationExceptions, JsonProcessingException {
		
		MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo.
				findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndMerchantId(deleteBeneficiaryRequest.getBeneficiaryAccountNo(),
						deleteBeneficiaryRequest.getBeneficiaryIFSCCode(), user.getMerchantID());
		
		if(merchantBeneficiaryDetails==null) {
			logger.error("There is no records found as per Delete request .");
			throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206, FormValidationExceptionEnums.E0206);					
		}
		if(merchantBeneficiaryDetails.getStatus().equalsIgnoreCase(UserStatus.DELETE.toString())) {
			logger.error("The beneficiary account already in deleted state.");
			throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_DELETED_E0207, FormValidationExceptionEnums.E0207);
		}
		
		merchantBeneficiaryDetails.setStatus(UserStatus.DELETE.toString());
		merchantBeneficiaryDetails.setModifiedBy(user.getUuid());
		merchantBeneficiaryDetails.setUpdateRequestData(Utility.convertDTO2JsonString(deleteBeneficiaryRequest));		
		
		return merchantBeneficiaryDetailsRepo.save(merchantBeneficiaryDetails);
	}

	public Object associateBeneficiaryBankAccount(MerchantDetails user, AssociateBankDetails associateBankDetails) throws ValidationExceptions, JsonProcessingException, ParseException {
		
		IMerchantWalletDetails iMerchantWalletDetails = merchantBeneficiaryDetailsRepo.getMerchatWalletDetails(user.getMerchantID());
		if(iMerchantWalletDetails == null) {
			throw new ValidationExceptions(MERCHANT_DISBRUSHMENT_ACCOUNT_NOT_FOUND_E0200, FormValidationExceptionEnums.E0200);
		}
		logger.info("Merchant Wallet Disbrushment Account :: "+iMerchantWalletDetails.getWalletGuuid());
		
		if(ValidationUtils.validateBeneficiaryAssocRequest(associateBankDetails)) {
			MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo.
					findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndStatus(
							associateBankDetails.getBeneficiaryAccountNo(),associateBankDetails.getBeneficiaryIFSCCode(),"ACTIVE");
			if(merchantBeneficiaryDetails == null) {
				throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206, FormValidationExceptionEnums.E0206);
			}
			if(merchantBeneficiaryDetails.getMerchantId().equalsIgnoreCase(user.getMerchantID())) {		
				if(merchantBeneficiaryDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
					throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_EXITS_E0204, FormValidationExceptionEnums.E0204);
				}
			}
			
			return merchantBeneficiaryDetailsRepo.save(
					payoutWalletUtilityServices.associateMerchnatBeneficiary(user, associateBankDetails, merchantBeneficiaryDetails, iMerchantWalletDetails.getWalletGuuid()));
		}
		
		return null;
	}

	public Object verifyBankAccount(MerchantDetails user, VerifyBankAccount verifyBankAccount) throws ValidationExceptions, ParseException {
		
		ObjectMapper mapper = new ObjectMapper();
		MerchantBeneficiaryDetails merchantBeneficiaryDetails = 
				merchantBeneficiaryDetailsRepo.findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndMerchantIdAndStatus(
						verifyBankAccount.getBeneficiaryAccountNo(), verifyBankAccount.getBeneficiaryIFSCCode(),user.getMerchantID(),UserStatus.ACTIVE.toString());
		
		if(merchantBeneficiaryDetails == null) {
			throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206, FormValidationExceptionEnums.E0206);
		}
		
		if(!merchantBeneficiaryDetails.getAccountValidationFlag().equalsIgnoreCase("F")) {
			throw new ValidationExceptions(BENEFICIARY_ALREADY_VERIFIED_E0209, FormValidationExceptionEnums.E0209);
		}
		
		verifyBankAccount.setMerchantId(user.getMerchantID());
		if(Objects.isNull(verifyBankAccount.getOrderId())) {
			verifyBankAccount.setOrderId(String.valueOf(Utility.getEpochTIme()));
		}
		
		String strVeriftAccount =  payoutMerchant.verifyAccount(verifyBankAccount);
		logger.info("strVeriftAccount :: "+strVeriftAccount);
		try {
			VerifyBankAccountResponse verifyBankAccountResponse = mapper.readValue(strVeriftAccount,VerifyBankAccountResponse.class);
			if(verifyBankAccountResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
				merchantBeneficiaryDetails.setAccountValidationFlag("T");
			}else if(verifyBankAccountResponse.getStatus().equalsIgnoreCase("ACCEPTED") || 
					verifyBankAccountResponse.getStatus().equalsIgnoreCase("PENDING")) {
				merchantBeneficiaryDetails.setAccountValidationFlag("P");
			}else {
				merchantBeneficiaryDetails.setAccountValidationFlag("F");
			}
			merchantBeneficiaryDetailsRepo.save(merchantBeneficiaryDetails);
			
		}catch(Exception e) {
			return strVeriftAccount;
		}
		return strVeriftAccount ;	
	}
	
	public MerchantDetails getMerchantFromAppId(String appId, String secret)
			throws JsonProcessingException, ValidationExceptions {

		MerchantDetails merhantDetails = merchantDetailsRepository.findByAppIDAndSecretId(
				appId, Encryption.encryptCardNumberOrExpOrCvvKMS(secret));
		logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merhantDetails));

		if (merhantDetails == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND + appId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		return merhantDetails;
	}

	
	public MerchanSerectAppId getAppIdAndSecretByMerchantDetails(String merchantId)
	throws JsonProcessingException, ValidationExceptions {

MerchantDetails merhantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
if (merhantDetails == null) {
	throw new ValidationExceptions(MERCHANT_NOT_FOUND + merchantId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
}
String secret = Encryption.decryptForFrontEndDataKMS(merhantDetails.getSecretId());
MerchanSerectAppId mer=new MerchanSerectAppId();
mer.setSecret_id(secret);
mer.setAppid(merhantDetails.getAppID());
mer.setKyc_status(merhantDetails.getKycStatus());
mer.setMerchantEMail(merhantDetails.getMerchantEmail());
mer.setMerchantId(merhantDetails.getMerchantID());
mer.setMerchant_name(merhantDetails.getMerchantName());
mer.setPhone_number(merhantDetails.getPhoneNumber());
mer.setSalt_key(merhantDetails.getSaltKey());
mer.setUser_status(merhantDetails.getUserStatus());
mer.setUuid(merhantDetails.getUuid());

logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merhantDetails));



return mer;
}

	public List<UserDetailsReport> getCustomerDetailsReport(MerchantDetails user, String mobileNo, String emailId) throws ValidationExceptions {
		
		if(mobileNo == null && emailId == null ) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED , FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		if(mobileNo.length()==0 && emailId.length()==0 ) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED , FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		
		List<IUserDetails> listIUserDetails = userDetailsRepository.getDistinctUserDetails(emailId , mobileNo);
		List<UserDetailsReport> listIUserDetailsUps = new ArrayList<>();
		
		for(IUserDetails iuserDetails : listIUserDetails) {			
			listIUserDetailsUps.add( UserDetailsUtils.updateUserDetails(iuserDetails));
		}
		
		return listIUserDetailsUps;
	}

	public List<UserDetailsReport> getCustomerDetailsAll(MerchantDetails user) {
		
		List<IUserDetails> listIUserDetails = userDetailsRepository.getDistinctUserDetailsAll();
		List<UserDetailsReport> listIUserDetailsUps = new ArrayList<>();
		
		for(IUserDetails iuserDetails : listIUserDetails) {			
			listIUserDetailsUps.add( UserDetailsUtils.updateUserDetails(iuserDetails));
		}
		
		return listIUserDetailsUps;
	}

	public Object getMerchantSettleMentReportFilterWise(MerchantDetails user, 
			String orderId, String status,int pageNo,int pageRecords) throws ValidationExceptions {
		
		if(orderId== null && status==null) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED , FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		
		Pageable paging = PageRequest.of(pageNo, pageRecords);
		Page<MerchantBalanceSheet> pageTuts;
		
		if(status != null && (orderId ==null || orderId=="")) {
			logger.info("Inside Status Block");
			pageTuts = merchantBalanceSheetRepository.findByMerchantIdAndSettlementStatusContaining(user.getMerchantID(), status, paging);
			List<MerchantBalanceSheet> listMerchantBalanceSheet = pageTuts.getContent();
			return listMerchantBalanceSheet;
		}
		if(orderId !=null && (status ==null || status=="")) {
			return merchantBalanceSheetRepository.findByMerchantIdAndMerchantOrderId(user.getMerchantID(), orderId);
		}
		if(orderId !=null && status !=null) {
			return merchantBalanceSheetRepository.findByMerchantIdAndMerchantOrderIdAndSettlementStatus(user.getMerchantID(), orderId, status);
		}
		return null;
	}

	public Object getMerchantTransactionFilterWise(MerchantDetails user, 
			String orderId, String status,int pageNo,int pageRecords) throws ValidationExceptions {
		
		if(orderId== null && status==null) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED , FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		
		Pageable paging = PageRequest.of(pageNo, pageRecords);
		Page<TransactionDetails> pageTuts;
		
		if(status != null && (orderId ==null || orderId=="")) {
			logger.info("Inside Status Block" +status +" merchant Id :: "+user.getMerchantID());
			pageTuts = transactionDetailsRepository.findByMerchantIdAndStatusContaining(user.getMerchantID(), status, paging);
			List<TransactionDetails> listTransactionDetails = pageTuts.getContent();
			//List<TransactionDetails> listTransactionDetails = transactionDetailsRepository.findByMerchantIdAndStatus(user.getMerchantID(), status);
			logger.info("Length :: "+listTransactionDetails.size());
			return listTransactionDetails;
		}
		if(orderId !=null && (status ==null || status=="")) {
			return transactionDetailsRepository.findAllByMerchantOrderIdAndMerchantId(orderId, user.getMerchantID());
		}
		if(orderId !=null && status !=null) {
			return transactionDetailsRepository.findAllByMerchantOrderIdAndMerchantIdAndStatus(orderId, user.getMerchantID(), status);
		}
		return null;
	}

	public Object curentMonthSettleMentMerchantWise(MerchantDetails user, String dateFrom, String dateTo) throws ParseException {
		
		return merchantBalanceSheetRepository.getMerchantWiseSettlementDateWise(user.getMerchantID(),
				Utility.convertDatetoMySqlDateFormat(dateFrom), 
				Utility.convertDatetoMySqlDateFormat(dateTo));
	}
}
