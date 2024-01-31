package com.asktech.admin.reports.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxReportGenerateTodayResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7072193277549654070L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private int status;
	private String message;

}
