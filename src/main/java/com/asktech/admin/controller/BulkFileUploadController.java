package com.asktech.admin.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.admin.dto.admin.FileResponseDto;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeRequestDto;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.BulkFileUrlData;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.service.BulkFileUploadService;
import com.asktech.admin.util.JwtUserValidator;
import com.asktech.admin.util.bulkUpload.FileValidations;

import io.swagger.annotations.ApiOperation;

@RestController
public class BulkFileUploadController {

	@Autowired
	JwtUserValidator jwtUserValidator;
	@Autowired
	BulkFileUploadService bulkFileUploadService;

	static Logger logger = LoggerFactory.getLogger(BulkFileUploadController.class);

	@PostMapping("api/file/upload")
	@ApiOperation(value = "Upload file on aws s3 bucket..")
	public ResponseEntity<?> fileUpload(@RequestPart("uuid") String uuid, @RequestPart("file") MultipartFile file)
			throws ValidationExceptions, UserException, JWTException, SessionExpiredException, IOException {
		logger.info("Inside Controller and validate file extension..");
		FileValidations.fileUploadValidation(file);
		logger.info("User Auth call............ Request: " + uuid);
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("service call........ Request: " + uuid + "file: " + file);
		String fileUrl = bulkFileUploadService.uploadFile(userAdminDetails, file);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("File uploaded");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("FileData", fileUrl);
		return ResponseEntity.status(HttpStatus.OK).body(sdto);
	}

	@PostMapping(value = "/api/update/txnStatus/file/upload")
	@ApiOperation(value = "Bulk file upload for update transaction status")
	ResponseEntity<Object> bulkFileUpload(@RequestParam("uuid") String uuid, @RequestPart("file") MultipartFile file,
			WebRequest request) throws Exception {
		SuccessResponseDto successResponseDto = new SuccessResponseDto();
		logger.info("Inside Controller and validate file extension..");
		FileValidations.bulkFileUploadValidation(file);
		logger.info("User Auth call............ Request: " + uuid);
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("service call........ Request: " + uuid + "file: " + file);
		FileValidations.checkFileFormate(file);
		FileResponseDto url = bulkFileUploadService.txnStatusUpdateBulkFileUpload(file);
		logger.info("File url==> " + url);
		BulkFileUrlData data = bulkFileUploadService.saveFile(url, userAdminDetails, "PAYOUT");
		logger.info("Bulk file data==> " + data.toString());
		TransactionChangeRequestDto transactionChangeRequestDto = bulkFileUploadService.bulkRegistrationParser(
				url.getFileData(), FileValidations.getFileExtension(file.getOriginalFilename()), data, request);
		transactionChangeRequestDto.setUuid(uuid);
		bulkFileUploadService.callPayoutRestTempletToUpdateTxnStatus(transactionChangeRequestDto,data);
		successResponseDto.getMsg().add("File uploaded successfully and parse internally");
		successResponseDto.setSuccessCode(SuccessCode.API_SUCCESS);
		logger.info("File uploaded successfully and parse internally==> ");
		return ResponseEntity.ok().body(successResponseDto);
	}
	
	@PostMapping(value = "/api/payin/update/txnStatus/file/upload")
	@ApiOperation(value = "Payin Bulk file upload for update transaction status")
	ResponseEntity<Object> payinBulkFileUpload(@RequestParam("uuid") String uuid, @RequestPart("file") MultipartFile file,
			WebRequest request) throws Exception {
		SuccessResponseDto successResponseDto = new SuccessResponseDto();
		logger.info("Inside Controller and validate file extension..");
		FileValidations.bulkFileUploadValidation(file);
		logger.info("User Auth call............ Request: " + uuid);
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		logger.info("service call........ Request: " + uuid + "file: " + file);
		FileValidations.checkFileFormate(file);
		FileResponseDto url = bulkFileUploadService.txnStatusUpdateBulkFileUpload(file);
		logger.info("File url==> " + url);
		BulkFileUrlData data = bulkFileUploadService.saveFile(url, userAdminDetails, "PAYIN");
		logger.info("Bulk file data==> " + data.toString());
		TransactionChangeRequestDto transactionChangeRequestDto = bulkFileUploadService.bulkRegistrationParser(
				url.getFileData(), FileValidations.getFileExtension(file.getOriginalFilename()), data, request);
		transactionChangeRequestDto.setUuid(uuid);
		bulkFileUploadService.callPayinServiceToUpdateTxnStatus(transactionChangeRequestDto,data);
		successResponseDto.getMsg().add("File uploaded successfully and parse internally");
		successResponseDto.setSuccessCode(SuccessCode.API_SUCCESS);
		logger.info("File uploaded successfully and parse internally==> ");
		return ResponseEntity.ok().body(successResponseDto);
	}

	@GetMapping("check/txn/file/parsing/staus")
	@ApiOperation(value = "check parsing status, bulk file is parsed or not.")
	public ResponseEntity<?> checkParsingStatus(@RequestParam("fileName") String fileName)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (bulkFileUploadService.checkParsingStatus(fileName))
			sdto.getExtraData().put("status", true);
		else
			sdto.getExtraData().put("status", false);
		sdto.getMsg().add("Parsing Status");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("api/get/all/txn/update/files")
	@ApiOperation(value = "check parsing status, bulk file is parsed or not.")
	public ResponseEntity<?> getAllTxnUpdateBulkFile(@RequestParam("uuid") String uuid,
			@RequestParam("fileType") String fileType)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		SuccessResponseDto sdto = new SuccessResponseDto();
		logger.info("User Auth call............ Request: " + uuid);
		jwtUserValidator.validatebyJwtAdminDetails(uuid);
		List<BulkFileUrlData> data = bulkFileUploadService.getAllTxnUpdateBulkFile(fileType);
		sdto.getMsg().add("Parsing Status");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("FilesList", data);
		return ResponseEntity.ok().body(sdto);
	}

}
