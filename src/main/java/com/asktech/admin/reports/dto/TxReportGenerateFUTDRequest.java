package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TxReportGenerateFUTDRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1015870429511609266L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String fromDate;
	private String upToDate;

}
