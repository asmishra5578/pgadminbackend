package com.asktech.admin.customInterface;

import java.math.BigDecimal;

public interface IMerchantTransaction {

	String getMerchantId();;
	String getStatus();
	BigDecimal getAmount();
	
}
