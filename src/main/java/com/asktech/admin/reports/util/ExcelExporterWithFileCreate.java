package com.asktech.admin.reports.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.admin.reports.dto.ReportData;

@Service
public class ExcelExporterWithFileCreate {
	
	static Logger logger = LoggerFactory.getLogger(ExcelExporterWithFileCreate.class);

	@Value("${reportArgument.reportHeaderStyle.font}")
	private String reportHeaderFont;
	@Value("${reportArgument.reportHeaderStyle.size}")
	private int reportHeaderSize;
	@Value("${reportArgument.reportHeaderStyle.bold}")
	private String reportHeaderBold;
	
	@Value("${reportArgument.reportDataStyle.font}")
	private String reportDataFont;
	@Value("${reportArgument.reportDataStyle.size}")
	private int reportDataSize;
	@Value("${reportArgument.reportDataStyle.bold}")
	private String reportDataBold;
	
	private  XSSFWorkbook workbook;
	private  XSSFSheet sheet;
	private  List<ReportData> reportDatas;

	public ExcelExporterWithFileCreate(List<ReportData> reportDatas) {
		this.reportDatas = reportDatas;
		workbook = new XSSFWorkbook();
	}

	private  void writeHeaderLine(String headers,String reportName) {
		logger.info("Inside writeHeaderLine()");		
		
		sheet = workbook.createSheet(reportName);
		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		//logger.info("Headers :: "+headers);
		
		font.setFontHeight(reportHeaderSize);
		font.setFontName(reportHeaderFont);
		font.setBold(true);
		style.setFont(font);
		
		String[] arrOfStr = headers.split("\\^");
		int columnCount = 0;
		for(String headerValue : arrOfStr) {
			createCell(row, columnCount++, headerValue, style);
		}
		logger.info("End writeHeaderLine()");
	}

	private  void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private  void writeDataLines() {

		logger.info("Inside writeDataLines()");
		int rowCount = 1;

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		
		font.setFontHeight(reportDataSize);
		font.setFontName(reportDataFont);		
		style.setFont(font);		

		for (ReportData reportData : reportDatas) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			String[] arrOfStr = reportData.getDatas().split("\\^");
			
			for (String data : arrOfStr) {
				createCell(row, columnCount++, data, style);
			}
		}
		logger.info("End writeDataLines()");
	}

	public void export(String headers, String fileName, String reportName) throws IOException {
		writeHeaderLine(headers,reportName);
		writeDataLines();

		
		FileOutputStream fileout = new FileOutputStream(fileName);
		workbook.write(fileout);
		workbook.close();

		fileout.close();

	}
}
