package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class TxReportGeneratePGWiseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8571429967537746939L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String pgName;
	private int status;
	private String message;
	

}
