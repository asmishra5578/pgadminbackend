package com.asktech.admin.service.payout.merchantManagement;

import java.util.Arrays;
import java.util.List;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.payout.merchant.PayUserDetails;
import com.asktech.admin.dto.payout.merchant.WalletCreateReqDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.PayoutUserStatus;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.payout.PayoutApiUserDetails;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.payout.PayoutApiUserDetailsRepo;
import com.asktech.admin.security.Encryption;
import com.asktech.admin.service.PGGatewayAdminService;
import com.asktech.admin.util.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MerchantManagement extends MerchantUtils implements ErrorValues {
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	PayoutApiUserDetailsRepo payoutApiUserDetailsRepo;
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;

	public PayUserDetails payoutCreateUser(PayUserDetails dto) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(dto.getMerchantId());
		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		String secret = Encryption.decryptForFrontEndDataKMS(merchantDetails.getSecretId());
		// logger.info(secret);

		PayoutApiUserDetails payUser = payoutApiUserDetailsRepo.findByMerchantId(dto.getMerchantId());
		// PayUserDetails user = new PayUserDetails();
		if (payUser != null) {
			throw new ValidationExceptions(INFORMATION_EXISTS,
					FormValidationExceptionEnums.INFORMATION_ALREADY_EXISTS_IN_SYSTEM);
		}

		if ((!dto.getWhitelistedip().isEmpty())) {
			if (dto.getWhitelistedip().contains(",")) {
				List<String> list = Arrays.asList(dto.getWhitelistedip().split(","));
				for (String l : list) {
					if (!Validator.isValidIP(l)) {
						throw new ValidationExceptions(INVALID_IP_ADDRESS,
								FormValidationExceptionEnums.INVALID_IP_ADDRESS);
					}
				}
			} else {
				if (!Validator.isValidIP(dto.getWhitelistedip())) {
					throw new ValidationExceptions(INVALID_IP_ADDRESS, FormValidationExceptionEnums.INVALID_IP_ADDRESS);
				}
			}
		}

		payUser = new PayoutApiUserDetails();
		payUser.setMerchantId(dto.getMerchantId());
		payUser.setToken(secret);
		payUser.setWhitelistedip(dto.getWhitelistedip());
		payUser.setWalletCheckStatus(dto.getWalletCheck());
		payUser.setMerchantStatus(PayoutUserStatus.PENDING.toString());
		payoutApiUserDetailsRepo.save(payUser);

		if (dto.getWalletCheck().equalsIgnoreCase("true")) {
			WalletCreateReqDto wallet = new WalletCreateReqDto();
			wallet.setAmount(dto.getAmount());
			wallet.setMainWalletid(dto.getMainWalletid());
			wallet.setName(dto.getName());
			wallet.setStatus(dto.getStatus());
			walletCreation(payUser.getMerchantId(), wallet);
		}

		PayUserDetails res = new PayUserDetails();
		res.setMerchantId(dto.getMerchantId());
		res.setWhitelistedip(dto.getWhitelistedip());
		res.setWalletCheck(dto.getWalletCheck());
		res.setMainWalletid(dto.getMainWalletid());
		res.setName(dto.getName());
		res.setAmount(dto.getAmount());
		res.setStatus(dto.getStatus());

		return res;
	}

	public void updatePayoutUser(PayUserDetails dto) {

	}

	public void getAllPayoutUsers(){

	}

	public void getPayoutUser(String merchantid){

	}


	// --------------PG Associations -------------------//

	public void changePgAssociationToMerchant(String pgid, String service) {
/*
{
"merchantid":"merchantid",
	[
		{
			"pgid":"id",
			"service":"UPI"
		}
	]
}
*/ 
	}

	public void getPgAssociations(String merchantid) {

	}

	public void getAllPgAssociations() {

	}

	// ------------------------ Commission ------------------------//
	public void setMerchantCommissions() {
/*{
"gateway":"gatewayname",
	[
		{
		"slabname":"",
		"serviceType":"",
		"deductionAmount":""
		}
	]
}*/


	}

	public void getMerchantCommissions(){

	}

	public void getAllMerchantCommissions(){
		
	}

	// -------------------------------------------------UTILS ---------------------------------

	@Value("${apiPayoutEndPoint.payoutUrl}")
	String payoutUrl;
	@Value("${apiPayoutEndPoint.payoutBaseUrl}")
	String payoutBaseUrl;

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
		getDet.setWalletCheckStatus("TRUE");
		payoutApiUserDetailsRepo.save(getDet);
		return MerchantUtils.createWallet(dto, payoutBaseUrl, merchantid);

	}
}
