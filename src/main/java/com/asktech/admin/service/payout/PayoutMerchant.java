package com.asktech.admin.service.payout;


/*
 * Commented By Anamika
 */
/*
 * Payout service 
 * 1. Updating Whitelist IP Address
 * 2. Update User Status
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.merchant.MainWalletDto;
import com.asktech.admin.dto.merchant.MainWalletRechargeReqDto;
import com.asktech.admin.dto.merchant.MainWalletReversalReqDto;
import com.asktech.admin.dto.merchant.PayoutAndWalletDto;
import com.asktech.admin.dto.merchant.RechargeServiceDto;
import com.asktech.admin.dto.merchant.TransactionServiceDto;
import com.asktech.admin.dto.merchant.WalletReversalReqDto;
import com.asktech.admin.dto.merchant.WalletUpdateReqDto;
import com.asktech.admin.dto.payout.beneficiary.VerifyBankAccount;
import com.asktech.admin.dto.payout.merchant.AccountTransferMerReq;
import com.asktech.admin.dto.payout.merchant.PayUserDetails;
import com.asktech.admin.dto.payout.merchant.TransactionFilterReq;
import com.asktech.admin.dto.payout.merchant.TransactionReportMerReq;
import com.asktech.admin.dto.payout.merchant.TransactionRequestFilterMerReq;
import com.asktech.admin.dto.payout.merchant.TransferStatusReq;
import com.asktech.admin.dto.payout.merchant.WalletCreateReqDto;
import com.asktech.admin.dto.payout.merchant.WalletFilterReq;
import com.asktech.admin.dto.payout.merchant.WalletRechargeReqDto;
import com.asktech.admin.dto.payout.merchant.WalletTransferMerReq;
import com.asktech.admin.dto.merchant.TrxnWithParameter;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.PayoutUserStatus;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.payout.PayoutApiUserDetails;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.payout.PayoutApiUserDetailsRepo;
import com.asktech.admin.service.PGGatewayAdminService;
import com.asktech.admin.util.Validator;
import  com.asktech.admin.customInterface.payout.MerchantWisePgWiseSumPayoutDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.GenericType;
import kong.unirest.Unirest;

@Service
public class PayoutMerchant implements ErrorValues {

	// Logger Implementation for Logging PayoutMerchant class.
	static Logger logger = LoggerFactory.getLogger(PayoutMerchant.class);
	@Value("${apiPayoutEndPoint.payoutUrl}")
	String payoutUrl;
	@Value("${apiPayoutEndPoint.payoutBaseUrl}")
	String payoutBaseUrl;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	PayoutApiUserDetailsRepo payoutApiUserDetailsRepo;
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;

	/*
	 * Method for User Ip Address update
	 * 
	 * @input sample: PayUserDetails :
	 * {"merchantId":"23764523764",
	 * 
	 * }
	 */
	public PayoutApiUserDetails updateUserIpAddress(PayUserDetails dto) throws ValidationExceptions {
		// @Query: find merchant details by merchant id
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(dto.getMerchantId());
		// check if merchant details are not found
		if (merchantDetails == null) {
			// if merchant details are not found throw merchant not found exception.
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		logger.info(dto.getWhitelistedip());

		if (dto.getWhitelistedip().contains(",")) {

			List<String> list = Arrays.asList(dto.getWhitelistedip().split(","));
			for (String l : list) {
				if (!Validator.isValidIP(l)) {
					throw new ValidationExceptions(INVALID_IP_ADDRESS, FormValidationExceptionEnums.INVALID_IP_ADDRESS);
				}
			}
		} else {
			if (!Validator.isValidIP(dto.getWhitelistedip())) {
				throw new ValidationExceptions(INVALID_IP_ADDRESS, FormValidationExceptionEnums.INVALID_IP_ADDRESS);
			}
		}

		PayoutApiUserDetails payUser = payoutApiUserDetailsRepo.findByMerchantId(dto.getMerchantId());
		if (payUser == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (payUser.getMerchantStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		// if(payUser.getWhitelistedip().contains(dto.getWhitelistedip())) {
		// throw new ValidationExceptions(INFORMATION_EXISTS,
		// FormValidationExceptionEnums.INFORMATION_ALREADY_EXISTS_IN_SYSTEM);
		// }
		String withoutSpaceIps = dto.getWhitelistedip().replaceAll("\\s+", "");
		payUser.setWhitelistedip(withoutSpaceIps);
		return payoutApiUserDetailsRepo.save(payUser);
	}

	public PayoutApiUserDetails updateUserStatus(String status, String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails payUser = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (payUser == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (!Validator.containsEnum(PayoutUserStatus.class, status)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		payUser.setMerchantStatus(status);
		return payoutApiUserDetailsRepo.save(payUser);
	}

	public List<String> getIpAddress(String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails payUser = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (payUser == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (payUser.getMerchantStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		String userip = payUser.getWhitelistedip().replaceAll("\\s+", "");
		List<String> list = new ArrayList<String>();
		if (userip.contains(",")) {
			list = Arrays.asList(userip.split(","));
		} else {
			list.add(userip);
		}

		return list;
	}

	public List<PayoutApiUserDetails> getPayOutUser(String merchantid) throws ValidationExceptions {
		List<PayoutApiUserDetails> list = new ArrayList<PayoutApiUserDetails>();

		if (merchantid != null) {
			MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
			if (merchantDetails == null) {

				throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
			}

			PayoutApiUserDetails payUser = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
			if (payUser == null) {
				throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
						FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
			}
			list.add(payUser);
		} else {
			list = payoutApiUserDetailsRepo.findAll();
		}

		return list;
	}

	public List<PayoutAndWalletDto> payoutMerdet(String merchantid) throws ValidationExceptions, ParseException {
		List<PayoutAndWalletDto> list = new ArrayList<PayoutAndWalletDto>();
		PayoutAndWalletDto res = new PayoutAndWalletDto();
		if (pgGatewayAdminService.txnParam(merchantid) == true) {
			res = getPayoutAndWallet(merchantid);
			list.add(res);
		} else {
			List<PayoutApiUserDetails> payuserlist = payoutApiUserDetailsRepo.findAll();
			if (payuserlist.size() > 0) {
				for (PayoutApiUserDetails l : payuserlist) {
					System.out.println("inside findall 1");
					res = getPayoutAndWallet(l.getMerchantId());
					list.add(res);
				}
			}
		}
		return list;
	}

	public List<PayoutApiUserDetails> getAllPayoutUsers(){
		return  payoutApiUserDetailsRepo.findAll();
	} 

	public PayoutAndWalletDto getPayoutAndWallet(String merchantid) throws ValidationExceptions, ParseException {
		PayoutAndWalletDto paywallet = new PayoutAndWalletDto();
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails mer = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (mer == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		System.out.println("\n\n\n\n\n\n\n\n inside findall 3");
		paywallet.setCreated(mer.getCreated().toString());
		paywallet.setUpdated(mer.getUpdated().toString());
		paywallet.setMerchantId(mer.getMerchantId());
		paywallet.setMerchantStatus(mer.getMerchantStatus());
		paywallet.setWalletStatus(mer.getWalletCheckStatus().toString());
		paywallet.setWhitelistedIP(mer.getWhitelistedip());

		String merWallet = "";
		if (mer.getWalletCheckStatus().equalsIgnoreCase("TRUE")) {
			merWallet = walletlistMerchantwise(merchantid);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(merWallet);
			paywallet.setMerchantWallet(json);
		}

		return paywallet;
	}

	public String WalletTransfer(WalletTransferMerReq dto, String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		// dto.setOrderid();
		logger.info("Inside Wallet transfer Service WalletTransfer()");
		String res = Unirest.post(payoutUrl + "walletTransfer/" + merchantid).header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		logger.info("Response from Payout :: " + res);
		return res;
	}

	public String AccountTransfer(AccountTransferMerReq dto, String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		String res = Unirest.post(payoutUrl + "accountTransfer/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}

	public String BalanceCheck(String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		logger.info("Submit URL :: " + payoutUrl + "balanceCheck/" + merchantid);
		String res = Unirest.get(payoutUrl + "balanceCheck/" + merchantid).header("Content-Type", "application/json")
				.asString().getBody();
		return res;

	}

	public String TransactionReport(String merchantid, TransactionReportMerReq dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		String res = Unirest.post(payoutUrl + "transactionReport/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}

	public String WalletReport(String merchantid, TransactionReportMerReq dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		String res = Unirest.post(payoutUrl + "walletReport/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}

	public String TransactionStatus(TransferStatusReq dto, String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		String res = Unirest.post(payoutUrl + "transactionStatus/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String verifyAccount(VerifyBankAccount dto) {
		String res = Unirest.post(payoutUrl + "accountVerify")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String TransactionReportFilter(String merchantid, TransactionRequestFilterMerReq dto) {
		String res = Unirest.post(payoutUrl + "transactionReportWithFilter/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String walletCreation(String merchantid, WalletCreateReqDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		// String secret =
		// Encryption.decryptForFrontEndDataKMS(merchantDetails.getSecretId());

		PayoutApiUserDetails getDet = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (getDet == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (getDet.getMerchantStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		String res = Unirest.post(payoutBaseUrl + "admin/wallet/walletCreation/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		getDet.setWalletCheckStatus("TRUE");
		payoutApiUserDetailsRepo.save(getDet);
		return res;
	}

	public String walletRecharge(String merchantid, WalletRechargeReqDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails getDet = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (getDet == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (getDet.getMerchantStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		String res = Unirest.post(payoutBaseUrl + "admin/wallet/walletRecharge/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String transactionFilter(TransactionFilterReq dto) {
		String res = Unirest.post(payoutUrl + "transactionFilter")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String walletFilter(WalletFilterReq dto) {
		String res = Unirest.post(payoutUrl + "walletFilter")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String AllWallet() {
		String res = Unirest.get(payoutUrl + "AllwalletReport")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String AllTransaction() {
		String res = Unirest.get(payoutUrl + "AlltransactionReport")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String walletlist() {
		String res = Unirest.get(payoutUrl + "walletlist")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String walletlistMerchantwise(String merchantid) {
		String res = Unirest.get(payoutUrl + "walletlist/" + merchantid)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String mainWalletlistAssociation(String mainWalletid) {
		String res = Unirest.get(payoutUrl + "mainWalletAssociation/" + mainWalletid)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String mainWalletCreation(MainWalletDto dto) throws ValidationExceptions {
		String res = Unirest.post(payoutBaseUrl + "admin/wallet/mainWalletCreation")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String updateMainWallet(MainWalletDto dto, String walletid) throws ValidationExceptions {
		String res = Unirest.put(payoutBaseUrl + "admin/wallet/updateMainWalletStatus/" + walletid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String rechargeServiceCommission(RechargeServiceDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(dto.getMerchantId());
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		String res = Unirest.post(payoutBaseUrl + "admin/wallet/rechargeServiceCommission")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String transactionServiceCommission(TransactionServiceDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(dto.getMerchantId());
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		String res = Unirest.post(payoutBaseUrl + "admin/wallet/transactionServiceCommission")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String updateTransactionServiceCommission(TransactionServiceDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(dto.getMerchantId());
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		String res = Unirest.put(payoutBaseUrl + "admin/wallet/updateTransactionServiceCommission")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String updateRechargeServiceCommission(RechargeServiceDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(dto.getMerchantId());
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		String res = Unirest.put(payoutBaseUrl + "admin/wallet/updateRechargeServiceCommission")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String getTransactionServiceCommissionList() throws ValidationExceptions {
		String res = Unirest.get(payoutBaseUrl + "admin/wallet/getTransactionCommissionList")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String getRechargeServiceCommissionList() throws ValidationExceptions {
		String res = Unirest.get(payoutBaseUrl + "admin/wallet/getRechargeCommissionList")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String getTransactionServiceCommissionListMerchantwise(String merchantid) throws ValidationExceptions {
		String res = Unirest.get(payoutBaseUrl + "admin/wallet/getTransactionCommissionListMerchantwise/" + merchantid)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String getRechargeServiceCommissionListMerchantwise(String merchantid) throws ValidationExceptions {
		String res = Unirest.get(payoutBaseUrl + "admin/wallet/getRechargeCommissionListMerchantwise/" + merchantid)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String mainWalletBalanceSer(String walletId) {
		String res = Unirest.get(payoutBaseUrl + "admin/wallet/mainWalletBalance/" + walletId)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String mainWalletRechargeSer(MainWalletRechargeReqDto dto, String walletId) {
		String res = Unirest.post(payoutBaseUrl + "admin/wallet/mainWalletRecharge/" + walletId)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String mainWalletReversalSer(MainWalletReversalReqDto dto, String walletId) {
		String res = Unirest.post(payoutBaseUrl + "admin/wallet/mainWalletReversal/" + walletId)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String walletUpdateSer(String merchantid, WalletUpdateReqDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails getDet = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (getDet == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (getDet.getMerchantStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		String res = Unirest.put(payoutBaseUrl + "admin/wallet/walletUpdateStatusAndHoldAmount")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String walletRefundSer(String merchantid, WalletRechargeReqDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails getDet = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (getDet == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		if (getDet.getMerchantStatus().equalsIgnoreCase("BLOCKED")) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
		}

		String res = Unirest.post(payoutBaseUrl + "admin/wallet/walletRefund/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String walletReversal(String merchantid, WalletReversalReqDto dto) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails getDet = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (getDet == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		String res = Unirest.post(payoutBaseUrl + "admin/wallet/walletReversal/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}

	public String mainWalletList() {
		String res = Unirest.get(payoutBaseUrl + "admin/wallet/mainWalletList")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String merchantWalletBalance(String merchantid) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantid);
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		PayoutApiUserDetails getDet = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (getDet == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
					FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
		}

		String res = Unirest.get(payoutUrl + "balanceCheck/" + merchantid)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public PayUserDetails payoutUser(PayUserDetails dto) {
		return null;
	}

	public List <MerchantWisePgWiseSumPayoutDto> getByMerchantWisePgWiseSumPayOut(String start_date,String end_date,String status) throws ValidationExceptions{
		kong.unirest.HttpResponse<List<MerchantWisePgWiseSumPayoutDto>> responce   = Unirest.get(payoutBaseUrl + "controller/getByMerchantWisePgWiseSum/"+start_date+"/"+end_date+"/"+status)
				.header("Content-Type", "application/json").asObject(new GenericType<List<MerchantWisePgWiseSumPayoutDto>>(){});
				List<MerchantWisePgWiseSumPayoutDto> mlist=new ArrayList<>();
				
for( MerchantWisePgWiseSumPayoutDto m1:responce.getBody()){
	MerchantDetails getDet = merchantDetailsRepository.findByMerchantID(m1.getMerchantId());
				if (getDet == null) {
					throw new ValidationExceptions(MERCHNT_NOT_EXISTIS_FOR_PAYOUT,
							FormValidationExceptionEnums.MERCHANT_INFORMATION_NOT_FOUND);
				}
				MerchantWisePgWiseSumPayoutDto mPDto=new MerchantWisePgWiseSumPayoutDto();
				mPDto.setCnt(m1.getCnt());
				mPDto.setMerchantId(m1.getMerchantId());
				mPDto.setTotalAmt(m1.getTotalAmt());
				mPDto.setTransactionType(m1.getTransactionType());
				mPDto.setMerchantName(getDet.getMerchantName());
				mPDto.setCompanyName(getDet.getCompanyName());

				mlist.add(mPDto);

}

		return mlist;
	}
	public String getPgTypeAndCountByStatusAndDatePayOut(String start_date,String end_date,String status) {
		String res = Unirest.get(payoutBaseUrl + "controller/getPgTypeAndCountByStatusAndDate/"+start_date+"/"+end_date+"/"+status)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

		public String getHourandCountStatusAndDatePayOut(String start_date,String status) {
		String res = Unirest.get(payoutBaseUrl + "controller/getHourandCountStatusAndDate/"+start_date+"/"+status)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}
	public String getMinuteandCountByStatusPayOut(String status) {
		String res = Unirest.get(payoutBaseUrl + "controller/getMinuteandCountByStatus/"+status)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}
	public String getStatusCountPayOut(String start_date,String end_date ) {
		String res = Unirest.get(payoutBaseUrl + "controller/getStatusCount/"+start_date+"/"+end_date)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}
	public String getLastTrxMerchListPayOut(String start_date) {
		String res = Unirest.get(payoutBaseUrl + "controller/getLastTrxMerchList/"+start_date)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}
	public String getHourandStatusWiseCountAndDatePayOut(String start_date) {
		String res = Unirest.get(payoutBaseUrl + "controller/getHourandStatusWiseCountAndDate/"+start_date)
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}
	public String getStatusAndMinuteWiseCountPayOut() {
		String res = Unirest.get(payoutBaseUrl + "controller/getStatusAndMinuteWiseCount")
				.header("Content-Type", "application/json").asString().getBody();
		return res;
	}

	public String txnWithParametersPayOut(TrxnWithParameter dto) {
		
		String res = Unirest.post(payoutBaseUrl + "controller/txnWithParameters/")
		.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}
}
