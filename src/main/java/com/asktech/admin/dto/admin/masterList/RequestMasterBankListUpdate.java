package com.asktech.admin.dto.admin.masterList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestMasterBankListUpdate {

	private String pgId;
	private String bankCode;
	private String merchantId;
	private String status;
}
