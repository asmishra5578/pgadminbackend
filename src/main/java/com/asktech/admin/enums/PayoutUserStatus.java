package com.asktech.admin.enums;

import java.util.HashMap;
import java.util.Map;

public enum PayoutUserStatus {
ACTIVE("ACTIVE"),BLOCKED("BLOCKED"),PENDING("PENDING");
	
	private static Map<String, Object> map = new HashMap<>();
	private String payoutUserStatus;
	static {
		for (PayoutUserStatus payoutUserStatus : PayoutUserStatus.values()) {
			map.put(payoutUserStatus.payoutUserStatus, payoutUserStatus);
		}
	}

	public String getValue() {
		return payoutUserStatus;
	}


	private PayoutUserStatus(String k) {
		this.payoutUserStatus = k;
	}

	public String getStatus() {
		return payoutUserStatus;
	}
	
}
