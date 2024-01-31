package com.asktech.admin.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.admin.AllMerchantDetails;
import com.asktech.admin.dto.admin.AllPgDetailsResponse;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.service.PGAdminReports;
import com.asktech.admin.service.PGGatewayAdminService;
import com.asktech.admin.service.PaymentMerchantService;
import com.asktech.admin.util.JwtUserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class PGAdminReportsController implements ErrorValues {

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);

	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	@Autowired
	private JwtUserValidator jwtUserValidator;


	@GetMapping(value = "api/admin/dateWiseTxnWithParameters")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> dateWiseTxnWithParameters(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchant_id", required = false) String merchant_id,
			@RequestParam(value = "payment_option", required = false) String payment_option,
			@RequestParam(value = "pgType", required = false) String pgType,
			@RequestParam(value = "merchant_order_id", required = false) String merchant_order_id,
			@RequestParam(value = "merchant_order_ids", required = false) String merchant_order_ids,
			@RequestParam(value = "trId", required = false) String trId,
			@RequestParam(value = "pg_id", required = false) String pg_id,
			@RequestParam(value = "oder_id", required = false) String oder_id,
			@RequestParam(value = "txt_msg", required = false) String txt_msg,
			@RequestParam(value = "start_date", required = false) String start_date,
			@RequestParam(value = "end_date", required = false) String end_date,
			@RequestParam(value = "end_date", required = false)	String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		List<TransactionDetails> list = pgGatewayAdminService.txnWithParameters(merchant_id, payment_option, pgType,
				merchant_order_id,merchant_order_ids, trId, pg_id, start_date, end_date,oder_id,txt_msg,status);		
		System.out.println(list);
		if (list == null) {
			list = new ArrayList<TransactionDetails>();
		}

		if (list.isEmpty()) {
			throw new ValidationExceptions(TICKET_NOT_FOUND,
					FormValidationExceptionEnums.REQUIRED_INFORMATION_NOT_FOUND);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails", list);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getMerchantByNameAndId")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantByNameAndIdDate(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "merchantName", required = false) String merchantName,
			@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getPgDetailsByMerchantIdAndMerchantNameDate(merchantId,
				merchantName, start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getPgDetailByPGNameAndPgId")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getPGDetailsByPGNameAndPgId(@RequestParam("uuid") String uuid,
			@RequestParam(value = "pgName", required = false) String pgName,
			@RequestParam(value = "pguuid", required = false) String pgUuid,
			@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.getPgDetailsByPGNameAndPgId(pgName, pgUuid, start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getPgDetailAndServices")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getPGDetailsAndServices(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getAllPgDetails());
	}

	@GetMapping(value = "/api/admin/getPgDetailAndServicesDateWise")
	@ApiOperation(value = "reports", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getPGDetailsAndServicesDateWise(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		List<AllPgDetailsResponse> list = pgGatewayAdminService.getAllPgDetailsDateWise(start_date, end_date);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetails", list);
		return ResponseEntity.ok().body(sdto);
	}

	// api/admin/allMerchantDetailsReport
	@GetMapping(value = "/api/admin/allMerchantDetailsReport")
	@ApiOperation(value = "all MerchantDetailsReport ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllMerchantDetailsReport(@RequestParam("uuid") String uuid)
			throws JsonProcessingException {
				logger.info("inside controller :::  /api/admin/allMerchantDetailsReport");
		// @SuppressWarnings("unused")
		// UserAdminDetails userAdminDetails =
		// jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getAllMerchantDetailsReport());
	}

	@GetMapping(value = "/api/admin/allMerchantDetailsReportDateWise")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantDetailsReportDateWise(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		List<AllMerchantDetails> merchantList = pgGatewayAdminService.getAllMerchantDetailsReportDateWise(start_date,
				end_date);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", merchantList);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "api/admin/merchantList")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		// return
		// ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusList(userAdminDetails));
		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusList());
	}

	@GetMapping(value = "/api/admin/getTotalTransaction")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalTransaction(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalTransaction(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getTotalRefund")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalRefund(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalRefund(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getTotalCancelledTransaction")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalCancelledTransaction(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalCancelledTransaction(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getTotalSettledAndUnsettledAmount")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalSettledAmount(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.totalSettledAndUnsettledAmount(start_date, end_date, status));
	}

	@GetMapping(value = "/api/admin/getTotalUnSettledAmount")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalUnSettledAmount(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalUnSettledAmount(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getTrxTopReport")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTrxTop100(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions
			 {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getTrxTop100());
	}

	@GetMapping(value = "/api/admin/getTotalCardPayment")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalCardPayment(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalCardPaymentAmount(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getTotalPayOptionTransaction")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalPayOptionTransaction(@RequestParam("uuid") String uuid,
			@RequestParam(value = "payment_option", required = false) String payment_option,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		Object paOptTransaction = pgGatewayAdminService.totalPayOptTransaction(payment_option, start_date, end_date);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails", paOptTransaction);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getAllSumByPaymentOption")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllSumByPaymentOption(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		Object paOptTransaction = pgGatewayAdminService.getAllSumByPaymentOption(start_date, end_date);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails", paOptTransaction);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getTotalHitTransaction")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalHitTransaction(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalhitTransaction(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getTotalCaptured")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalCaptured(@RequestParam("uuid") String uuid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalCapturedTransaction(start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getNumberOfTxnWithStatus")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getNumberOfTxnStatus(@RequestParam("uuid") String uuid,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		String val = pgGatewayAdminService.totalTxnWithStatus(status);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails", val);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getTotalNoOfMerchants")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalNoOfMerchants(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.totalNumberOfMerchants());
	}

	@GetMapping(value = "/api/admin/getTotalPayModeTransaction")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getTotalPayModeTransaction(@RequestParam("uuid") String uuid,
			@RequestParam(value = "payment_mode", required = false) String payment_mode,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		Object val = pgGatewayAdminService.totalPayModeTransaction(payment_mode, start_date, end_date);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetails", val);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/admin/getAllTopTxnByMerchantId")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllTopTxnByMerchantId(@RequestParam("uuid") String uuid,
			@RequestParam(value = "merchantid") String merchantid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getTopTxnByMerchantId(merchantid));
	}

	@GetMapping(value = "/api/admin/getComplaint")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getComplaintWithStatus(@RequestParam("uuid") String uuid,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "complaintid", required = false) String complaintid,
			@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getComplaint(status, complaintid, start_date, end_date));
	}

	@GetMapping(value = "/api/admin/getAllTopMerchantTxn")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllTopMerchantTxn(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException,
			JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getTopMerchantTxn());
	}

	// @GetMapping(value = "/api/admin/merchantReport")
	// @ApiOperation(value = "Date wise transaction ", authorizations = {
	// @Authorization(value = "apiKey") })
	// public ResponseEntity<?> merchantReport(@RequestParam("uuid") String uuid,
	// @RequestParam("start_date") String start_date, @RequestParam("end_date")
	// String end_date)
	// throws UserException, JWTException, SessionExpiredException,
	// ValidationExceptions, ParseException, JsonProcessingException {

	// @SuppressWarnings("unused")
	// UserAdminDetails userAdminDetails =
	// jwtUserValidator.validatebyJwtAdminDetails(uuid);

	// SuccessResponseDto sdto = new SuccessResponseDto();
	// sdto.getMsg().add("Request Processed Successfully !");
	// sdto.setSuccessCode(SuccessCode.API_SUCCESS);
	// sdto.getExtraData().put("Details",pgGatewayAdminService.txnreport(start_date,
	// end_date));
	// return ResponseEntity.ok().body(sdto);
	// }

	@GetMapping(value = "/api/admin/findByStatusAndDateAndMerchantID")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantReport(@RequestParam("start_date") String start_date,
			@RequestParam("status") String status) {
		return ResponseEntity.ok().body(pgGatewayAdminService.findByStatusAndDateAndMerchantID(start_date, status));
	}

	//////////////////////////////////////////// DASHBOARD APIS
	//////////////////////////////////////////// /////////////////////////////////////////////

	// PAYIN
	@Autowired
	PGAdminReports pGAdminReports;

	@GetMapping(value = "/api/admin/getByMerchantWisePgWiseSum")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantWisePgWiseSum(@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date, @RequestParam("status") String status) {
		return ResponseEntity.ok().body(pGAdminReports.getByMerchantWisePgWiseSum(start_date, end_date, status));
	}
	@GetMapping(value = "/api/admin/getPgTypeAndCountByStatusAndDate")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getPgTypeAndCountByStatusAndDate(@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date, @RequestParam("status") String status) {
		return ResponseEntity.ok().body(pGAdminReports.getPgTypeAndCountByStatusAndDate(start_date, end_date, status));
	}
	@GetMapping(value = "/api/admin/getHourandCountStatusAndDate")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getHourandCountStatusAndDate(@RequestParam("start_date") String start_date,
    @RequestParam("status") String status) {
		return ResponseEntity.ok().body(pGAdminReports.getHourandCountStatusAndDate(start_date, status));
	}
	@GetMapping(value = "/api/admin/getMinuteandCountByStatus")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMinuteandCountByStatus(@RequestParam("status") String status) {
		return ResponseEntity.ok().body(pGAdminReports.getMinuteandCountByStatus( status));
	}

	@GetMapping(value = "/api/admin/getStatusCount")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getStatusCount(@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date) {
		return ResponseEntity.ok().body(pGAdminReports.getStatusCount(start_date, end_date));
	}
	@GetMapping(value = "/api/admin/getLastTrxMerchList")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getLastTrxMerchList(@RequestParam("start_date") String start_date) {
		return ResponseEntity.ok().body(pGAdminReports.getLastTrxMerchList(start_date));
	}

	@GetMapping(value = "/api/admin/getStatusAndMinuteWiseCount")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getStatusAndMinuteWiseCount() throws JsonProcessingException {
		return ResponseEntity.ok().body(pGAdminReports.getStatusAndMinuteWiseCount());
	}
	@GetMapping(value = "/api/admin/getHourandStatusWiseCountAndDate")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getHourandStatusWiseCountAndDate(@RequestParam("start_date") String start_date) throws JsonProcessingException {
		return ResponseEntity.ok().body(pGAdminReports.getHourandStatusWiseCountAndDate(start_date));
	}

	@GetMapping(value = "/api/admin/getPGWiseMerchantList")
	@ApiOperation(value = "get all the merchant associated with a pg", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantListFromPgId(@RequestParam("uuid") String uuid,@RequestParam("pgId") String pgId ) throws JsonProcessingException, UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		return ResponseEntity.ok().body(pGAdminReports.getAllMerchantListFromPgId(pgId, uuid));
		// pGAdminReports.getHourandStatusWiseCountAndDate(start_date)
			
	}

	@GetMapping(value = "/api/admin/getHourandStatusWiseCountAndDateAndSum")
	@ApiOperation(value = "Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getHourandStatusWiseCountAndDateAndSum() throws JsonProcessingException {
		return ResponseEntity.ok().body(pGAdminReports.getHourandStatusWiseCountAndDateAndSum());
	}
	
}
