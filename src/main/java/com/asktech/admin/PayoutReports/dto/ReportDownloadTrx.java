package com.asktech.admin.PayoutReports.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDownloadTrx {
    private String createdBy;
	private String updatedBy;
	private String reportPath;
	private String reportName;
    private String reportExecuteStatus;
	private String folderName;
	private String reportType;
    private String reportParam1;
	private String reportParam2;
	private String reportParam3;
    private String reportParam4;
	private String reportParam5;
}
