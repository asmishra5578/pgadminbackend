package com.asktech.admin.reports.enums;

import java.util.HashMap;
import java.util.Map;

public enum ReportStatus {
	
PENDING("PENDING"),INPROGRESS("INPROGRESS"),COMPLETED("COMPLETED"),FAILED("FAILED");
	
	private static Map<String, Object> map = new HashMap<>();
	private String apStatus;
	static {
		for (ReportStatus apStatus : ReportStatus.values()) {
			map.put(apStatus.apStatus, apStatus);
		}
	}

	public String getValue() {
		return apStatus;
	}


	private ReportStatus(String k) {
		this.apStatus = k;
	}

	public String getStatus() {
		return apStatus;
	}

}
