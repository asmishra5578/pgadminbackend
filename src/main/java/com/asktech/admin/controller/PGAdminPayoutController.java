package com.asktech.admin.controller;

import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.admin.UpdateTransactionDetailsRequestDto;
import com.asktech.admin.dto.merchant.MainWalletDto;
import com.asktech.admin.dto.merchant.MainWalletRechargeReqDto;
import com.asktech.admin.dto.merchant.MainWalletReversalReqDto;
import com.asktech.admin.dto.merchant.PayoutAndWalletDto;
import com.asktech.admin.dto.merchant.RechargeServiceDto;
import com.asktech.admin.dto.merchant.TransactionReversalRequest;
import com.asktech.admin.dto.merchant.TransactionServiceDto;
import com.asktech.admin.dto.merchant.WalletReversalReqDto;
import com.asktech.admin.dto.merchant.WalletUpdateReqDto;
import com.asktech.admin.dto.payout.Wallet.WalletRechargeRequest;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeRequestDto;
import com.asktech.admin.dto.payout.merchant.BalanceCheckMainWallet;
import com.asktech.admin.dto.payout.merchant.BalanceCheckMerRes;
import com.asktech.admin.dto.payout.merchant.PayUserDetails;
import com.asktech.admin.dto.payout.merchant.TransactionFilterReq;
import com.asktech.admin.dto.payout.merchant.TransactionReportMerReq;
import com.asktech.admin.dto.payout.merchant.TransactionRequestFilterMerReq;
import com.asktech.admin.dto.payout.merchant.TransferStatusReq;
import com.asktech.admin.dto.payout.merchant.WalletCreateReqDto;
import com.asktech.admin.dto.payout.merchant.WalletFilterReq;
import com.asktech.admin.dto.payout.merchant.WalletRechargeReqDto;
import com.asktech.admin.dto.payout.merchant.WalletTransferMerReq;
import com.asktech.admin.dto.payout.pgPayout.ConfigPgMerchantDto;
import com.asktech.admin.dto.payout.pgPayout.PgCreationDto;
import com.asktech.admin.dto.payout.pgPayout.PgResponse;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.dto.merchant.TrxnWithParameter;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.model.payout.PayoutApiUserDetails;
import com.asktech.admin.service.PGGatewayAdminService;
import com.asktech.admin.service.payout.PayOutAdmin;
import com.asktech.admin.service.payout.PayoutMerchant;
import com.asktech.admin.util.JwtUserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class PGAdminPayoutController implements ErrorValues{

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);

	@Autowired
	PayoutMerchant payoutMerchant;
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	@Autowired
	JwtUserValidator jwtUserValidator;
	@Autowired
	private PayOutAdmin payOutAdmin;

	/*** @author modified by abhimanyu start */

	@GetMapping(value = "api/admin/transactionFilterReport")
	@ApiOperation(value = "Transaction Report", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> transactionFilterReport(@RequestParam("uuid") String uuid,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "bankaccount", required = false) String bankaccount,
			@RequestParam(value = "beneficiaryName", required = false) String beneficiaryName,
			@RequestParam(value = "ifsc", required = false) String ifsc,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "transactionType", required = false) String transactionType)
			throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		TransactionFilterReq dto = new TransactionFilterReq();
		dto.setFromDate(fromDate);
		dto.setToDate(toDate);
		dto.setMerchantId(merchantId);
		dto.setBankaccount(bankaccount);
		dto.setBeneficiaryName(beneficiaryName);
		dto.setIfsc(ifsc);
		dto.setOrderId(orderId);
		dto.setTransactionType(transactionType);
		dto.setStatus(status);
		String res = payoutMerchant.transactionFilter(dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	/*** @author modified by abhimanyu end */

	@PostMapping(value = "api/admin/payOutUserCreation")
	@ApiOperation(value = "payout user Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> payOutUserCreation(@RequestParam("uuid") String uuid, @RequestBody PayUserDetails dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		PayUserDetails res = payoutMerchant.payoutUser(dto);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payout", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@GetMapping(value = "api/admin/getPayoutUser")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getPayoutUser(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantId", required = false) String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		List<PayoutApiUserDetails> res = payoutMerchant.getPayOutUser(merchantid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payoutUser", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@GetMapping(value = "api/admin/getAllPayoutUsers")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllPayoutUsers(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		List<PayoutApiUserDetails> res = payoutMerchant.getAllPayoutUsers();
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payoutUser", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@GetMapping(value = "api/admin/getPayoutUserWithDetails")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getPayoutUserMapping(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantId", required = false) String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		List<PayoutAndWalletDto> res = payoutMerchant.payoutMerdet(merchantid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payoutUser", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@GetMapping(value = "api/admin/getUserIp")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getUserIp(@RequestParam("uuid") String uuid, @RequestParam("merchantId") String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		List<String> res = payoutMerchant.getIpAddress(merchantid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payout", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@PutMapping(value = "api/admin/updateUserIp")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateUserIp(@RequestParam("uuid") String uuid, @RequestBody PayUserDetails dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		PayoutApiUserDetails res = payoutMerchant.updateUserIpAddress(dto);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payout", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@PutMapping(value = "api/admin/updatePayoutUserStatus")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updatePayoutUserStatus(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		PayoutApiUserDetails res = payoutMerchant.updateUserStatus(status, merchantid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("payout", res);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@PostMapping(value = "api/admin/WalletCreation")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminWalletCreation(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody WalletCreateReqDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.walletCreation(merchantid, dto);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/admin/WalletRecharge")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminWalletRecharge(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody WalletRechargeReqDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.walletRecharge(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/transactionReport")
	@ApiOperation(value = "Transaction Report", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> transactionReport(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody TransactionReportMerReq dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.TransactionReport(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/walletReport")
	@ApiOperation(value = "Wallet Report", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> walletReport(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody TransactionReportMerReq dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.WalletReport(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/transactionStatus")
	@ApiOperation(value = "Transaction Status", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getStatus(@RequestParam("uuid") String uuid, @RequestParam("merchantId") String merchantid,
			@RequestBody TransferStatusReq dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.TransactionStatus(dto, merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/transactionReportFilter")
	@ApiOperation(value = "Transaction Report", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> transactionReportFilter(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody TransactionRequestFilterMerReq dto)
			throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.TransactionReportFilter(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/walletFilterReport")
	@ApiOperation(value = "Transaction Report", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> walletFilterReport(@RequestParam("uuid") String uuid,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "credit_debit", required = false) String credit_debit,
			@RequestParam(value = "walletId", required = false) String walletId,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "transactionId", required = false) String transactionId)
			throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());

		WalletFilterReq dto = new WalletFilterReq();
		dto.setFromDate(fromDate);
		dto.setToDate(toDate);
		dto.setMerchantId(merchantId);
		dto.setStatus(status);
		dto.setCreditDebit(credit_debit);
		dto.setWalletId(walletId);
		dto.setTransactionId(transactionId);
		dto.setTransactionType(transactionType);
		String res = payoutMerchant.walletFilter(dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	// @GetMapping(value = "api/admin/AllWalletReport")
	// @ApiOperation(value = "Transaction Report", authorizations = {
	// @Authorization(value = "apiKey") })
	// public ResponseEntity<?> AllWalletReport(@RequestParam("uuid") String uuid)
	// throws UserException, JWTException, SessionExpiredException {
	//
	// UserAdminDetails userAdminDetails =
	// jwtUserValidator.validatebyJwtAdminDetails(uuid);
	// logger.info("User Validation done :: "+userAdminDetails.getEmailId());
	// String res = payoutMerchant.AllWallet();
	//
	// return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,
	// "application/json").body(res);
	// }
	//
	// @GetMapping(value = "api/admin/AllTransactionReport")
	// @ApiOperation(value = "Transaction Report", authorizations = {
	// @Authorization(value = "apiKey") })
	// public ResponseEntity<?> AllTransactionReport(@RequestParam("uuid") String
	// uuid) throws UserException, JWTException, SessionExpiredException {
	//
	// UserAdminDetails userAdminDetails =
	// jwtUserValidator.validatebyJwtAdminDetails(uuid);
	// logger.info("User Validation done :: "+userAdminDetails.getEmailId());
	// String res = payoutMerchant.AllTransaction();
	//
	// return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,
	// "application/json").body(res);
	// }

	@GetMapping(value = "api/admin/PayoutWalletlist")
	@ApiOperation(value = "wallet list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getPayoutWalletList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.walletlist();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/PayoutWalletlistMerchantWise")
	@ApiOperation(value = "wallet list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getPayoutWalletListMerchantWise(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.walletlistMerchantwise(merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/rechargeCommission")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> rechargeCommission(@RequestParam("uuid") String uuid, @RequestBody RechargeServiceDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.rechargeServiceCommission(dto);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/transactionCommission")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> transactionCommission(@RequestParam("uuid") String uuid,
			@RequestBody TransactionServiceDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.transactionServiceCommission(dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PutMapping(value = "api/updateRechargeCommission")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateRechargeCommission(@RequestParam("uuid") String uuid,
			@RequestBody RechargeServiceDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.updateRechargeServiceCommission(dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PutMapping(value = "api/updateTransactionCommission")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateTransactionCommission(@RequestParam("uuid") String uuid,
			@RequestBody TransactionServiceDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.updateTransactionServiceCommission(dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/getRechargeCommissionList")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getRechargeCommissionList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.getRechargeServiceCommissionList();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/getTransactionCommissionList")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getTransactionCommissionList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.getTransactionServiceCommissionList();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/getRechargeCommissionListMerchantwise")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getRechargeCommissionListMerchantwise(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.getRechargeServiceCommissionListMerchantwise(merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/getTransactionCommissionListMerchantwise")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getTransactionCommissionListMerchantwise(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.getTransactionServiceCommissionListMerchantwise(merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/mainWalletRecharge")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> mainWalletRecharge(@RequestParam("uuid") String uuid,
			@RequestParam("walletId") String walletId, @RequestBody MainWalletRechargeReqDto dto)
			throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.mainWalletRechargeSer(dto, walletId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/mainWalletReversal")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> mainWalletReversal(@RequestParam("uuid") String uuid,
			@RequestParam("walletId") String walletId, @RequestBody MainWalletReversalReqDto dto)
			throws UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.mainWalletReversalSer(dto, walletId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/walletUpdate")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> walletUpdate(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody WalletUpdateReqDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.walletUpdateSer(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/walletRefund")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> walletRefund(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody WalletRechargeReqDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.walletRefundSer(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/walletReversal")
	@ApiOperation(value = "wallet Recharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> walletReversal(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid, @RequestBody WalletReversalReqDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("User Validation done :: " + userAdminDetails.getEmailId());
		String res = payoutMerchant.walletReversal(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/getMainWalletBalance")
	@ApiOperation(value = "wallet list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMainWalletBalance(@RequestParam("uuid") String uuid,
			@RequestParam("walletId") String walletId) throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.mainWalletBalanceSer(walletId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}
	
	@GetMapping(value = "api/getMerchantWalletBalance")
	@ApiOperation(value = "wallet list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantWalletBalance(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.merchantWalletBalance(merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/getMainWalletList")
	@ApiOperation(value = "wallet list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMainWalletList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.mainWalletList();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/mainWalletCreation")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> mainWalletCreation(@RequestParam("uuid") String uuid, @RequestBody MainWalletDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.mainWalletCreation(dto);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PutMapping(value = "api/updateMainWallet")
	@ApiOperation(value = "wallet Creation", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMainWallet(@RequestParam("uuid") String uuid,
			@RequestParam("walletId") String walletId, @RequestBody MainWalletDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.updateMainWallet(dto, walletId);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/getMainWalletAssociation")
	@ApiOperation(value = "wallet list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMainWalletAssociation(@RequestParam("uuid") String uuid,
			@RequestParam("mainWalletid") String mainWalletid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.mainWalletlistAssociation(mainWalletid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "api/rechargeRequest")
	@ApiOperation(value = "merchantRecharge create", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> rechargeCommission(@RequestBody WalletRechargeRequest dto,
			@RequestParam("uuid") String uuid) throws ValidationExceptions, JsonProcessingException, ParseException,
			UserException, JWTException, SessionExpiredException {
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		WalletRechargeRequest walletRechargeRequest = new WalletRechargeRequest();
		walletRechargeRequest.setAmount(dto.getAmount());
		walletRechargeRequest.setBankName(dto.getBankName());
		walletRechargeRequest.setCommission(dto.getCommission());
		walletRechargeRequest.setMerchantId(dto.getMerchantId());
		walletRechargeRequest.setNote1(dto.getNote1());
		walletRechargeRequest.setNote2(dto.getNote2());
		walletRechargeRequest.setNote3(dto.getNote3());
		walletRechargeRequest.setRechargeAgent(uuid);
		walletRechargeRequest.setRechargeAgentName(userAdminDetails.getUserName());
		walletRechargeRequest.setReferenceId(dto.getReferenceId());
		walletRechargeRequest.setReferenceName(dto.getReferenceName());
		walletRechargeRequest.setUtrid(dto.getUtrid());
		walletRechargeRequest.setRechargeType(dto.getRechargeType());
		return ResponseEntity.ok().body(payOutAdmin.walletRecharge(walletRechargeRequest));

	}

	@GetMapping(value = "api/MerchantRechargeByMerchantId/{merchantid}")
	@ApiOperation(value = "merchantRecharge list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getByMerchantRechargeByMerchantId(@PathVariable String merchantid) {
		return ResponseEntity.ok().body(payOutAdmin.getByMerchantRechargeByMerchantId(merchantid));
	}

	@GetMapping(value = "api/MerchantRechargeByUtrid/{utrid}")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getByMerchantRechargeByUtrid(@PathVariable("utrid") String utrid) {
		return ResponseEntity.ok().body(payOutAdmin.getByMerchantRechargeByUtrid(utrid));
	}

	@GetMapping(value = "api/getMerchantRechargeDateRange/{dateFrom}/{dateTo}")
	@ApiOperation(value = "merchantRecharge list", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantRechargeDateRange(@PathVariable String dateFrom, @PathVariable String dateTo) {
		return ResponseEntity.ok().body(payOutAdmin.getMerchantRechargeDateRange(dateFrom, dateTo));
	}

	@PutMapping(value = "api/updateTransactionStatus")
	@ApiOperation(value = "Transaction update payout", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateTransactionStatus(@RequestBody TransactionChangeRequestDto dto,
			@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		if(dto.getUpdateDataDto().isEmpty()) {
			throw new ValidationExceptions(UPDATE_TXN_STATUS_ERROR, FormValidationExceptionEnums.UPDATE_TXN_STATUS_ERROR);
		}
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		TransactionChangeRequestDto transactionChangeRequestDto = new TransactionChangeRequestDto();
		transactionChangeRequestDto.setUuid(uuid);
		transactionChangeRequestDto.setUpdateDataDto(dto.getUpdateDataDto());
		return ResponseEntity.ok().body(payOutAdmin.updateTransactionStatus(transactionChangeRequestDto));

	}

	@GetMapping(value = "api/walletBalance/{merchantid}")
	@ApiOperation(value = "wallet balance by merchantid", authorizations = {
			@Authorization(value = "apmerchantidiKey") })
	public ResponseEntity<?> walletBalance(@PathVariable String merchantid) throws ValidationExceptions {
		BalanceCheckMerRes balanceCheckMerRes = payOutAdmin.balanceCheck(merchantid);
		return ResponseEntity.ok().body(balanceCheckMerRes);

	}

	@GetMapping(value = "api/MainwalletBalance/{merchantid}")
	@ApiOperation(value = "Transaction update payout", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> mainwalletBalance(@PathVariable String merchantid, @RequestParam("uuid") String uuid)
			throws ValidationExceptions {
		BalanceCheckMainWallet balanceCheckMainWallet = payOutAdmin.mainWalletBalanceByMerchantId(merchantid);
		return ResponseEntity.ok().body(balanceCheckMainWallet);

	}

	@GetMapping(value = "api/getAllByMerchantRecharge")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllByMerchantRecharge(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(payOutAdmin.getAllByMerchantRecharge());
	}

	@GetMapping("api/payout/getallTransactionChangeRequest")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getallTransactionChangeRequest(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(payOutAdmin.getallTransactionChangeRequest());
	}

//PG Creation for payout

	@PostMapping(value = "api/payout/pgCreation")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> pgCreation(@RequestBody PgCreationDto dto, @RequestParam("uuid") String uuid)
			throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.createPg(dto));
	}

	@PostMapping(value = "api/payout/pgUpdate")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> pgUpdate(@RequestBody PgCreationDto dto, @RequestParam("uuid") String uuid)
			throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.updatePg(dto));
	}

	@GetMapping(value = "api/payout/getAllPg")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllPg(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.getAllPg());
	}

	@PostMapping(value = "api/payout/configPgMerchant")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> linkPgMerchant(@RequestBody ConfigPgMerchantDto dto, @RequestParam("uuid") String uuid)
			throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.createConfigPgMerchant(dto));
	}

	@PutMapping(value = "api/payout/updateConfigPgMerchant")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> updateLinkPgMerchant(@RequestBody ConfigPgMerchantDto dto,
			@RequestParam("uuid") String uuid)
			throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.updateConfigPgMerchant(dto));
	}

	@GetMapping(value = "api/payout/getAllConfigPgMerchant")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllLinkPgMerchant(@RequestParam("uuid") String uuid) throws JsonProcessingException,
			ValidationExceptions, ParseException, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.getAllMerchantPgLinks());
	}

	@GetMapping(value = "api/payout/getAllConfigPgNameMerchant")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllConfigPgNameMerchant(@RequestParam("uuid") String uuid)
			throws JsonProcessingException, ValidationExceptions, ParseException, UserException, JWTException,
			SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.getAllConfigPgNameMerchant());
	}

	@PutMapping(value = "api/payout/walletUpdateStatusAndHoldAmount")
	public ResponseEntity<?> walletUpdateStatusAndHoldAmount(@RequestBody WalletUpdateReqDto walletUpdateReqDto,
			@RequestParam("uuid") String uuid)
			throws ValidationExceptions, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(payOutAdmin.walletUpdateStatusAndHoldAmount(walletUpdateReqDto));
	}
//transaction Reversal

	@PostMapping(value = "api/payout/transactionReversal")
	public ResponseEntity<?> transactionReversal(@RequestBody TransactionReversalRequest dto,
			@RequestParam("uuid") String uuid) throws JsonProcessingException, ValidationExceptions, ParseException,
			UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(payOutAdmin.transactionReversal(dto));
	}

	@PostMapping(value = "api/payout/walletRecharge")
	public ResponseEntity<?> walletRecharge(@RequestBody TransactionReversalRequest dto,
			@RequestParam("uuid") String uuid) throws JsonProcessingException, ValidationExceptions, ParseException,
			UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(payOutAdmin.transactionReversalWalletRecharge(dto));
	}

	@GetMapping(value = "api/payout/findAllPayoutMerchant")
	@ApiOperation(value = "merchantRecharge", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> findAllPayoutMerchant(@RequestParam("uuid") String uuid) throws JsonProcessingException,
			ValidationExceptions, ParseException, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(payOutAdmin.findAllPayoutMerchant());
	}

	@PutMapping(value = "api/update/transaction/details/{internalOrderId}")
	@ApiOperation(value = "update transaction details using internal order id.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateTransactionDetailsByInternalOrderId(
			@PathVariable("internalOrderId") String internalOrderId, @RequestParam("uuid") String uuid,
			@RequestBody UpdateTransactionDetailsRequestDto dto) throws JsonProcessingException, ValidationExceptions,
			ParseException, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		payOutAdmin.updateTransactionDetailsByInternalOrderId(internalOrderId,dto);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Transaction Details Updated.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}
	

//DashBord Report

	@GetMapping(value = "api/admin/payout/getByMerchantWisePgWiseSumPayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getByMerchantWisePgWiseSumPayOut(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date,@RequestParam("end_date") String end_date,@RequestParam("status")String status)
			throws UserException, JWTException, ValidationExceptions,SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		//String res = payoutMerchant.getByMerchantWisePgWiseSumPayOut(start_date,end_date,status);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(payoutMerchant.getByMerchantWisePgWiseSumPayOut(start_date,end_date,status));
	}

	@GetMapping(value = "api/admin/payout/getPgTypeAndCountByStatusAndDatePayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getPgTypeAndCountByStatusAndDatePayOut(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date,@RequestParam("end_date") String end_date,@RequestParam("status")String status)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getPgTypeAndCountByStatusAndDatePayOut(start_date,end_date,status);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/payout/getHourandCountStatusAndDatePayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getHourandCountStatusAndDatePayOut(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date,@RequestParam("status")String status)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getHourandCountStatusAndDatePayOut(start_date,status);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/payout/getMinuteandCountByStatusPayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMinuteandCountByStatusPayOut(@RequestParam("uuid") String uuid,
			@RequestParam("status")String status)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getMinuteandCountByStatusPayOut(status);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/payout/getStatusCountPayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getStatusCountPayOut(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date,@RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getStatusCountPayOut(start_date,end_date);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "api/admin/payout/getLastTrxMerchListPayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getLastTrxMerchListPayOut(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getLastTrxMerchListPayOut(start_date);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}
	@GetMapping(value = "api/admin/payout/getStatusAndMinuteWiseCountPayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getStatusAndMinuteWiseCountPayOut(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getStatusAndMinuteWiseCountPayOut();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}
	@GetMapping(value = "api/admin/payout/getHourandStatusWiseCountAndDatePayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getHourandStatusWiseCountAndDatePayOut(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.getHourandStatusWiseCountAndDatePayOut(start_date);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}


	@PostMapping(value = "api/admin/payout/txnWithParametersPayOut")
	@ApiOperation(value = "wallet list", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> txnWithParametersPayOut(@RequestParam("uuid") String uuid,
			@RequestBody() TrxnWithParameter dto)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String res = payoutMerchant.txnWithParametersPayOut(dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	// Neeed to impl one api for trnasfer ticket one admin to another admin
}
