package com.asktech.admin.util.bulkUpload;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.ValidationExceptions;

public class FileValidations implements ErrorValues {

	public static void fileUploadValidation(MultipartFile file) throws ValidationExceptions {
		String docs = file.getOriginalFilename();
		if (!(getFileExtension(docs).equalsIgnoreCase(".jpg") || getFileExtension(docs).equalsIgnoreCase(".jpeg")
				|| getFileExtension(docs).equalsIgnoreCase(".png") || getFileExtension(docs).equalsIgnoreCase(".pdf")
				|| getFileExtension(docs).equalsIgnoreCase(".docx") || getFileExtension(docs).equalsIgnoreCase(".csv")
				|| getFileExtension(docs).equalsIgnoreCase(".xlsx"))) {
			throw new ValidationExceptions(UPLOAD_FORMATE_ERROR, FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
		}

	}

	public static String getFileExtension(String docs) {
		String extension = "";
		extension = docs.substring(docs.lastIndexOf("."));
		return extension;

	}

	public static void bulkFileUploadValidation(MultipartFile file) throws ValidationExceptions {
		String docs = file.getOriginalFilename();
		if (!(getFileExtension(docs).equalsIgnoreCase(".csv") || getFileExtension(docs).equalsIgnoreCase(".xlsx"))) {
			throw new ValidationExceptions(UPLOAD_FORMATE_ERROR, FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
		}
	}

	static int dataCount = 0;

	@SuppressWarnings("resource")
	public static void checkFileFormate(MultipartFile file) throws ValidationExceptions, IOException {
		dataCount = 0;
		String docs = file.getOriginalFilename();
		if (getFileExtension(docs).equalsIgnoreCase(".xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet worksheet = workbook.getSheetAt(0);
			DataFormat fmt = workbook.createDataFormat();
			CellStyle textStyle = workbook.createCellStyle();
			textStyle.setDataFormat(fmt.getFormat("@"));
			worksheet.setDefaultColumnStyle(0, textStyle);
			worksheet.setDefaultColumnStyle(1, textStyle);
			worksheet.setDefaultColumnStyle(2, textStyle);
			worksheet.setDefaultColumnStyle(3, textStyle);
			worksheet.setDefaultColumnStyle(4, textStyle);
			worksheet.setDefaultColumnStyle(5, textStyle);
			worksheet.setDefaultColumnStyle(6, textStyle);
			worksheet.setDefaultColumnStyle(7, textStyle);
			dataCount = worksheet.getLastRowNum();
			while (0 <= worksheet.getLastRowNum()) {
				XSSFRow row = worksheet.getRow(0);
				if (!row.getCell(0).getStringCellValue().equals("InternalOrderId")
						|| !row.getCell(1).getStringCellValue().equals("UTR")
						|| !row.getCell(2).getStringCellValue().equals("ReferenceId")
						|| !row.getCell(3).getStringCellValue().equals("Status")
						|| !row.getCell(4).getStringCellValue().equals("TransactionMessage")
						|| !row.getCell(5).getStringCellValue().equals("Comment")
						|| !row.getCell(6).getStringCellValue().equals("CallBack")) {
					throw new ValidationExceptions(UPLOAD_FORMATE_ERROR,
							FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
				}
				break;
			}
		}

	}

}
