package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TxReportGenerateTodayRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2775735535328335644L;
	private String reportLocation;
	private String fileName;
	private String fileType;

}
