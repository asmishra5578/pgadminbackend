package com.asktech.admin.service.payout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.admin.TransactionChangeResponceListDto;
import com.asktech.admin.dto.admin.UpdateTransactionDetailsRequestDto;
import com.asktech.admin.dto.merchant.TransactionReversalRequest;
import com.asktech.admin.dto.merchant.TransactionReversalResponse;
import com.asktech.admin.dto.merchant.WalletUpdateReqDto;
import com.asktech.admin.dto.merchant.WalletUpdateResDto;
import com.asktech.admin.dto.payout.Wallet.MerchantRecharge;
import com.asktech.admin.dto.payout.Wallet.WalletRechargeRequest;
import com.asktech.admin.dto.payout.Wallet.WalletRechargeResponse;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeRequestDto;
import com.asktech.admin.dto.payout.merchant.BalanceCheckMainWallet;
import com.asktech.admin.dto.payout.merchant.BalanceCheckMerRes;
import com.asktech.admin.dto.payout.pgPayout.ConfigPgMerchantDto;
import com.asktech.admin.dto.payout.pgPayout.ConfigPgNameMerchant;
import com.asktech.admin.dto.payout.pgPayout.MerchantPgConfig;
import com.asktech.admin.dto.payout.pgPayout.PgConfigResponse;
import com.asktech.admin.dto.payout.pgPayout.PgCreationDto;
import com.asktech.admin.dto.payout.pgPayout.PgDetails;
import com.asktech.admin.dto.payout.pgPayout.PgResponse;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.TransactionChangeRequest;
import com.asktech.admin.model.payout.PayoutApiUserDetails;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.UserAdminDetailsRepository;
import com.asktech.admin.repository.payout.PayoutApiUserDetailsRepo;
import com.fasterxml.jackson.core.JsonProcessingException;

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Service
public class PayOutAdmin implements ErrorValues {
	@Value("${apiPayoutEndPoint.payoutUrl}")
	String payoutUrl;
	@Value("${apiPayoutEndPoint.payoutBaseUrl}")
	String payoutBaseUrl;

	@Autowired
	PayoutApiUserDetailsRepo payoutApiUserDetailsRepo;

	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;

	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;

	public List<PayoutApiUserDetails> findAllPayoutMerchant() {
		return payoutApiUserDetailsRepo.findAll();
	}

	public WalletRechargeResponse walletRecharge(WalletRechargeRequest walletRechargeRequest)
			throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository
				.findByMerchantID(walletRechargeRequest.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions("MERCHANT NOT FOUND", FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		WalletRechargeResponse walletRechargeResponse = new WalletRechargeResponse();
		kong.unirest.HttpResponse<WalletRechargeResponse> responce = Unirest
				.post(payoutBaseUrl + "admin/wallet/rechargeRequest").header("Content-Type", "application/json")
				.body(walletRechargeRequest).asObject(WalletRechargeResponse.class);
		walletRechargeResponse = responce.getBody();
		return walletRechargeResponse;
	}

	public MerchantRecharge getByMerchantRechargeByUtrid(String utrid) {
		MerchantRecharge merchantRecharge2 = new MerchantRecharge();

		kong.unirest.HttpResponse<MerchantRecharge> responce = Unirest
				.get(payoutBaseUrl + "admin/wallet/MerchantRechargeByUtrid/" + utrid)
				.header("Content-Type", "application/json").asObject(MerchantRecharge.class);

		merchantRecharge2 = responce.getBody();
		// UserAdminDetails user =
		// userAdminDetailsRepository.findByuuid(merchantRecharge2.getRechargeAgent());
		// MerchantRecharge2 merchant=new MerchantRecharge2();
		// merchant.setAmount(merchantRecharge2.getAmount());
		// merchant.setBankName(merchantRecharge2.getBankName());
		// merchant.setCommission(merchantRecharge2.getCommission());
		// merchant.setId(merchantRecharge2.getId());
		// merchant.setMainWalletId(merchantRecharge2.getMainWalletId());
		// merchant.setMerchantId(merchantRecharge2.getMerchantId());
		// merchant.setNote1(merchantRecharge2.getNote1());
		// merchant.setNote2(merchantRecharge2.getNote2());
		// merchant.setNote3(merchantRecharge2.getNote3());
		// merchant.setRechargeAgent(merchantRecharge2.getRechargeAgent());
		// merchant.setRechargeAgentName(user.getUserName());
		// merchant.setRechargeId(merchantRecharge2.getRechargeId());
		// merchant.setReferenceId(merchantRecharge2.getReferenceId());
		// merchant.setReferenceName(merchantRecharge2.getReferenceName());
		return merchantRecharge2;
	}

	public List<MerchantRecharge> getByMerchantRechargeByMerchantId(String merchantId) {
		List<MerchantRecharge> merchantRecharge12 = new ArrayList<>();
		// List<MerchantRecharge2> merchantRecharge = new ArrayList<>();

		kong.unirest.HttpResponse<List<MerchantRecharge>> responce = Unirest
				.get(payoutBaseUrl + "admin/wallet/MerchantRechargeByMerchantId/" + merchantId)
				.header("Content-Type", "application/json").asObject(new GenericType<List<MerchantRecharge>>() {
				});

		merchantRecharge12 = responce.getBody();

		// for (MerchantRecharge merchantRecharge2 : merchantRecharge12) {
		// UserAdminDetails user =
		// userAdminDetailsRepository.findByuuid(merchantRecharge2.getRechargeAgent());
		// if(user==null){
		// MerchantRecharge2 merchant=new MerchantRecharge2();
		// merchant.setAmount(merchantRecharge2.getAmount());
		// merchant.setBankName(merchantRecharge2.getBankName());
		// merchant.setCommission(merchantRecharge2.getCommission());
		// merchant.setId(merchantRecharge2.getId());
		// merchant.setMainWalletId(merchantRecharge2.getMainWalletId());
		// merchant.setMerchantId(merchantRecharge2.getMerchantId());
		// merchant.setNote1(merchantRecharge2.getNote1());
		// merchant.setNote2(merchantRecharge2.getNote2());
		// merchant.setNote3(merchantRecharge2.getNote3());
		// merchant.setRechargeAgent(merchantRecharge2.getRechargeAgent());
		// merchant.setRechargeAgentName("User Not Found");
		// merchant.setRechargeId(merchantRecharge2.getRechargeId());
		// merchant.setReferenceId(merchantRecharge2.getReferenceId());
		// merchant.setReferenceName(merchantRecharge2.getReferenceName());
		// }
		// MerchantRecharge2 merchant=new MerchantRecharge2();
		// merchant.setAmount(merchantRecharge2.getAmount());
		// merchant.setBankName(merchantRecharge2.getBankName());
		// merchant.setCommission(merchantRecharge2.getCommission());
		// merchant.setId(merchantRecharge2.getId());
		// merchant.setMainWalletId(merchantRecharge2.getMainWalletId());
		// merchant.setMerchantId(merchantRecharge2.getMerchantId());
		// merchant.setNote1(merchantRecharge2.getNote1());
		// merchant.setNote2(merchantRecharge2.getNote2());
		// merchant.setNote3(merchantRecharge2.getNote3());
		// merchant.setRechargeAgent(merchantRecharge2.getRechargeAgent());
		// merchant.setRechargeAgentName(user.getUserName());
		// merchant.setRechargeId(merchantRecharge2.getRechargeId());
		// merchant.setReferenceId(merchantRecharge2.getReferenceId());
		// merchant.setReferenceName(merchantRecharge2.getReferenceName());

		// merchantRecharge.add(merchant);

		// }

		return merchantRecharge12;
	}

	public List<MerchantRecharge> getMerchantRechargeDateRange(String dateFrom, String dateTo) {
		List<MerchantRecharge> merchantRecharge12 = new ArrayList<>();
		// List<MerchantRecharge2> merchantRecharge = new ArrayList<>();

		kong.unirest.HttpResponse<List<MerchantRecharge>> responce = Unirest
				.get(payoutBaseUrl + "admin/wallet/getMerchantRechargeDateRange/" + dateFrom + "/" + dateTo)
				.header("Content-Type", "application/json").asObject(new GenericType<List<MerchantRecharge>>() {
				});

		merchantRecharge12 = responce.getBody();

		// for (MerchantRecharge merchantRecharge2 : merchantRecharge12) {
		// UserAdminDetails user =
		// userAdminDetailsRepository.findByuuid(merchantRecharge2.getRechargeAgent());
		// MerchantRecharge2 merchant=new MerchantRecharge2();
		// merchant.setAmount(merchantRecharge2.getAmount());
		// merchant.setBankName(merchantRecharge2.getBankName());
		// merchant.setCommission(merchantRecharge2.getCommission());
		// merchant.setId(merchantRecharge2.getId());
		// merchant.setMainWalletId(merchantRecharge2.getMainWalletId());
		// merchant.setMerchantId(merchantRecharge2.getMerchantId());
		// merchant.setNote1(merchantRecharge2.getNote1());
		// merchant.setNote2(merchantRecharge2.getNote2());
		// merchant.setNote3(merchantRecharge2.getNote3());
		// merchant.setRechargeAgent(merchantRecharge2.getRechargeAgent());
		// merchant.setRechargeAgentName(user.getUserName());
		// merchant.setRechargeId(merchantRecharge2.getRechargeId());
		// merchant.setReferenceId(merchantRecharge2.getReferenceId());
		// merchant.setReferenceName(merchantRecharge2.getReferenceName());

		// merchantRecharge.add(merchant);
		// }
		return merchantRecharge12;
	}

	@SuppressWarnings("deprecation")
	public TransactionChangeResponceListDto updateTransactionStatus(
			TransactionChangeRequestDto transactionChangeRequestDto) throws ValidationExceptions {
		TransactionChangeResponceListDto resDto = new TransactionChangeResponceListDto();
		ArrayList<String> satuslist = new ArrayList<String>();
		satuslist.add("SUCCESS");
		satuslist.add("FAILURE");
		satuslist.add("PENDING");
		satuslist.add("REFUND");
		List<UpdateTransactionDetailsRequestDto> checkStatus = transactionChangeRequestDto.getUpdateDataDto().stream()
				.filter(o1 -> satuslist.stream().noneMatch(o2 -> o2.equals(o1.getTransactionStatus())))
				.collect(Collectors.toList());
		if (!checkStatus.isEmpty()) {
			throw new ValidationExceptions(TXN_STATUS_NOT_MATCH, FormValidationExceptionEnums.TXN_STATUS_NOT_MATCH);
		}
		List<UpdateTransactionDetailsRequestDto> checkOrderIdNull = new ArrayList<>();
		transactionChangeRequestDto.getUpdateDataDto().forEach(o -> {
			if (StringUtils.isEmpty(o.getInternalOrderId())) {
				checkOrderIdNull.add(o);
			}
		});
		if (!checkOrderIdNull.isEmpty()) {
			throw new ValidationExceptions(INTERNAL_ORDER_ID_CAN_NOT_NULL,
					FormValidationExceptionEnums.INTERNAL_ORDER_ID_CAN_NOT_NULL);
		}
		kong.unirest.HttpResponse<TransactionChangeResponceListDto> responce = Unirest
				.put(payoutBaseUrl + "controller/updateTransactionStatus").header("Content-Type", "application/json")
				.body(transactionChangeRequestDto).asObject(TransactionChangeResponceListDto.class);
		resDto = responce.getBody();
		return resDto;
	}

	public BalanceCheckMerRes balanceCheck(String merchantid) throws ValidationExceptions {
		PayoutApiUserDetails merchantDetails = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (merchantDetails == null) {
			throw new ValidationExceptions("MERCHANT NOT FOUND", FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		BalanceCheckMerRes balanceCheckMerRes = new BalanceCheckMerRes();
		kong.unirest.HttpResponse<BalanceCheckMerRes> responce = Unirest
				.get(payoutBaseUrl + "admin/wallet/walletBalance/" + merchantid)
				// .header("Content-Type", "application/json")
				.asObject(BalanceCheckMerRes.class);

		balanceCheckMerRes = responce.getBody();
		return balanceCheckMerRes;
	}

	public BalanceCheckMainWallet mainWalletBalanceByMerchantId(String merchantid) throws ValidationExceptions {
		PayoutApiUserDetails merchantDetails = payoutApiUserDetailsRepo.findByMerchantId(merchantid);
		if (merchantDetails == null) {
			throw new ValidationExceptions("MERCHANT NOT FOUND", FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		BalanceCheckMainWallet balanceCheckMainWallet = new BalanceCheckMainWallet();
		kong.unirest.HttpResponse<BalanceCheckMainWallet> responce = Unirest
				.get(payoutBaseUrl + "admin/wallet/mainWalletBalanceByMerchantId/" + merchantid)
				.header("Content-Type", "application/json").asObject(BalanceCheckMainWallet.class);

		balanceCheckMainWallet = responce.getBody();
		return balanceCheckMainWallet;
	}

	public List<MerchantRecharge> getAllByMerchantRecharge() {
		List<MerchantRecharge> merchantRecharge12 = new ArrayList<>();
		// List<MerchantRecharge2> merchantRecharge = new ArrayList<>();

		kong.unirest.HttpResponse<List<MerchantRecharge>> responce = Unirest
				.get(payoutBaseUrl + "admin/wallet/getAllByMerchantRecharge")
				// .header("Content-Type", "application/json")
				.asObject(new GenericType<List<MerchantRecharge>>() {
				});
		merchantRecharge12 = responce.getBody();

		// for (MerchantRecharge merchantRecharge2 : merchantRecharge12) {
		// UserAdminDetails user =
		// userAdminDetailsRepository.findByuuid(merchantRecharge2.getRechargeAgent());
		// MerchantRecharge2 merchant=new MerchantRecharge2();
		// merchant.setAmount(merchantRecharge2.getAmount());
		// merchant.setBankName(merchantRecharge2.getBankName());
		// merchant.setCommission(merchantRecharge2.getCommission());
		// merchant.setId(merchantRecharge2.getId());
		// merchant.setMainWalletId(merchantRecharge2.getMainWalletId());
		// merchant.setMerchantId(merchantRecharge2.getMerchantId());
		// merchant.setNote1(merchantRecharge2.getNote1());
		// merchant.setNote2(merchantRecharge2.getNote2());
		// merchant.setNote3(merchantRecharge2.getNote3());
		// merchant.setRechargeAgent(merchantRecharge2.getRechargeAgent());
		// merchant.setRechargeAgentName(user.getUserName());
		// merchant.setRechargeId(merchantRecharge2.getRechargeId());
		// merchant.setReferenceId(merchantRecharge2.getReferenceId());
		// merchant.setReferenceName(merchantRecharge2.getReferenceName());

		// merchantRecharge.add(merchant);
		// }
		return merchantRecharge12;
	}

	public List<TransactionChangeRequest> getallTransactionChangeRequest() {
		List<TransactionChangeRequest> transactionChangeResponce = new ArrayList<>();

		kong.unirest.HttpResponse<List<TransactionChangeRequest>> responce = Unirest
				.get(payoutBaseUrl + "controller/getallTransactionChangeRequest")
				.header("Content-Type", "application/json").asObject(new GenericType<List<TransactionChangeRequest>>() {
				});

		transactionChangeResponce = responce.getBody();
		return transactionChangeResponce;
	}

//pg creation

	public PgResponse createPg(PgCreationDto dto) throws ValidationExceptions {
		PgResponse pgdetailsResponce = new PgResponse();
		kong.unirest.HttpResponse<PgResponse> responce = Unirest.post(payoutBaseUrl + "controller/pgCreation")
				.header("Content-Type", "application/json").body(dto).asObject(PgResponse.class);
		pgdetailsResponce = responce.getBody();
		return pgdetailsResponce;

	}

//update pg
	public PgResponse updatePg(PgCreationDto dto) throws ValidationExceptions {
		PgResponse pgdetailsResponce = new PgResponse();
		kong.unirest.HttpResponse<PgResponse> responce = Unirest.post(payoutBaseUrl + "controller/pgUpdate")
				.header("Content-Type", "application/json").body(dto).asObject(PgResponse.class);
		pgdetailsResponce = responce.getBody();
		return pgdetailsResponce;

	}

	public List<PgDetails> getAllPg() {
		List<PgDetails> pglist = new ArrayList<>();
		kong.unirest.HttpResponse<List<PgDetails>> responce = Unirest.get(payoutBaseUrl + "controller/getAllPg")
				.header("Content-Type", "application/json").asObject(new GenericType<List<PgDetails>>() {
				});

		pglist = responce.getBody();
		return pglist;
	}

	public PgConfigResponse updateConfigPgMerchant(ConfigPgMerchantDto dto) throws ValidationExceptions {
		PayoutApiUserDetails merchantDetails = payoutApiUserDetailsRepo.findByMerchantId(dto.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions("MERCHANT NOT FOUND", FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		PgConfigResponse pgdetailsResponce = new PgConfigResponse();

		kong.unirest.HttpResponse<PgConfigResponse> responce = Unirest
				.post(payoutBaseUrl + "controller/updateConfigPgMerchant").header("Content-Type", "application/json")
				.body(dto).asObject(PgConfigResponse.class);
		pgdetailsResponce = responce.getBody();
		return pgdetailsResponce;
	}

	public PgConfigResponse createConfigPgMerchant(ConfigPgMerchantDto dto) throws ValidationExceptions {
		PayoutApiUserDetails merchantDetails = payoutApiUserDetailsRepo.findByMerchantId(dto.getMerchantId());
		if (merchantDetails == null) {
			throw new ValidationExceptions("MERCHANT NOT FOUND", FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		PgConfigResponse pgdetailsResponce = new PgConfigResponse();

		kong.unirest.HttpResponse<PgConfigResponse> responce = Unirest
				.post(payoutBaseUrl + "controller/configPgMerchant").header("Content-Type", "application/json")
				.body(dto).asObject(PgConfigResponse.class);
		pgdetailsResponce = responce.getBody();
		return pgdetailsResponce;
	}

	public List<MerchantPgConfig> getAllMerchantPgLinks() {
		List<MerchantPgConfig> merchantPgConfigs = new ArrayList<>();

		kong.unirest.HttpResponse<List<MerchantPgConfig>> responce = Unirest
				.get(payoutBaseUrl + "controller/getAllConfigPgMerchant").header("Content-Type", "application/json")
				.asObject(new GenericType<List<MerchantPgConfig>>() {
				});

		merchantPgConfigs = responce.getBody();
		return merchantPgConfigs;
	}

	public List<ConfigPgNameMerchant> getAllConfigPgNameMerchant() {
		List<ConfigPgNameMerchant> merchantPgConfigs = new ArrayList<>();

		kong.unirest.HttpResponse<List<ConfigPgNameMerchant>> responce = Unirest
				.get(payoutBaseUrl + "controller/getAllConfigPgNameMerchant").header("Content-Type", "application/json")
				.asObject(new GenericType<List<ConfigPgNameMerchant>>() {
				});

		merchantPgConfigs = responce.getBody();
		return merchantPgConfigs;
	}

	public WalletUpdateResDto walletUpdateStatusAndHoldAmount(WalletUpdateReqDto walletUpdateReqDto)
			throws ValidationExceptions {
		WalletUpdateResDto dto = new WalletUpdateResDto();
		kong.unirest.HttpResponse<WalletUpdateResDto> responce = Unirest
				.put(payoutBaseUrl + "admin/wallet/walletUpdateStatusAndHoldAmount")
				.header("Content-Type", "application/json").body(walletUpdateReqDto).asObject(WalletUpdateResDto.class);

		dto = responce.getBody();

		return dto;

	}

	public TransactionReversalResponse transactionReversal(TransactionReversalRequest dto)
			throws JsonProcessingException {

		TransactionReversalResponse pgdetailsResponce = new TransactionReversalResponse();

		kong.unirest.HttpResponse<TransactionReversalResponse> responce = Unirest
				.post(payoutBaseUrl + "controller/transactionReversal").header("Content-Type", "application/json")
				.body(dto).asObject(TransactionReversalResponse.class);
		pgdetailsResponce = responce.getBody();
		return pgdetailsResponce;
	}

	public TransactionReversalResponse transactionReversalWalletRecharge(TransactionReversalRequest dto)
			throws JsonProcessingException {

		TransactionReversalResponse pgdetailsResponce = new TransactionReversalResponse();

		kong.unirest.HttpResponse<TransactionReversalResponse> responce = Unirest
				.post(payoutBaseUrl + "controller/walletRecharge").header("Content-Type", "application/json").body(dto)
				.asObject(TransactionReversalResponse.class);
		pgdetailsResponce = responce.getBody();
		return pgdetailsResponce;
	}

	public void updateTransactionDetailsByInternalOrderId(String internalOrderId,
			UpdateTransactionDetailsRequestDto dto) {
		HttpResponse<String> responce = Unirest
				.put(payoutBaseUrl + "controller/update/transation/details/" + internalOrderId + "")
				.header("Content-Type", "application/json").body(dto).asString();

	}

}
