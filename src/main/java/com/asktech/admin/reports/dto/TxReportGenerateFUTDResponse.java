package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TxReportGenerateFUTDResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -749254613214666623L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String fromDate;
	private String upToDate;
	private int status;
	private String message;
	
	

}
