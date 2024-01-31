package com.asktech.admin.reports.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.reports.dto.ReportRequestDTO;
import com.asktech.admin.reports.dto.TxReportGenerate3MBRequest;
import com.asktech.admin.reports.dto.TxReportGenerate3MBResponse;
import com.asktech.admin.reports.dto.TxReportGenerateFUTDRequest;
import com.asktech.admin.reports.dto.TxReportGenerateFUTDResponse;
import com.asktech.admin.reports.dto.TxReportGenerateMerchantID;
import com.asktech.admin.reports.dto.TxReportGenerateMerchantIDAndOrderID;
import com.asktech.admin.reports.dto.TxReportGeneratePGNameWithDate;
import com.asktech.admin.reports.dto.TxReportGeneratePGWiseRequest;
import com.asktech.admin.reports.dto.TxReportGeneratePGWiseResponse;
import com.asktech.admin.reports.dto.TxReportGenerateTodayRequest;
import com.asktech.admin.reports.dto.TxReportGenerateTodayResponse;
import com.asktech.admin.reports.dto.TxReportMerchantIDAndOrderIDWiseResponse;
import com.asktech.admin.reports.dto.TxReportMerchantIDWiseResponse;
import com.asktech.admin.reports.dto.TxReportPGNameWithDateWiseResponse;
import com.asktech.admin.reports.service.ReportService;
import com.asktech.admin.service.processors.pgConfigNewSS;
import com.asktech.admin.util.JwtUserValidator;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class ReportController implements ErrorValues {

	@Autowired
	JwtUserValidator jwtUserValidator;
	@Autowired
	ReportService reportService;
	
	
	/**@author abhimanyu-kumar
	 * */ 
	
	

	
	@PostMapping(value = "api/admin/reports/merId/transactionReport")
	public ResponseEntity<?> merchantIDWiseTransactionReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGenerateMerchantID txReportGenerateMerchantID) throws UserException, JWTException, SessionExpiredException{
		
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		
		TxReportMerchantIDWiseResponse txReportMerchantIDWiseResponse = reportService.generateMerchantIDWiseTransactionReport(txReportGenerateMerchantID);
		
		return ResponseEntity.ok().body(txReportMerchantIDWiseResponse);
	}
	@PostMapping(value = "api/admin/reports/merIdOrdId/transactionReport")
	public ResponseEntity<?> merchantIDAndOrderIDTransactionReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGenerateMerchantIDAndOrderID txReportGenerateMerchantIDAndOrderID) throws UserException, JWTException, SessionExpiredException{
		
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		TxReportMerchantIDAndOrderIDWiseResponse txReportMerchantIDAndOrderIDWiseResponse = reportService.generateMerchantIDAndOrderIDWiseTransactionReport(txReportGenerateMerchantIDAndOrderID);
		
		
		return ResponseEntity.ok().body(txReportMerchantIDAndOrderIDWiseResponse);
	}
	
	@PostMapping(value = "api/admin/reports/pgAndDate/transactionReport")
	public ResponseEntity<?> pgNameWithDateTransactionReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGeneratePGNameWithDate txReportGeneratePGNameWithDate) throws UserException, JWTException, SessionExpiredException{
		
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		
		TxReportPGNameWithDateWiseResponse txReportPGNameWithDateWiseResponse = reportService.generatePGNameWithDateWiseTransactionReport(txReportGeneratePGNameWithDate);
		
		
		return ResponseEntity.ok().body(txReportPGNameWithDateWiseResponse);
	}
	
	
	
	
	@PostMapping(value = "api/admin/reports/3mb/transactionReport")
	@ApiOperation(value = "Report -- Transaction Report", authorizations = { @Authorization(value = "apiKey") }) 
	public ResponseEntity<?> transaction3mbReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGenerate3MBRequest txReportGenerate3MBRequest) throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		TxReportGenerate3MBResponse txReportGenerate3MBResponse = reportService.transaction3mbReportGeneratation(uuid,txReportGenerate3MBRequest);
		
		return  ResponseEntity.ok().body(txReportGenerate3MBResponse);
	}
	@PostMapping(value = "api/admin/reports/futd/transactionReport")
	@ApiOperation(value = "Report -- Transaction Report", authorizations = { @Authorization(value = "apiKey") }) 
	public ResponseEntity<?> transactionfutdReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGenerateFUTDRequest txReportGenerateFUTDRequest) throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		TxReportGenerateFUTDResponse txReportGenerateFUTDResponse  = reportService.transactionfutdReportGeneratation(uuid,txReportGenerateFUTDRequest);
		
		return  ResponseEntity.ok().body(txReportGenerateFUTDResponse);
	}
	@PostMapping(value = "api/admin/reports/pgnamelike/transactionReport")
	@ApiOperation(value = "Report -- Transaction Report", authorizations = { @Authorization(value = "apiKey") }) 
	public ResponseEntity<?> transactionpgnamelikeReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGeneratePGWiseRequest txReportGeneratePGWiseRequest) throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		TxReportGeneratePGWiseResponse txReportGeneratePGWiseResponse = reportService.transactionpgnamelikeReportGeneratation(uuid,txReportGeneratePGWiseRequest);
		
		return  ResponseEntity.ok().body(txReportGeneratePGWiseResponse);
	}
	
	@PostMapping(value = "api/admin/reports/today/transactionReport")
	@ApiOperation(value = "Report -- Transaction Report", authorizations = { @Authorization(value = "apiKey") }) 
	public ResponseEntity<?> transactiontodayReportGeneratation(@RequestParam("uuid") String uuid, @RequestBody TxReportGenerateTodayRequest txReportGenerateTodayRequest) throws UserException, JWTException, SessionExpiredException {
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		TxReportGenerateTodayResponse txReportGenerateTodayResponse = reportService.transactiontodayReportGeneratation(uuid,txReportGenerateTodayRequest);
		
		return  ResponseEntity.ok().body(txReportGenerateTodayResponse);
	}
	@RequestMapping("/download/jasperreports/{fileName}")
	public void downloadJasperReports(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String fileName,
			@RequestParam("uuid") String uuid) throws IOException, ValidationExceptions, UserException, JWTException, SessionExpiredException {

		File file = reportService.downloadJasperReportsResource(fileName);
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		// get the mimetype
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			// unknown mimetype so set the mimetype to application/octet-stream
			mimeType = "application/octet-stream";
		}

		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());
		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(inputStream, response.getOutputStream());

	}
	
	
/****/
	
	
	@PostMapping(value = "api/admin/report/transactionReport")
	@ApiOperation(value = "Report -- Transaction Report", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> reportTransaction(@RequestParam("uuid") String uuid,
			@RequestBody ReportRequestDTO reportRequestDTO) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, ParseException, IOException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		
		String fileName = reportService.transactionReportService(reportRequestDTO, uuid);
		
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("file Download Link", fileName);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}
	

	@GetMapping(value = "report/getAllReportTrList/{reportName}")
	public ResponseEntity<?> getAllReportTrList(@PathVariable("reportName") String reportName){

		return ResponseEntity.ok().body(reportService.getAllReportTrList(reportName));	
	}

	@GetMapping(value = "report/getAllReportByParam3AndName")
	public ResponseEntity<?> getAllReportByParam3AndName(@RequestParam("reportName") String reportName,@RequestParam("param3") String param3){
		return ResponseEntity.ok().body(reportService.getAllReportByParam3AndName(reportName,param3));	
	}
	@GetMapping(value = "report/getAllPayinReportDetails")
	public ResponseEntity<?> getAllReportDetail(){
		return ResponseEntity.ok().body(reportService.getAllReportDetails());	
	}
	@RequestMapping("/download/{fileName}")
	public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String fileName,
			@RequestParam("uuid") String uuid) throws IOException, ValidationExceptions, UserException, JWTException, SessionExpiredException {

		File file = reportService.fileDownloadUtility(fileName);
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		// get the mimetype
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			// unknown mimetype so set the mimetype to application/octet-stream
			mimeType = "application/octet-stream";
		}

		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());
		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(inputStream, response.getOutputStream());

	}
	@Autowired
	pgConfigNewSS pgConfigNewSS1;

	@GetMapping(value = "getoneTest")
	public ResponseEntity<?> getoneTest(@RequestParam String piuuid){
		return ResponseEntity.ok().body(pgConfigNewSS1.getoneTest(piuuid));	
	}

}
