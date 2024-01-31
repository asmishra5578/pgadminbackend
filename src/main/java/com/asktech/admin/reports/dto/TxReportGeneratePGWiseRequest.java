package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TxReportGeneratePGWiseRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8991989580024158598L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String pgName;
	

}
