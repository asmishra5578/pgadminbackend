package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class TxReportGenerate3MBRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -764538895339572413L;
	//private String reportLocation; read from yaml file
	private String fileName;
	private String fileType;// PDF,doc, docx,xlsx, cvs
	private String reportType;//DOWNLOAD("DOWNLOAD"),SCHEDULE("SCHEDULE"),ONLINE("ONLINE");
	//reportStatus PENDING("PENDING"),INPROGRESS("INPROGRESS"),COMPLETED("COMPLETED"),FAILED("FAILED");

}
