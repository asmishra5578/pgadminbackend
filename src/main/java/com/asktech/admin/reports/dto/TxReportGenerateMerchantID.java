package com.asktech.admin.reports.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxReportGenerateMerchantID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2834851883364069245L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String merchantId;
	

}
