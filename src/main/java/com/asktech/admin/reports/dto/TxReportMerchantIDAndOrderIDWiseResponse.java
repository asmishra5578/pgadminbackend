package com.asktech.admin.reports.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxReportMerchantIDAndOrderIDWiseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3778430610558827033L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private int status;
	private String message;
	private String merchantID;
	private String orderID;

}
