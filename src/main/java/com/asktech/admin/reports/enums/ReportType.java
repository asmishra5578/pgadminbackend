package com.asktech.admin.reports.enums;

import java.util.HashMap;
import java.util.Map;

public enum ReportType {
	
DOWNLOAD("DOWNLOAD"),SCHEDULE("SCHEDULE"),ONLINE("ONLINE");
	
	private static Map<String, Object> map = new HashMap<>();
	private String apStatus;
	static {
		for (ReportType apStatus : ReportType.values()) {
			map.put(apStatus.apStatus, apStatus);
		}
	}

	public String getValue() {
		return apStatus;
	}


	private ReportType(String k) {
		this.apStatus = k;
	}

	public String getStatus() {
		return apStatus;
	}

}
