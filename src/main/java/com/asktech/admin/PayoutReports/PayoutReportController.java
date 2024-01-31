package com.asktech.admin.PayoutReports;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.encryptionsdk.exception.ParseException;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.reports.dto.ReportRequestDTO;



@RestController
public class PayoutReportController {
    @Autowired
	PayoutReportService reportService;


	@PostMapping(value = "api/payout/report/transactionReport")
	
	public ResponseEntity<?> reportTransaction(@RequestParam("uuid") String uuid,
 			@RequestBody ReportRequestDTO reportRequestDTO) throws ParseException, IOException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ValidationExceptions, java.text.ParseException {

		// jwtUserValidator.validatebyJwtAdminDetails(uuid);
		String fileName = reportService.transactionReportService(reportRequestDTO, uuid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("file Download Link", fileName);
		return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/json").body(sdto);
	}

	@RequestMapping("api/payout/report/download")
	public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("fileName") String fileName)
			throws IOException, ValidationExceptions {

		File file = new File("/home/ubuntu/reports/" + fileName);
		// jwtUserValidator.validatebyJwtAdminDetails(uuid);
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
	
// 	@RequestMapping("api/payout/report/download")
// public void pyoutdownloadPdf(HttpServletRequest request, HttpServletResponse response,
// @RequestParam("fileName") String fileName){
	
// }


	@GetMapping(value = "api/report/getAllPayoutReportDetails")
	public ResponseEntity<?> getAllReportDetails(){
		return ResponseEntity.ok().body(reportService.getAllReportDetails());	
	}
	@GetMapping(value = "api/report/getAllPayoutReportTransactionDownloadLinkList")
	public ResponseEntity<?> getAllReportTransactionDownloadLinkList(){
		return ResponseEntity.ok().body(reportService.getAllReportTransactionDownloadLinkList());	
	}
	@GetMapping(value = "api/report/getAllReportTrList")
	public ResponseEntity<?> getAllReportTrList(@RequestParam("reportName")String reportName){
		return ResponseEntity.ok().body(reportService.getAllReportTrList(reportName));	
	}
}
