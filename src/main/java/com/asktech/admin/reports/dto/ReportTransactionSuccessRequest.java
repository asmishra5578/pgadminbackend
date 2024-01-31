package com.asktech.admin.reports.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ReportTransactionSuccessRequest {

	@NonNull
	private String fromDate;
	@NonNull
	private String toDate;
	private String reportExportType;
	private String reportName;
}
