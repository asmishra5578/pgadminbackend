package com.asktech.admin.service.merchantApi;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.merchant.MerchantRefundResponse;
import com.asktech.admin.dto.merchant.MerchantTransaction;
import com.asktech.admin.dto.merchant.MerchantTransactionResponse;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.UserStatus;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.MerchantBalanceSheet;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.RefundDetails;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.repository.MerchantBalanceSheetRepository;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.RefundDetailsRepository;
import com.asktech.admin.repository.TransactionDetailsRepository;
import com.asktech.admin.security.Encryption;
import com.asktech.admin.security.SecurityExposerFunction;
import com.asktech.admin.service.PaymentMerchantService;
import com.asktech.admin.util.GeneralUtils;
import com.asktech.admin.util.SecurityUtils;
import com.asktech.admin.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.jsonwebtoken.Claims;

@Service
public class ServiceMerchantApiExposer implements ErrorValues {

	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantBalanceSheetRepository merchantBalanceSheetRepository;
	@Autowired
	RefundDetailsRepository refundDetailsRepository;
	@Autowired
	PaymentMerchantService paymentMerchantService;

	static Logger logger = LoggerFactory.getLogger(ServiceMerchantApiExposer.class);

	public MerchantTransaction transactionStatus(String tokenInfo, MultiValueMap<String, String> formData)
			throws ValidationExceptions, NoSuchAlgorithmException, JsonProcessingException {
		logger.info("MerchantAPIService transactionStatus In this Method.");
		logger.info("transactionStatus ::"+GeneralUtils.MultiValueMaptoJson(formData));
		SecurityExposerFunction securityExposerFunction = new SecurityExposerFunction();
		MerchantTransaction merchantTransaction = new MerchantTransaction();

		logger.info("tokenInfo :: " + tokenInfo);
		logger.info("strBody :: " + formData);

		Claims claimBody = securityExposerFunction.decodeJWT(tokenInfo);
		MerchantDetails merchantDetails = merchantDetailsRepository
				.findByAppID(claimBody.get("appId").toString());

		logger.info(Utility.convertDTO2JsonString(claimBody));
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		logger.info("Before Verify the Token with Secrect key and Signature");
		String SECRET = Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()).trim().strip().replace("\u0000", "");
		logger.info("SECRET::"+SECRET);
		securityExposerFunction.decodeJWTwithSignature(tokenInfo,
				SECRET);		
		logger.info("Verification done from Signature with JWT");
		logger.info("claimBody.checkSum :: " + claimBody.get("checkSum"));
		logger.info("Generate the CheckSum :: "
				+ SecurityExposerFunction.generateCheckSum(populatedMultiPleMapToMap(formData),SECRET)
				+ populatedMultiPleMapToMap(formData));

		if (!validateCheckSum(claimBody.get("checkSum").toString(),
				SecurityExposerFunction.generateCheckSum(populatedMultiPleMapToMap(formData),SECRET))) {
			throw new ValidationExceptions(CHECKSUM_MISMATCH, FormValidationExceptionEnums.CHECKSUM_MISMATCH);
		}

		logger.info("CheckSum Validation done ...." + merchantDetails.getMerchantID() + "|"
				+ formData.get("orderId").get(0));

		List<TransactionDetails> listTransactionDetails = transactionDetailsRepository
				.findAllByMerchantOrderIdAndMerchantId(formData.get("orderId").get(0), merchantDetails.getMerchantID());

		List<MerchantTransactionResponse> listMerchantTransactionResponse = populateTransactionDetails(
				listTransactionDetails);

		merchantTransaction.setListMerchantTransactionResponse(listMerchantTransactionResponse);
		merchantTransaction.setHeader(createdHeader(listMerchantTransactionResponse.toString(),SECRET));

		return merchantTransaction;
	}
	
	public MerchantRefundResponse generateRefundRequest(String tokenInfo, MultiValueMap<String, String> formData) throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException {		
		
		logger.info("MerchantAPIService generateRefundRequest In this Method.");

		SecurityExposerFunction securityExposerFunction = new SecurityExposerFunction();		

		logger.info("tokenInfo :: " + tokenInfo);
		logger.info("strBody :: " + formData);

		Claims claimBody = securityExposerFunction.decodeJWT(tokenInfo);		
		MerchantDetails merchantDetails = merchantDetailsRepository.findByAppID(claimBody.get("appId").toString());
		logger.info(Utility.convertDTO2JsonString(claimBody));
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}		

		logger.info("Before Verify the Token with Secrect key and Signature");

		securityExposerFunction.decodeJWTwithSignature(tokenInfo,Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()));

		logger.info("Verification done from Signature with JWT");
		logger.info("claimBody.checkSum :: " + claimBody.get("checkSum"));
		logger.info("Generate the CheckSum :: "
				+ SecurityExposerFunction.generateCheckSum(populatedMultiPleMapToMap(formData),
						Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()))+populatedMultiPleMapToMap(formData));

		if (!validateCheckSum(claimBody.get("checkSum").toString(),
				SecurityExposerFunction.generateCheckSum(populatedMultiPleMapToMap(formData),
						Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId())))) {
			throw new ValidationExceptions(CHECKSUM_MISMATCH, FormValidationExceptionEnums.CHECKSUM_MISMATCH);
		}
		
		logger.info("Check sum validation success ...");
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(
				merchantDetails.getMerchantID(),
				formData.get("orderId").get(0),
				UserStatus.PENDING.toString());
		
		if(merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_INITIATE_FAILED, FormValidationExceptionEnums.REFUND_INITIATE_FAILED);
		}
		
		RefundDetails refundDetails = new RefundDetails();
		refundDetails.setAmount(String.valueOf(merchantBalanceSheet.getAmount()));
		refundDetails.setInitiatedBy(merchantDetails.getUuid());
		refundDetails.setMerchantId(merchantDetails.getMerchantID());
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
		
		RefundDetails refundDetailsUpd = refundDetailsRepository.save(refundDetails);
		MerchantRefundResponse merchantRefundResponse = new MerchantRefundResponse();
		merchantRefundResponse.setRefundDetails(refundDetailsUpd);
		merchantRefundResponse.setHeader(createdHeader(refundDetailsUpd.toString(),
				Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId())));
		
		return merchantRefundResponse;
	}

	public Map<String, String> populatedMultiPleMapToMap(MultiValueMap<String, String> formData) {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("orderId", formData.get("orderId").get(0));

		return parameters;
	}

	public boolean validateCheckSum(String inputCheckSum, String generatedCheckSum) {

		if (inputCheckSum.equals(generatedCheckSum)) {
			return true;
		}

		return false;
	}

	public List<MerchantTransactionResponse> populateTransactionDetails(
			List<TransactionDetails> listTransactionDetails) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
			merchantTransactionResponse.setStatus(transactionDetails.getStatus());
			merchantTransactionResponse.setTxtMsg(transactionDetails.getTxtMsg());
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(
						SecurityUtils.decryptSaveDataKMS(transactionDetails.getVpaUPI()).replace("\u0000", ""));
			}
			if (transactionDetails.getPaymentCode() != null) {
				//merchantTransactionResponse.setPaymentCode(SecurityUtils.decryptSaveDataKMS(transactionDetails.getPaymentCode()).replace("\u0000", ""));
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(transactionDetails.getCardNumber())).replace("\u0000", ""));
			}
			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String createdHeader(String strJson, String secretKey) throws NoSuchAlgorithmException {

		return SecurityExposerFunction.generateCheckSum(strJson, secretKey);
	}

	public MerchantTransaction getTransactionDetailsUsingOrderId(String appId, String secret, String orderId) throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException {
		
		SecurityExposerFunction securityExposerFunction = new SecurityExposerFunction();
		MerchantTransaction merchantTransaction = new MerchantTransaction();
		MerchantDetails merchantDetails = paymentMerchantService.getMerchantFromAppId(appId, secret);
		if(merchantDetails == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND + appId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		
		List<TransactionDetails> listTransactionDetails = transactionDetailsRepository
				.findAllByMerchantOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());

		List<MerchantTransactionResponse> listMerchantTransactionResponse = populateTransactionDetails(listTransactionDetails);

		merchantTransaction.setListMerchantTransactionResponse(listMerchantTransactionResponse);
		merchantTransaction.setHeader(createdHeader(listMerchantTransactionResponse.toString(),Encryption.decryptCardNumberOrExpOrCvvKMS(merchantDetails.getSecretId()).trim().strip().replace("\u0000", "")));
				
		return merchantTransaction ;
	}
}
