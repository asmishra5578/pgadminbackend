package com.asktech.admin.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.admin.constants.BucketNameConstant;
import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.admin.FileResponseDto;
import com.asktech.admin.dto.admin.TransactionChangeResponceListDto;
import com.asktech.admin.dto.admin.UpdateTransactionDetailsRequestDto;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeRequestDto;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeResponce;
import com.asktech.admin.dto.payout.beneficiary.TransactionChangeResponceList;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.BulkFileUrlData;
import com.asktech.admin.model.TransactionChangeRequest;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.repository.BulkFileUrlDataRepo;
import com.asktech.admin.repository.TransactionChangeRequestRepo;
import com.asktech.admin.repository.TransactionDetailsRepository;

import kong.unirest.Unirest;

@Service
public class BulkFileUploadService implements BucketNameConstant, ErrorValues {

	static Logger logger = LoggerFactory.getLogger(BulkFileUploadService.class);

	@Autowired
	FileUploadManagmentService fileUploadManagmentService;
	@Autowired
	BulkFileUrlDataRepo bulkFileUrlDataRepo;
	@Value("${apiPayoutEndPoint.payoutUrl}")
	String payoutUrl;
	@Value("${apiPayoutEndPoint.payoutBaseUrl}")
	String payoutBaseUrl;
	@Autowired
	TransactionChangeRequestRepo transactionChangeRequestRepo;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;

	public String uploadFile(UserAdminDetails userAdminDetails, MultipartFile file) throws IOException {
		String fileUrl = fileUploadManagmentService.fileUpload(file, TXN_STATUS_BULK_UPLOAD);
		return fileUrl;
	}

	public FileResponseDto txnStatusUpdateBulkFileUpload(MultipartFile file) throws IOException {
		FileResponseDto fileResponseDto = fileUploadManagmentService.txnStatusFileUpload(file, TXN_STATUS_BULK_UPLOAD);
		return fileResponseDto;
	}

	public BulkFileUrlData saveFile(FileResponseDto url, UserAdminDetails userAdminDetails, String fileType) {
		BulkFileUrlData blkFileUrlData = new BulkFileUrlData();
		blkFileUrlData.setCreatedByUuid(userAdminDetails.getUuid());
		blkFileUrlData.setUrl(url.getFileUrl());
		blkFileUrlData.setFileName(url.getFileName());
		blkFileUrlData.setParsingStatus("false");
		blkFileUrlData.setFileType(fileType);
		return bulkFileUrlDataRepo.save(blkFileUrlData);
	}

	@SuppressWarnings({ "deprecation", "resource" })
	public TransactionChangeRequestDto bulkRegistrationParser(File filedata, String fileExtension, BulkFileUrlData data,
			WebRequest request) throws InvalidFormatException, IOException, ValidationExceptions {
		TransactionChangeRequestDto transactionChangeRequestDto = new TransactionChangeRequestDto();
		List<UpdateTransactionDetailsRequestDto> rdto = new ArrayList<>();
		if (fileExtension.equalsIgnoreCase(".xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook(filedata);
			XSSFSheet worksheet = workbook.getSheetAt(0);
			int i = 1;
			while (i <= worksheet.getLastRowNum()) {
				UpdateTransactionDetailsRequestDto dataSource = new UpdateTransactionDetailsRequestDto();
				XSSFRow row = worksheet.getRow(i++);
				if (row.getCell(0) == null) {
					dataSource.setInternalOrderId("");
				} else {
					if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setInternalOrderId(NumberToTextConverter.toText(row.getCell(0).getNumericCellValue())
								.replaceAll("\\s+", ""));
					} else {
						dataSource.setInternalOrderId(row.getCell(0).getStringCellValue().replaceAll("\\s+", ""));
					}
				}
				if (row.getCell(1) == null) {
					dataSource.setUtrid("");
				} else {
					if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setUtrid(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue())
								.replaceAll("\\s+", ""));
					} else {
						dataSource.setUtrid(row.getCell(1).getStringCellValue().replaceAll("\\s+", ""));
					}
				}
				if (row.getCell(2) == null) {
					dataSource.setReferenceId("");
				} else {
					if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setReferenceId(NumberToTextConverter.toText(row.getCell(2).getNumericCellValue())
								.toLowerCase().replaceAll("\\s+", ""));
					} else {
						dataSource.setReferenceId(
								row.getCell(2).getStringCellValue().toLowerCase().replaceAll("\\s+", ""));
					}
				}
				if (row.getCell(3) == null) {
					dataSource.setTransactionStatus("");
				} else {
					if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setTransactionStatus(NumberToTextConverter
								.toText(row.getCell(3).getNumericCellValue()).replaceAll("\\s+", ""));
					} else {
						dataSource.setTransactionStatus(row.getCell(3).getStringCellValue().replaceAll("\\s+", ""));
					}
				}
				if (row.getCell(4) == null) {
					dataSource.setTransactionMessage("");
				} else {
					if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setTransactionMessage(NumberToTextConverter
								.toText(row.getCell(4).getNumericCellValue()).replaceAll("\\s+", ""));
					} else {
						dataSource.setTransactionMessage(row.getCell(4).getStringCellValue().replaceAll("\\s+", ""));
					}
				}

				if (row.getCell(5) == null) {
					dataSource.setComment("");
				} else {
					if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setComment(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue())
								.replaceAll("\\s+", ""));
					} else {
						dataSource.setComment(row.getCell(5).getStringCellValue().replaceAll("\\s+", ""));
					}
				}

				if (row.getCell(6) == null) {
					dataSource.setCallBackFlag("");
				} else {
					if (row.getCell(6).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						dataSource.setCallBackFlag(NumberToTextConverter.toText(row.getCell(6).getNumericCellValue())
								.replaceAll("\\s+", ""));
					} else {
						dataSource.setCallBackFlag(row.getCell(6).getStringCellValue().replaceAll("\\s+", ""));
					}
				}
				rdto.add(dataSource);
			}
		}
		if (fileExtension.equalsIgnoreCase(".csv")) {
			rdto = Files.lines(filedata.toPath()).skip(1).map(BulkFileUploadService::getTxnDetails)
					.collect(Collectors.toList());
		}
		filedata.delete();
		if (rdto.isEmpty()) {
			logger.info("File parsing error==> ");
			throw new ValidationExceptions(FILE_PARSING_ERROR, FormValidationExceptionEnums.FILE_PARSING_ERROR);
		}
		transactionChangeRequestDto.setUpdateDataDto(rdto);
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
		return transactionChangeRequestDto;
	}

	private static UpdateTransactionDetailsRequestDto getTxnDetails(String line) {
		String[] fields = line.split(",", -1);
		UpdateTransactionDetailsRequestDto resD = new UpdateTransactionDetailsRequestDto();
		if (fields[0] == null || fields[0].length() == 0) {
			resD.setInternalOrderId("");
		} else {
			resD.setInternalOrderId(fields[0]);
		}
		if (fields[1] == null || fields[1].length() == 0) {
			resD.setUtrid("");
		} else {
			resD.setUtrid(fields[1]);
		}
		if (fields[2] == null || fields[2].length() == 0) {
			resD.setReferenceId("");
		} else {
			resD.setReferenceId(fields[2]);
		}
		if (fields[3] == null || fields[3].length() == 0) {
			resD.setTransactionStatus("");
		} else {
			resD.setTransactionStatus(fields[3]);
		}
		if (fields[4] == null || fields[4].length() == 0) {
			resD.setTransactionMessage("");
		} else {
			resD.setTransactionMessage(fields[4]);
		}
		if (fields[5] == null || fields[5].length() == 0) {
			resD.setComment("");
		} else {
			resD.setComment(fields[5]);
		}
		if (fields[6] == null || fields[6].length() == 0) {
			resD.setCallBackFlag("");
		} else {
			resD.setCallBackFlag(fields[6]);
		}
		return resD;
	}

	public void callPayoutRestTempletToUpdateTxnStatus(TransactionChangeRequestDto transactionChangeRequestDto, BulkFileUrlData data) {
		Unirest.put(payoutBaseUrl + "controller/updateBulkFileTransactionStatus")
				.header("Content-Type", "application/json").body(transactionChangeRequestDto).asString();
		data.setParsingStatus("true");
		bulkFileUrlDataRepo.save(data);
	}

	public boolean checkParsingStatus(String fileName) throws ValidationExceptions {
		BulkFileUrlData data = bulkFileUrlDataRepo.findByfileName(fileName);
		if (data == null) {
			throw new ValidationExceptions(FILE_NAME_NOT_FOUND, FormValidationExceptionEnums.FILE_NAME_NOT_FOUND);
		}
		if (data.getParsingStatus().equals("true"))
			return true;
		return false;
	}

	public List<BulkFileUrlData> getAllTxnUpdateBulkFile(String fileType) throws ValidationExceptions {
		if (!(fileType.equals("PAYOUT") || fileType.equals("PAYIN"))) {
			throw new ValidationExceptions(FILE_TYPE_NOT_MATCHED, FormValidationExceptionEnums.FILE_NAME_NOT_FOUND);
		}
		List<BulkFileUrlData> data = bulkFileUrlDataRepo.findByfileType(fileType);
		return data;
	}

	int scount = 0;
	int fcount = 0;

	@SuppressWarnings("deprecation")
	@Async
	public void callPayinServiceToUpdateTxnStatus(TransactionChangeRequestDto transactionChangeRequestDto, BulkFileUrlData data) {
		TransactionChangeResponceListDto resDto = new TransactionChangeResponceListDto();
		List<UpdateTransactionDetailsRequestDto> updateSuccessDataDto = new ArrayList<>();
		List<UpdateTransactionDetailsRequestDto> updateFailedDataDto = new ArrayList<>();
		List<UpdateTransactionDetailsRequestDto> updatePendingDataDto = new ArrayList<>();
		List<UpdateTransactionDetailsRequestDto> updateRefundDataDto = new ArrayList<>();
		List<String> successOrderIds = new ArrayList<>();
		List<String> failedOrderIds = new ArrayList<>();
		List<String> pendingOrderIds = new ArrayList<>();
		List<String> refundOrderIds = new ArrayList<>();
		transactionChangeRequestDto.getUpdateDataDto().forEach(o -> {
			if (o.getTransactionStatus().equals("SUCCESS")) {
				updateSuccessDataDto.add(o);
				successOrderIds.add(o.getInternalOrderId());
			}
			if (o.getTransactionStatus().equals("FAILURE")) {
				updateFailedDataDto.add(o);
				failedOrderIds.add(o.getInternalOrderId());
			}
			if (o.getTransactionStatus().equals("PENDING")) {
				updatePendingDataDto.add(o);
				pendingOrderIds.add(o.getInternalOrderId());
			}
			if (o.getTransactionStatus().equals("REFUND")) {
				updateRefundDataDto.add(o);
				refundOrderIds.add(o.getInternalOrderId());
			}
		});
		if (!updateSuccessDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updateSuccessDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", successOrderIds));
			transactionChangeRequest.setStatus(updateSuccessDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updateSuccessDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updateSuccessDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(successOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(successOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setSuccessDataTransactionChangeResponce(transactionChangeResponce);
		}
		if (!updateFailedDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updateFailedDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", failedOrderIds));
			transactionChangeRequest.setStatus(updateFailedDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updateFailedDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updateFailedDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(failedOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(failedOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setFailedDataTransactionChangeResponce(transactionChangeResponce);
		}
		if (!updatePendingDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updatePendingDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", pendingOrderIds));
			transactionChangeRequest.setStatus(updatePendingDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updatePendingDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updatePendingDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(pendingOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(pendingOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setPendingDataTransactionChangeResponce(transactionChangeResponce);
		}
		if (!updateRefundDataDto.isEmpty()) {
			TransactionChangeRequest transactionChangeRequest = new TransactionChangeRequest();
			transactionChangeRequest.setComment(updateRefundDataDto.get(0).getComment());
			transactionChangeRequest.setOrderIds(String.join(",", refundOrderIds));
			transactionChangeRequest.setStatus(updateRefundDataDto.get(0).getTransactionStatus());
			transactionChangeRequest.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			List<TransactionChangeResponceList> transactionChangeResponceList = new ArrayList<>();
			scount = 0;
			fcount = 0;
			updateRefundDataDto.forEach(o -> {
				if (o.getInternalOrderId().trim().length() > 5) {
					TransactionDetails transactionDetails = transactionDetailsRepository
							.getOneByOrderID(o.getInternalOrderId());
					TransactionChangeResponceList transactionChangeResponceList2 = new TransactionChangeResponceList();
					if (transactionDetails == null) {
						transactionChangeResponceList2
								.setComment("Status update is fail because Internal OrderId is not found in database");
						transactionChangeResponceList2.setOrderIds(o.getInternalOrderId());
						transactionChangeResponceList2.setStatus("FAILURE");
						transactionChangeResponceList.add(transactionChangeResponceList2);
						fcount++;
					} else {
						if (!StringUtils.isEmpty(o.getTransactionStatus())) {
							transactionDetails.setStatus(o.getTransactionStatus());
						}
						if (!StringUtils.isEmpty(o.getTransactionMessage())) {
							transactionDetails.setTxtMsg(o.getTransactionMessage());
						}
						if(!StringUtils.isEmpty(o.getCallBackFlag())){
							transactionDetails.setCallBackFlag(o.getCallBackFlag());
						}
						if (!StringUtils.isEmpty(o.getComment())) {
							transactionDetails.setErrorMsg(o.getComment());
						}
						transactionDetails = transactionDetailsRepository.save(transactionDetails);
						transactionChangeResponceList2.setComment(updateRefundDataDto.get(0).getComment());
						transactionChangeResponceList2.setOrderIds(transactionDetails.getOrderID());
						transactionChangeResponceList2.setStatus(transactionDetails.getStatus());
						transactionChangeResponceList.add(transactionChangeResponceList2);
						scount++;
					}
				}
			});
			transactionChangeRequest.setSucessCount(scount);
			transactionChangeRequest.setFailCount(fcount);
			transactionChangeRequest.setCount(refundOrderIds.size());
			transactionChangeRequest = transactionChangeRequestRepo.save(transactionChangeRequest);
			TransactionChangeResponce transactionChangeResponce = new TransactionChangeResponce();
			transactionChangeResponce.setComment(transactionChangeRequest.getComment());
			transactionChangeResponce.setTotalCount(refundOrderIds.size());
			transactionChangeResponce.setOrderIds(transactionChangeRequest.getOrderIds());
			transactionChangeResponce.setStatus(transactionChangeRequest.getStatus());
			transactionChangeResponce.setUuid(transactionChangeRequestDto.getUuid());
			transactionChangeResponce.setFailCount(fcount);
			transactionChangeResponce.setSucessCount(scount);
			transactionChangeResponce.setTransactionChangeResponceList(transactionChangeResponceList);
			resDto.setRefundDataTransactionChangeResponce(transactionChangeResponce);
		}
		data.setParsingStatus("true");
		bulkFileUrlDataRepo.save(data);
	}

}
