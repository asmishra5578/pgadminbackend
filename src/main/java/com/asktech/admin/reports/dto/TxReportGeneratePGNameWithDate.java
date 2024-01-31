package com.asktech.admin.reports.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxReportGeneratePGNameWithDate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2941858699620208081L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String pgName;
	private String fromDate;
	private String upToDate;

}
