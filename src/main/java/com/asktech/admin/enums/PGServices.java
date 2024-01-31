package com.asktech.admin.enums;

import java.util.HashMap;
import java.util.Map;

public enum PGServices {
	
CARD("CARD"),EMI("EMI"),UPI("UPI"),UPI_QR("UPI_QR"),GPAY("GPAY"),NB("NB"),WALLET("WALLET"), OTHERS("OTHERS");
	
	private static Map<String, Object> map = new HashMap<>();
	private String apStatus;
	static {
		for (PGServices apStatus : PGServices.values()) {
			map.put(apStatus.apStatus, apStatus);
		}
	}

	public String getValue() {
		return apStatus;
	}


	private PGServices(String k) {
		this.apStatus = k;
	}

	public String getStatus() {
		return apStatus;
	}

}
