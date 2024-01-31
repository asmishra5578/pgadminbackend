package com.asktech.admin.reports.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TransactionReportRequest {

	@NonNull
	private String fromDate;
	@NonNull
	private String toDate;
	private String orderId;
	private String merchantOrderId;
	private String pgOrderId;		
	private String merchantId;
	private String pgName;
	private String amount;	
	private String status;
	private String paymentMode;	
	private String reportExportType;
	private String reportName;
}
