package com.asktech.admin.reports.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.admin.PayoutReports.dto.ReportDownloadTrx;
import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.reports.customInterface.DataRepository;
import com.asktech.admin.reports.dto.ReportData;
import com.asktech.admin.reports.dto.ReportRequestDTO;
import com.asktech.admin.reports.dto.ReportTransactionSuccessRequest;
import com.asktech.admin.reports.dto.TransactionReportRequest;
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
import com.asktech.admin.reports.enums.ReportStatus;
import com.asktech.admin.reports.enums.ReportType;
import com.asktech.admin.reports.model.JasperReportTransactionDetails;
import com.asktech.admin.reports.model.ReportMaster;
import com.asktech.admin.reports.model.ReportTransactionDetails;
import com.asktech.admin.reports.repository.ReportMasterRepository;
import com.asktech.admin.reports.repository.ReportTransactionDetailsRepository;
import com.asktech.admin.reports.util.CSVExporterWithFileCreate;
import com.asktech.admin.reports.util.ExcelExporterWithFileCreate;
import com.asktech.admin.reports.util.GeneralUtils;
import com.asktech.admin.repository.JasperReportTransactionDetailsRepository;
import com.asktech.admin.repository.TransactionDetailsRepository;
import com.asktech.admin.util.Utility;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional
public class ReportService implements ErrorValues {

	static Logger logger = LoggerFactory.getLogger(ReportService.class);

	@Value("${reportArgument.reportPath}")
	private String reportFolderPath;
	@Value("${reportArgument.reportFileExpiry}")
	private int reportFileExpiry;
	@Value("${apiReportEndPoint}")
	private String apiReportEndPoint;
	
	@Value("${apiEndPoint}")
    private String apiEndPoint;
	
	
	@Autowired
	ReportMasterRepository reportMasterRepository;
	@Autowired
	ExcelExporterWithFileCreate excelExporterWithFileCreate;
	@Autowired
	CSVExporterWithFileCreate cSVExporterWithFileCreate;
	@Autowired
	DataRepository dataRepository;
	@Autowired
	ReportTransactionDetailsRepository reportTransactionDetailsRepository;
	@Autowired
	private TransactionDetailsRepository transactionDetailsRepository;
	
	@Autowired
	private JasperReportTransactionDetailsRepository jasperReportTransactionDetailsRepository;
	
	
	
	private String sql_query = "";
	private String reportFileName = "";

	public String transactionReportService(ReportRequestDTO reportRequestDTO, String uuid)
			throws ValidationExceptions, ParseException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		
		// check for report file name is 
		ReportMaster reportMaster = null;
		if(reportRequestDTO.getReportName().isEmpty() && reportRequestDTO.getReportName() == null) {
			throw new ValidationExceptions(REPORT_NAME_EMPTY_NULL, FormValidationExceptionEnums.REPORT_NAME_EMPTY_NULL);
		}else if(reportRequestDTO.getReportParam1().isEmpty() && reportRequestDTO.getReportParam1() == null ){
			
			throw new ValidationExceptions(START_DATE_ERROR, FormValidationExceptionEnums.START_DATE_ERROR);
		}else if(reportRequestDTO.getReportParam2().isEmpty() && reportRequestDTO.getReportParam2() == null) {
			throw new ValidationExceptions(END_DATE_ERROR, FormValidationExceptionEnums.END_DATE_ERROR);
			
		}else if(!Utility.validateJavaDateFormat(reportRequestDTO.getReportParam1())){
			throw new ValidationExceptions(DATE_FORMAT, FormValidationExceptionEnums.DATE_FORMAT);
		}else if(!Utility.validateJavaDateFormat(reportRequestDTO.getReportParam2())){
			throw new ValidationExceptions(DATE_FORMAT, FormValidationExceptionEnums.DATE_FORMAT);
		}
		
		reportMaster = reportMasterRepository.findByReportName(reportRequestDTO.getReportName());

		Object obj = reportRequestDTO;
		if(reportMaster != null) {
			sql_query = sqlBuilder(obj, reportMaster);
		}else {
			throw new ValidationExceptions(REPORT_NOT_FOUND, FormValidationExceptionEnums.REPORT_NOT_FOUND); 
		}
		

		return reportExecutionProcess(reportMaster, uuid, sql_query,reportRequestDTO);
	}

	
	public String transactionReportSuccessService(ReportTransactionSuccessRequest reportTransactionSuccessRequest,
			String uuid) throws ValidationExceptions, ParseException, IOException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		ReportMaster reportMaster = reportMasterRepository.findByReportName(reportTransactionSuccessRequest.getReportName());

		if (reportMaster == null) {
			throw new ValidationExceptions(REPORT_NOT_FOUND, FormValidationExceptionEnums.REPORT_NOT_FOUND);
		}

		Object obj = reportTransactionSuccessRequest;

		sql_query = sqlBuilder(obj, reportMaster);

		return reportExecutionProcess(reportMaster, uuid, sql_query,null);
	}
		
	
	
	private String reportExecutionProcess(ReportMaster reportMaster, String uuid, String sql_query,ReportRequestDTO reportRequestDTO) throws ParseException, IOException {

		ReportTransactionDetails reportTransactionDetails = createReportTransaction(uuid,ReportStatus.PENDING.toString(), reportMaster.getReportName(), reportMaster.getReportType(),
				reportFolderPath, sql_query, reportMaster.getHeaderNames(),reportRequestDTO.getReportParam1(),reportRequestDTO.getReportParam2(),reportRequestDTO.getReportParam3(),reportRequestDTO.getReportParam4(),reportRequestDTO.getReportParam5(),reportRequestDTO.getReportParam6(),reportMaster.getReportExportType());

		if (reportMaster.getReportType().contains(ReportType.DOWNLOAD.toString())) {

			List<ReportData> datas = dataRepository.executeQuery(sql_query);
			
			
			
			if(reportMaster.getReportExportType().contains("EXCEL")){

				if(datas.size() >500) {
					
					reportFileName = reportMaster.getReportName() + Utility.getEpochTIme() + ".csv";
					cSVExporterWithFileCreate.exportCSVFiles(datas, reportFileName, reportMaster.getHeaderNames(), reportFolderPath);
					
					updateReportTransaction(reportTransactionDetails.getId(), ReportStatus.COMPLETED.toString(), reportFileName);
					
				}else {
					ExcelExporterWithFileCreate excelExporterWithFileCreate = new ExcelExporterWithFileCreate(datas);
					reportFileName = reportMaster.getReportName() + Utility.getEpochTIme() + ".xlsx";
	
					excelExporterWithFileCreate.export(reportMaster.getHeaderNames(), reportFolderPath + reportFileName, reportMaster.getReportName());
					updateReportTransaction(reportTransactionDetails.getId(), ReportStatus.COMPLETED.toString(), reportFileName);
				}
				
			}else if(reportMaster.getReportExportType().contains("CSV")){
				
				reportFileName = reportMaster.getReportName() + Utility.getEpochTIme() + ".csv";
				cSVExporterWithFileCreate.exportCSVFiles(datas, reportFileName, reportMaster.getHeaderNames(), reportFolderPath);
				
				updateReportTransaction(reportTransactionDetails.getId(), ReportStatus.COMPLETED.toString(), reportFileName);
			}

			return apiReportEndPoint + reportFileName;
		}

		return "The report has been schedule , once executed it will be reflected in your account.";

	}

	private String sqlBuilder(Object obj, ReportMaster reportMaster) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ValidationExceptions {

		String[] arrOfManParameter = reportMaster.getMandatoryParameter().split("\\^");
		String[] arrOfManParameterType = reportMaster.getMandatoryPrType().split("\\^");
		String[] arrOfParameters = reportMaster.getParameters().split("\\^");
		String[] arrOfCondisons = reportMaster.getQueryFilter().split("\\^");
		logger.info(reportMaster.getReportQuery()+"************");

		boolean flagForOperator = false;
		logger.info(sql_query+"************");

		for (int i = 0; i < arrOfManParameter.length; i++) {
			if (arrOfManParameterType[i].equalsIgnoreCase("DATE")) {

				Method m = obj.getClass().getMethod("get" + GeneralUtils.convertString("reportParam"+String.valueOf(i+1)));
				if (!GeneralUtils.dateValidator(String.valueOf(m.invoke(obj)))) {
					throw new ValidationExceptions(DATE_FORMAT, FormValidationExceptionEnums.DATE_FORMAT);
				}
			}
		}
		logger.info(reportMaster.getReportQuery()+"***r2*********");
		logger.info(sql_query+"******2******");

		// SQLQuery Builder
		sql_query = reportMaster.getReportQuery();
		logger.info(sql_query+"******3******");
		logger.info(arrOfParameters.length+"******3******");
		for (int i = 0; i < arrOfParameters.length; i++) {

			Method m = obj.getClass().getMethod("get" + GeneralUtils.convertString("reportParam"+String.valueOf(i+1)));
			String value = String.valueOf(m.invoke(obj));
			if (value == "" || value == null || value.equalsIgnoreCase("null")) {
				
			} else {
				
				String condisionUpdate = arrOfCondisons[i];
				logger.info("Inside arrOfParameters else block :: "+condisionUpdate);
				
				if(!flagForOperator) {
					logger.info(" Previous Query IF :: "+ sql_query);
					//sql_query = sql_query+" " +arrOfParameters[i] + "=" +condisionUpdate.replace(":" + arrOfParameters[i], "'" + value + "'");
					sql_query = sql_query+" " +condisionUpdate.replace(":" + arrOfParameters[i], "'" + value + "'");
					flagForOperator=true;
				}else {
					if(arrOfParameters[i].contains("Date") || arrOfParameters[i].contains("date")) {
						sql_query = sql_query+" and " +condisionUpdate.replace(":" + arrOfParameters[i], "'" + value + "'");
					}else {
						logger.info(" Previous Query else :: "+ sql_query);
						sql_query = sql_query+" and "+arrOfParameters[i] + "=" +condisionUpdate.replace(":" + arrOfParameters[i], "'" + value + "'");
						//sql_query = sql_query+" and " +condisionUpdate.replace(":" + arrOfParameters[i], "'" + value + "'");
					}
				}
			}
		}
		logger.info(sql_query + " :::: ******4******");

		if(!StringUtils.isEmpty(reportMaster.getAdditionalPartQuery())) {
			sql_query = sql_query+reportMaster.getAdditionalPartQuery();
		}
		
		logger.info("sql_query :: "+sql_query);
		return sql_query;
	}


	
	
	private ReportTransactionDetails createReportTransaction(String uuid, String status, String reportName,
			String reportType, String folderName, String reportQuery, String reportHeader ,String reportParam1,String reportParam2,String reportParam3,String reportParam4,String reportParam5,String reportParam6,String reportExportType) {
		ReportTransactionDetails reportTransactionDetails = new ReportTransactionDetails();

		reportTransactionDetails.setCreatedBy(uuid);
		reportTransactionDetails.setReportExecuteStatus(status);
		reportTransactionDetails.setReportName(reportName);
		reportTransactionDetails.setReportType(reportType);
		reportTransactionDetails.setReportValidity(reportFileExpiry);
		reportTransactionDetails.setFolderName(folderName);
		reportTransactionDetails.setReportQuery(reportQuery);
		reportTransactionDetails.setReportHeader(reportHeader);
		reportTransactionDetails.setReportParam1(reportParam1);
		reportTransactionDetails.setReportParam2(reportParam2);
		reportTransactionDetails.setReportParam3(reportParam3);
		reportTransactionDetails.setReportParam4(reportParam4);
		reportTransactionDetails.setReportParam5(reportParam5);
		reportTransactionDetails.setReportParam6(reportParam6);
		reportTransactionDetails.setReportExportType(reportExportType);
		

		return reportTransactionDetailsRepository.save(reportTransactionDetails);
	}

	private void updateReportTransaction(long reportId, String status, String reportFilePath) {

		ReportTransactionDetails reportTransactionDetails = reportTransactionDetailsRepository.findById(reportId);

		reportTransactionDetails.setReportExecuteStatus(status);
		reportTransactionDetails.setReportPath(reportFilePath);
		reportTransactionDetailsRepository.save(reportTransactionDetails);

	}

	public File fileDownloadUtility(String fileName) throws ValidationExceptions {
		ReportTransactionDetails reportTransactionDetails = reportTransactionDetailsRepository
				.findByReportPath(fileName);
		File file = new File(reportTransactionDetails.getFolderName() + fileName);
		if (!file.exists()) {
			throw new ValidationExceptions(FILE_NOT_FOUND, FormValidationExceptionEnums.FILE_NOT_FOUND);
		}
		return file;
	}

	public static void main(String[] args) throws Exception {
		Object o = populateValue();
		Method m = o.getClass().getMethod("getOrderId");
		System.out.println(m.invoke(o));
	}

	public static TransactionReportRequest populateValue() {
		TransactionReportRequest transactionReportRequest = new TransactionReportRequest();
		transactionReportRequest.setFromDate("2022-08-10");

		return transactionReportRequest;

	}

	public List<ReportMaster> getAllReportDetails(){
		return reportMasterRepository.findAll();
	}
	public List<ReportDownloadTrx> getAllReportTrList(String reportName){
		List<ReportTransactionDetails> rtx= reportTransactionDetailsRepository.findAllByReportName(reportName);
		List<ReportDownloadTrx> rdtList=new ArrayList<>();
for (ReportTransactionDetails reportTransactionDetails : rtx) {
	ReportDownloadTrx rt=new ReportDownloadTrx();
   rt.setCreatedBy(reportTransactionDetails.getCreatedBy());
   rt.setReportExecuteStatus(reportTransactionDetails.getReportExecuteStatus());
   rt.setReportName(reportTransactionDetails.getReportName());
   rt.setReportPath(reportTransactionDetails.getReportPath());
   rt.setFolderName(reportTransactionDetails.getFolderName());
   rt.setReportParam1(reportTransactionDetails.getReportParam1());
   rt.setReportParam2(reportTransactionDetails.getReportParam2());
   rt.setReportParam3(reportTransactionDetails.getReportParam3());
   rt.setReportParam4(reportTransactionDetails.getReportParam4());
   rt.setReportParam5(reportTransactionDetails.getReportParam5());
   rt.setReportType(reportTransactionDetails.getReportType());
   rt.setUpdatedBy(reportTransactionDetails.getUpdatedBy());
   rdtList.add(rt);
}
return rdtList; 
	}


	public List<ReportDownloadTrx> getAllReportByParam3AndName(String reportName,String reportParam3){
		List<ReportTransactionDetails> rtx= reportTransactionDetailsRepository.findAllByReportNameAndReportParam3(reportName, reportParam3);
		List<ReportDownloadTrx> rdtList=new ArrayList<>();
		
for (ReportTransactionDetails reportTransactionDetails : rtx) {
	ReportDownloadTrx rt=new ReportDownloadTrx();
   rt.setCreatedBy(reportTransactionDetails.getCreatedBy());
   rt.setReportExecuteStatus(reportTransactionDetails.getReportExecuteStatus());
   rt.setReportName(reportTransactionDetails.getReportName());
   rt.setReportPath(reportTransactionDetails.getReportPath());
   rt.setFolderName(reportTransactionDetails.getFolderName());
   rt.setReportParam1(reportTransactionDetails.getReportParam1());
   rt.setReportParam2(reportTransactionDetails.getReportParam2());
   rt.setReportParam3(reportTransactionDetails.getReportParam3());
   rt.setReportParam4(reportTransactionDetails.getReportParam4());
   rt.setReportParam5(reportTransactionDetails.getReportParam5());
   rt.setReportType(reportTransactionDetails.getReportType());
   rt.setUpdatedBy(reportTransactionDetails.getUpdatedBy());
   rdtList.add(rt);
}
return rdtList; 
	}

/**@author abhimanyu-kumar**/
	
	
	public File downloadJasperReportsResource(String fileName) throws ValidationExceptions {
		
		//JasperReportTransactionDetails jasperReportTransactionDetails = jasperReportTransactionDetailsRepository.findByReportPath(fileName);
		
		
		JasperReportTransactionDetails jasperReportTransactionDetails = jasperReportTransactionDetailsRepository.findByReportName(fileName);
		File outputFile = new File(jasperReportTransactionDetails.getFolderName()+"/"+jasperReportTransactionDetails.getReportName());

		//File outputFile = new File(jasperReportTransactionDetails.getFolderName()+"/"+jasperReportTransactionDetails.getReportName());

		if(jasperReportTransactionDetails != null) {
			
			byte [] data = jasperReportTransactionDetails.getData();
			
			
	        try ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {

	            outputStream.write(data);  // Write the bytes and you're done.

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			/*
			 * if (!outputFile.exists()) { throw new ValidationExceptions(FILE_NOT_FOUND,
			 * FormValidationExceptionEnums.FILE_NOT_FOUND); }
			 */
			
		}
		return outputFile;
		
		
		
		
	}

	private void updateJasperReportTransaction(long reportId, String status, String reportFilePath) {

		Optional<JasperReportTransactionDetails> jasperReportTransactionDetailsOptional = jasperReportTransactionDetailsRepository.findById(reportId);
		if (jasperReportTransactionDetailsOptional.isPresent()) {
			jasperReportTransactionDetailsOptional.get().setReportExecuteStatus(status);
			jasperReportTransactionDetailsOptional.get().setReportPath(reportFilePath);
			jasperReportTransactionDetailsRepository.save(jasperReportTransactionDetailsOptional.get());
		}

	}

	private JasperReportTransactionDetails jasperCreateReportTransaction(String uuid,byte[] byteArray, String fileName, String fileType,
			 String reportType) throws ParseException {
		
	
		JasperReportTransactionDetails jasperReportTransactionDetails = new JasperReportTransactionDetails();

		jasperReportTransactionDetails.setCreatedBy(uuid);
		jasperReportTransactionDetails.setUpdatedBy(uuid);
		Long longEpochTime = Utility.getEpochTIme() ;
		
		 //"reportPath": "3MBreport-",
	     //   "reportName": "3MBreport-1669792946756.pdf",
		if(fileType.equalsIgnoreCase("pdf")) {
			jasperReportTransactionDetails.setReportPath(apiReportEndPoint + "/"+fileName+longEpochTime+".pdf");// read from yaml
			
		}
		if(fileType.equalsIgnoreCase("xlsx")) {
			jasperReportTransactionDetails.setReportPath(apiReportEndPoint + "/"+fileName+longEpochTime+".xlsx");// read from yaml
			
		}
		
		jasperReportTransactionDetails.setReportName(fileName+longEpochTime+".pdf");//filename
		jasperReportTransactionDetails.setFileType(fileType);// PDF docx cvs
		jasperReportTransactionDetails.setReportExecuteStatus(ReportStatus.PENDING.toString());
		
		jasperReportTransactionDetails.setReportType(reportType);// Download online schedule
		jasperReportTransactionDetails.setReportValidity(reportFileExpiry);// read from yaml
		jasperReportTransactionDetails.setFolderName(reportFolderPath);//reportFolderPath
		jasperReportTransactionDetails.setData(byteArray);
		jasperReportTransactionDetails.setUuid(UUID.randomUUID().toString());

		return jasperReportTransactionDetailsRepository.save(jasperReportTransactionDetails);
	}



	public TxReportGenerate3MBResponse transaction3mbReportGeneratation(String uuid, TxReportGenerate3MBRequest txReportGenerate3MBRequest) {
		
		
		
		List<TransactionDetails> dataDetails = transactionDetailsRepository.getPrevious3MonthsTransactionReport();
		TxReportGenerate3MBResponse txReportGenerate3MBResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		JasperReportTransactionDetails jasperReportTransactionDetails = null;
		
		try {

			// Compile the Jasper report from .jrxml to .japser
			JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");
			String filenamegiven = txReportGenerate3MBRequest.getFileName();
			
			
			/*****/
			

			

			
			if (txReportGenerate3MBRequest.getReportType().equalsIgnoreCase(ReportType.DOWNLOAD.toString())) {
				
				// Get your data source
				if(dataDetails != null) {
					jrBeanCollectionDataSource = new JRBeanCollectionDataSource(dataDetails);
					// Add parameters
					Map<String, Object> parameters = new HashMap<>();

					parameters.put("createdBy", "eazypaymentz.com");
					parameters.put("data", "Previous3monthsTransaction");
					parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
					
					// Fill the report
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
					
				}else {
					jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<>());
					// Add parameters
					Map<String, Object> parameters = new HashMap<>();

					parameters.put("createdBy", "eazypaymentz.com");
					parameters.put("data", "Previous3monthsTransaction");
					parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
					
					// Fill the report
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
					
				}
				byte[] byteArray = JasperExportManager.exportReportToPdf(jasperPrint); 
				
				jasperReportTransactionDetails = jasperCreateReportTransaction(uuid,
						byteArray, txReportGenerate3MBRequest.getFileName(),
						txReportGenerate3MBRequest.getFileType(), txReportGenerate3MBRequest.getReportType());
				
				// Export the report to a PDF file
				if(txReportGenerate3MBRequest.getFileType().equalsIgnoreCase("PDF") && txReportGenerate3MBRequest.getFileType() != null && dataDetails != null && dataDetails.size() >0) {
					
					

					JasperExportManager.exportReportToPdfFile(jasperPrint, reportFolderPath + "/"+filenamegiven+".pdf");

				}
				if (txReportGenerate3MBRequest.getFileType().equalsIgnoreCase("xlsx") && txReportGenerate3MBRequest.getFileType() != null && dataDetails != null && dataDetails.size() > 0) {
					
					
					JasperExportManager.exportReportToPdfFile(jasperPrint,reportFolderPath + "/" + filenamegiven +  ".xlsx");

				}
				
				
				updateJasperReportTransaction(jasperReportTransactionDetails.getId(), ReportStatus.COMPLETED.toString(), filenamegiven);

				
			}else {
				//return "The report has been schedule , once executed it will be reflected in your account.";
				
				
				//updateJasperReportTransaction(jasperReportTransactionDetails.getId(), ReportStatus.INPROGRESS.toString(), filenamegiven);

			}
			System.out.println("Done");
			txReportGenerate3MBResponse = new TxReportGenerate3MBResponse();
			txReportGenerate3MBResponse.setJasperReportTransactionDetails(jasperReportTransactionDetails);
			txReportGenerate3MBResponse.setStatus(HttpStatus.SC_OK);
			if(dataDetails != null && dataDetails.size() >0) {
				
				txReportGenerate3MBResponse.setMessage("Report successfully generated " + jasperReportTransactionDetails.getReportPath());

			}else {
				
				txReportGenerate3MBResponse.setMessage("No Data Found and No Report generated");

			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return txReportGenerate3MBResponse;
	}
	
	


	


	public TxReportGenerateFUTDResponse transactionfutdReportGeneratation(String uuid, TxReportGenerateFUTDRequest txReportGenerateFUTDRequest) {
		
		List<TransactionDetails> listOfTransactionDetails = transactionDetailsRepository.getTransactionfutdTransactionReport(txReportGenerateFUTDRequest.getFromDate(), txReportGenerateFUTDRequest.getUpToDate());
		TxReportGenerateFUTDResponse txReportGenerateFUTDResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		
		try {
				// Compile the Jasper report from .jrxml to .japser
				JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");

				// Get your data source
				if(listOfTransactionDetails != null) {
					jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listOfTransactionDetails);
					// Add parameters
					Map<String, Object> parameters = new HashMap<>();

					parameters.put("createdBy", "eazypaymentz.com");
					parameters.put("data", "StartDateAndEndDateTransaction");
					parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
					
					// Fill the report
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);

					
					
				}else {
					jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<TransactionDetails>());
					// Add parameters
					Map<String, Object> parameters = new HashMap<>();

					parameters.put("createdBy", "eazypaymentz.com");
					parameters.put("data", "StartDateAndEndDateTransaction");
					parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
					
					// Fill the report
					jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
				}
				
			
				 
				
				
				String filenamegiven = txReportGenerateFUTDRequest.getFileName();
				// Export the report to a PDF file
				if(txReportGenerateFUTDRequest.getFileType().equalsIgnoreCase("PDF") && txReportGenerateFUTDRequest.getFileType() != null && listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
					
					JasperExportManager.exportReportToPdfFile(jasperPrint, txReportGenerateFUTDRequest.getReportLocation() + "\\"+filenamegiven+".pdf");

				}
				if (txReportGenerateFUTDRequest.getFileType().equalsIgnoreCase("CVS")
						&& txReportGenerateFUTDRequest.getFileType() != null && listOfTransactionDetails != null
						&& listOfTransactionDetails.size() > 0) {

					JasperExportManager.exportReportToPdfFile(jasperPrint,
							reportFolderPath + "\\" + filenamegiven + ".cvs");

				}
				if (txReportGenerateFUTDRequest.getFileType().equalsIgnoreCase("xlsx ")
						&& txReportGenerateFUTDRequest.getFileType() != null && listOfTransactionDetails != null
						&& listOfTransactionDetails.size() > 0) {

					JasperExportManager.exportReportToPdfFile(jasperPrint,
							reportFolderPath + "\\" + filenamegiven + ".xlsx");

				}
				if (txReportGenerateFUTDRequest.getFileType().equalsIgnoreCase("DOC")
						&& txReportGenerateFUTDRequest.getFileType() != null && listOfTransactionDetails != null
						&& listOfTransactionDetails.size() > 0) {

					JasperExportManager.exportReportToPdfFile(jasperPrint,
							reportFolderPath + "\\" + filenamegiven + ".doc");

				}
				if (txReportGenerateFUTDRequest.getFileType().equalsIgnoreCase("DOCX")
						&& txReportGenerateFUTDRequest.getFileType() != null && listOfTransactionDetails != null
						&& listOfTransactionDetails.size() > 0) {

					JasperExportManager.exportReportToPdfFile(jasperPrint,
							reportFolderPath + "\\" + filenamegiven + ".docx");

				}
				
				System.out.println("Done");
				txReportGenerateFUTDResponse = new TxReportGenerateFUTDResponse();
				txReportGenerateFUTDResponse.setFileName(txReportGenerateFUTDRequest.getFileName());
				txReportGenerateFUTDResponse.setFileType(txReportGenerateFUTDRequest.getFileType());
				txReportGenerateFUTDResponse.setReportLocation(txReportGenerateFUTDRequest.getReportLocation());
				//txReportGenerateFUTDResponse.setReportLocation(txReportGenerateFUTDRequest.);
				txReportGenerateFUTDResponse.setFromDate(txReportGenerateFUTDRequest.getFromDate());
				txReportGenerateFUTDResponse.setUpToDate(txReportGenerateFUTDRequest.getUpToDate());
				txReportGenerateFUTDResponse.setStatus(HttpStatus.SC_OK);
				if(listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
					txReportGenerateFUTDResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + txReportGenerateFUTDRequest.getReportLocation());
					//txReportGenerateFUTDResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + reportFolderPath);

				}else {
					txReportGenerateFUTDResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + txReportGenerateFUTDRequest.getReportLocation());
					//txReportGenerateFUTDResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + reportFolderPath);

				}
				

			} catch (Exception e) {
				e.printStackTrace();
				
			}
		//Response
		
		return txReportGenerateFUTDResponse;
	}	
	public TxReportGeneratePGWiseResponse transactionpgnamelikeReportGeneratation(String uuid, TxReportGeneratePGWiseRequest txReportGeneratePGWiseRequest) {
		
		List<TransactionDetails> listOfTransactionDetails = transactionDetailsRepository.getTransactionpgnamelikeTransactionReport(txReportGeneratePGWiseRequest.getPgName());
		TxReportGeneratePGWiseResponse txReportGeneratePGWiseResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		try {
			// Compile the Jasper report from .jrxml to .japser
			JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");

			// Get your data source
			if(listOfTransactionDetails != null) {
				
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listOfTransactionDetails);
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "PG-NameWiseLikeTransaction");
				//parameters.put("quotationInformation", "@copyright 2022 https://eazypaymentz.com/");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
			}else {

				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<TransactionDetails>());
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "PG-NameWiseLikeTransaction");
				//parameters.put("quotationInformation", "@copyright 2022 https://eazypaymentz.com/");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
			}
			
		
			 
			
			

			String filenamegiven = txReportGeneratePGWiseRequest.getFileName();
			// Export the report to a PDF file
			if(txReportGeneratePGWiseRequest.getFileType().equalsIgnoreCase("PDF") && txReportGeneratePGWiseRequest.getFileType() != null && listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				
				JasperExportManager.exportReportToPdfFile(jasperPrint, txReportGeneratePGWiseRequest.getReportLocation() + "\\"+filenamegiven+".pdf");

			}
			
			System.out.println("Done");
			txReportGeneratePGWiseResponse = new TxReportGeneratePGWiseResponse();
			txReportGeneratePGWiseResponse.setFileName(txReportGeneratePGWiseRequest.getFileName());
			txReportGeneratePGWiseResponse.setFileType(txReportGeneratePGWiseRequest.getFileType());
			txReportGeneratePGWiseResponse.setReportLocation(txReportGeneratePGWiseRequest.getReportLocation());
			txReportGeneratePGWiseResponse.setPgName(txReportGeneratePGWiseRequest.getPgName());
			txReportGeneratePGWiseResponse.setStatus(HttpStatus.SC_OK);
			if(listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				txReportGeneratePGWiseResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + txReportGeneratePGWiseRequest.getReportLocation());

			}else {
				txReportGeneratePGWiseResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + txReportGeneratePGWiseRequest.getReportLocation());

			}
			

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return txReportGeneratePGWiseResponse;
	}
	
	public TxReportGenerateTodayResponse transactiontodayReportGeneratation(String uuid, TxReportGenerateTodayRequest txReportGenerateTodayRequest) {
		
		List<TransactionDetails> listOfTransactionDetails = transactionDetailsRepository.getTransactionTodayTransactionReport();
		TxReportGenerateTodayResponse txReportGenerateTodayResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		try {
			
			// Compile the Jasper report from .jrxml to .japser
			JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");

			// Get your data source
			if(listOfTransactionDetails != null) {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listOfTransactionDetails);
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "Today-CurrentDate-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);

			}else {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<TransactionDetails>());
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "Today-CurrentDate-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
			}
			
		
			 
			
			
			String filenamegiven = txReportGenerateTodayRequest.getFileName();
			// Export the report to a PDF file
			if(txReportGenerateTodayRequest.getFileType().equalsIgnoreCase("PDF") && txReportGenerateTodayRequest.getFileType() != null && listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				
				JasperExportManager.exportReportToPdfFile(jasperPrint, txReportGenerateTodayRequest.getReportLocation() + "\\"+filenamegiven+".pdf");

			}
			
			System.out.println("Done");
			txReportGenerateTodayResponse = new TxReportGenerateTodayResponse();
			txReportGenerateTodayResponse.setFileName(txReportGenerateTodayRequest.getFileName());
			txReportGenerateTodayResponse.setFileType(txReportGenerateTodayRequest.getFileType());
			txReportGenerateTodayResponse.setReportLocation(txReportGenerateTodayRequest.getReportLocation());
			txReportGenerateTodayResponse.setStatus(HttpStatus.SC_OK);
			if(listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				txReportGenerateTodayResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + txReportGenerateTodayRequest.getReportLocation());

			}else {
				txReportGenerateTodayResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + txReportGenerateTodayRequest.getReportLocation());

			}
					
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	
		return txReportGenerateTodayResponse;
	}


	public TxReportMerchantIDWiseResponse generateMerchantIDWiseTransactionReport(TxReportGenerateMerchantID txReportGenerateMerchantID) {
		
		
		List<TransactionDetails> listOfTransactionDetails = transactionDetailsRepository.getTransactionMerchantIDWiseTransactionReport(txReportGenerateMerchantID.getMerchantId());
		TxReportMerchantIDWiseResponse txReportMerchantIDWiseResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		try {
			
			// Compile the Jasper report from .jrxml to .japser
			JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");

			// Get your data source
			if(listOfTransactionDetails != null) {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listOfTransactionDetails);
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "MerchantIDWise-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);

			}else {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<TransactionDetails>());
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "MerchantIDWise-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
			}
			
		
			 
			
			
			String filenamegiven = txReportGenerateMerchantID.getFileName();
			// Export the report to a PDF file
			if(txReportGenerateMerchantID.getFileType().equalsIgnoreCase("PDF") && txReportGenerateMerchantID.getFileType() != null && listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				
				JasperExportManager.exportReportToPdfFile(jasperPrint, txReportGenerateMerchantID.getReportLocation() + "\\"+filenamegiven+".pdf");

			}
			
			System.out.println("Done");
			txReportMerchantIDWiseResponse = new TxReportMerchantIDWiseResponse();
			txReportMerchantIDWiseResponse.setFileName(txReportGenerateMerchantID.getFileName());
			txReportMerchantIDWiseResponse.setFileType(txReportGenerateMerchantID.getFileType());
			txReportMerchantIDWiseResponse.setReportLocation(txReportGenerateMerchantID.getReportLocation());
			txReportMerchantIDWiseResponse.setMerchantID(txReportGenerateMerchantID.getMerchantId());
			txReportMerchantIDWiseResponse.setStatus(HttpStatus.SC_OK);
			if(listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				txReportMerchantIDWiseResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + txReportGenerateMerchantID.getReportLocation());

			}else {
				txReportMerchantIDWiseResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + txReportGenerateMerchantID.getReportLocation());

			}
					
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	
		return txReportMerchantIDWiseResponse;
		
	}


	public TxReportMerchantIDAndOrderIDWiseResponse generateMerchantIDAndOrderIDWiseTransactionReport(TxReportGenerateMerchantIDAndOrderID txReportGenerateMerchantIDAndOrderID) {
		
		

		List<TransactionDetails> listOfTransactionDetails = transactionDetailsRepository.getTransactionMerchantIDAndOrderIDTransactionReport(txReportGenerateMerchantIDAndOrderID.getMerchantId(),txReportGenerateMerchantIDAndOrderID.getOrderId());
		TxReportMerchantIDAndOrderIDWiseResponse txReportMerchantIDAndOrderIDWiseResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		try {
			
			// Compile the Jasper report from .jrxml to .japser
			JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");

			// Get your data source
			if(listOfTransactionDetails != null) {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listOfTransactionDetails);
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "MerchantIDAndOrderIDWise-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);

			}else {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<TransactionDetails>());
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "MerchantIDAndOrderIDWise-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
			}
			
		
			 
			
			
			String filenamegiven = txReportGenerateMerchantIDAndOrderID.getFileName();
			// Export the report to a PDF file
			if(txReportGenerateMerchantIDAndOrderID.getFileType().equalsIgnoreCase("PDF") && txReportGenerateMerchantIDAndOrderID.getFileType() != null && listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				
				JasperExportManager.exportReportToPdfFile(jasperPrint, txReportGenerateMerchantIDAndOrderID.getReportLocation() + "\\"+filenamegiven+".pdf");

			}
			
			System.out.println("Done");
			txReportMerchantIDAndOrderIDWiseResponse = new TxReportMerchantIDAndOrderIDWiseResponse();
			txReportMerchantIDAndOrderIDWiseResponse.setFileName(txReportGenerateMerchantIDAndOrderID.getFileName());
			txReportMerchantIDAndOrderIDWiseResponse.setFileType(txReportGenerateMerchantIDAndOrderID.getFileType());
			txReportMerchantIDAndOrderIDWiseResponse.setReportLocation(txReportGenerateMerchantIDAndOrderID.getReportLocation());
			txReportMerchantIDAndOrderIDWiseResponse.setMerchantID(txReportGenerateMerchantIDAndOrderID.getMerchantId());
			txReportMerchantIDAndOrderIDWiseResponse.setOrderID(txReportGenerateMerchantIDAndOrderID.getOrderId());
			txReportMerchantIDAndOrderIDWiseResponse.setStatus(HttpStatus.SC_OK);
			if(listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				txReportMerchantIDAndOrderIDWiseResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + txReportGenerateMerchantIDAndOrderID.getReportLocation());

			}else {
				txReportMerchantIDAndOrderIDWiseResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + txReportGenerateMerchantIDAndOrderID.getReportLocation());

			}
					
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	
		return txReportMerchantIDAndOrderIDWiseResponse;
		
	}


	public TxReportPGNameWithDateWiseResponse generatePGNameWithDateWiseTransactionReport(TxReportGeneratePGNameWithDate txReportGeneratePGNameWithDate) {

		List<TransactionDetails> listOfTransactionDetails = transactionDetailsRepository.findByPgNameWithDate(txReportGeneratePGNameWithDate.getPgName(),txReportGeneratePGNameWithDate.getFromDate(),txReportGeneratePGNameWithDate.getUpToDate());
		TxReportPGNameWithDateWiseResponse txReportPGNameWithDateWiseResponse = null;
		JRBeanCollectionDataSource jrBeanCollectionDataSource = null;
		JasperPrint jasperPrint = null;
		try {
			
			// Compile the Jasper report from .jrxml to .japser
			JasperReport jasperReport = JasperCompileManager.compileReport("Transaction.jrxml");

			// Get your data source
			if(listOfTransactionDetails != null) {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(listOfTransactionDetails);
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "PGNameWithDateWise-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);

			}else {
				jrBeanCollectionDataSource = new JRBeanCollectionDataSource(new ArrayList<TransactionDetails>());
				// Add parameters
				Map<String, Object> parameters = new HashMap<>();

				parameters.put("createdBy", "eazypaymentz.com");
				parameters.put("data", "PGNameWithDateWise-Transaction");
				parameters.put("quotationInformation", "@copyright 2022 eazypaymentz.com");
				
				// Fill the report
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
			}
			
		
			 
			
			
			String filenamegiven = txReportGeneratePGNameWithDate.getFileName();
			// Export the report to a PDF file
			if(txReportGeneratePGNameWithDate.getFileType().equalsIgnoreCase("PDF") && txReportGeneratePGNameWithDate.getFileType() != null && listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				
				JasperExportManager.exportReportToPdfFile(jasperPrint, txReportGeneratePGNameWithDate.getReportLocation() + "\\"+filenamegiven+".pdf");

			}
			
			System.out.println("Done");
			txReportPGNameWithDateWiseResponse = new TxReportPGNameWithDateWiseResponse();
			txReportPGNameWithDateWiseResponse.setFileName(txReportGeneratePGNameWithDate.getFileName());
			txReportPGNameWithDateWiseResponse.setFileType(txReportGeneratePGNameWithDate.getFileType());
			txReportPGNameWithDateWiseResponse.setReportLocation(txReportGeneratePGNameWithDate.getReportLocation());
			txReportPGNameWithDateWiseResponse.setPgName(txReportGeneratePGNameWithDate.getPgName());
			txReportPGNameWithDateWiseResponse.setFromDate(txReportGeneratePGNameWithDate.getFromDate());
			txReportPGNameWithDateWiseResponse.setUpToDate(txReportGeneratePGNameWithDate.getUpToDate());
			txReportPGNameWithDateWiseResponse.setStatus(HttpStatus.SC_OK);
			if(listOfTransactionDetails != null && listOfTransactionDetails.size() >0) {
				txReportPGNameWithDateWiseResponse.setMessage("Report successfully generated for https://eazypaymentz.com/   @path " + txReportGeneratePGNameWithDate.getReportLocation());

			}else {
				txReportPGNameWithDateWiseResponse.setMessage("No Data Found ! No Report Generated ! https://eazypaymentz.com/   @path " + txReportGeneratePGNameWithDate.getReportLocation());

			}
					
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	
		return txReportPGNameWithDateWiseResponse;
		
	}

/****/
}




