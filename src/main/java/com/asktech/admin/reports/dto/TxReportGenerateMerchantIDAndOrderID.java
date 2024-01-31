package com.asktech.admin.reports.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxReportGenerateMerchantIDAndOrderID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8919955791243070217L;
	private String reportLocation;
	private String fileName;
	private String fileType;
	private String merchantId;
	private String orderId;

}
