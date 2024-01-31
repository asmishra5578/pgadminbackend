package com.asktech.admin.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.asktech.admin.model.TransactionDetails;

public class ExcelHelper {
	
	    private XSSFWorkbook workbook;
	    private XSSFSheet sheet;
	    private List<TransactionDetails> transactionDetailsList;
	     
	    public ExcelHelper(List<TransactionDetails> transactionDetailsList) {
	        this.transactionDetailsList = transactionDetailsList;
	        workbook = new XSSFWorkbook();
	    }
	 
	 
	    private void writeHeaderLine() {
	        sheet = workbook.createSheet("report");
	         
	        Row row = sheet.createRow(0);
	         
	        CellStyle style = workbook.createCellStyle();
	        XSSFFont font = workbook.createFont();
	        font.setBold(true);
	        font.setFontHeight(16);
	        style.setFont(font);
	         
	        createCell(row, 0, "MerchandID", style);      
	        createCell(row, 1, "PG-Type", style);       
	        createCell(row, 2, "Sum(Amount)", style);    
	     
	         
	    }
	     
	    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
	        sheet.autoSizeColumn(columnCount);
	        Cell cell = row.createCell(columnCount);
	        if (value instanceof Integer) {
	            cell.setCellValue((Integer) value);
	        } else if (value instanceof Boolean) {
	            cell.setCellValue((Boolean) value);
	        }else {
	            cell.setCellValue((String) value);
	        }
	        cell.setCellStyle(style);
	    }
	     
	    private void writeDataLines() {
	        int rowCount = 1;
	 
	        CellStyle style = workbook.createCellStyle();
	        XSSFFont font = workbook.createFont();
	        font.setFontHeight(14);
	        style.setFont(font);
	                 
	        for (TransactionDetails tx : transactionDetailsList) {
	            Row row = sheet.createRow(rowCount++);
	            int columnCount = 0;
	             
	            createCell(row, columnCount++, tx.getMerchantId(), style);
	            createCell(row, columnCount++, tx.getPgType(), style);
	            createCell(row, columnCount++, tx.getAmount(), style);
	          
	             
	        }
	    }
	     
	    public HttpServletResponse export(HttpServletResponse response) throws IOException {
	        writeHeaderLine();
	        writeDataLines();
	         
	        ServletOutputStream outputStream = response.getOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	         
	        outputStream.close();
			return response;
	         
	    }

	

}
