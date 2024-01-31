package com.asktech.admin.enums;

import java.util.HashMap;
import java.util.Map;

public enum MerchantWalletStatus {
    TRUE("TRUE"),FALSE("FALSE");
	
	private static Map<String, Object> map = new HashMap<>();
	private String merchantWalletStatus;
	static {
		for (MerchantWalletStatus merchantWalletStatus : MerchantWalletStatus.values()) {
			map.put(merchantWalletStatus.merchantWalletStatus, merchantWalletStatus);
		}
	}

	public String getValue() {
		return merchantWalletStatus;
	}


	private MerchantWalletStatus(String k) {
		this.merchantWalletStatus = k;
	}

	public String getStatus() {
		return merchantWalletStatus;
	}

}
