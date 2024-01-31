package com.asktech.admin.enums;

import java.util.HashMap;
import java.util.Map;

public enum RechargeStatus {
	
	INITIATED("INITIATED"),REFUNDED("REFUNDED") , PENDING("PENDING") ,REJECTED("REJECTED"), SUCCESS("SUCCESS"), FAILED("FAILED");
	
	private static Map<String, Object> map = new HashMap<>();
	private String rechargeStatus;
	
	
	static {
		for (RechargeStatus rechargeStatus : RechargeStatus.values()) {
			map.put(rechargeStatus.rechargeStatus, rechargeStatus);
		}
	}


	


	private RechargeStatus(String rechargeStatus) {
		this.rechargeStatus = rechargeStatus;
	}


	public static Map<String, Object> getMap() {
		return map;
	}


	public static void setMap(Map<String, Object> map) {
		RechargeStatus.map = map;
	}


	public String getRechargeStatus() {
		return rechargeStatus;
	}


	public void setRechargeStatus(String rechargeStatus) {
		this.rechargeStatus = rechargeStatus;
	}
	
	


}
