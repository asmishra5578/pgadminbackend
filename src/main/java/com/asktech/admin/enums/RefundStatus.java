package com.asktech.admin.enums;

import java.util.HashMap;
import java.util.Map;

import com.asktech.admin.enums.RefundStatus;

public enum RefundStatus {

INITIATED("INITIATED"),REFUNDED("REFUNDED") , PENDING("PENDING") ,APPROVED("APPROVED"),REJECTED("REJECTED");
	
	private static Map<String, Object> map = new HashMap<>();
	private String refundStatus;
	static {
		for (RefundStatus refundStatus : RefundStatus.values()) {
			map.put(refundStatus.refundStatus, refundStatus);
		}
	}

	public String getValue() {
		return refundStatus;
	}


	private RefundStatus(String k) {
		this.refundStatus = k;
	}

	public String getStatus() {
		return refundStatus;
	}
	
}
