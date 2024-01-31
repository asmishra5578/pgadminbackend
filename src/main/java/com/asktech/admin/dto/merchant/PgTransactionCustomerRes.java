package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PgTransactionCustomerRes {

	private String uuid;
	private String userName;
	private String userPhone;
	private String userEmail;
	private Integer amount;
	private String returnUrl;
	private String orderId;
	private String customerId;
	private String orderNote;
	private String merchantId;
	private String status;
	private String deviceType;
	private String ipaddress;
	private String message;
	
	
}
