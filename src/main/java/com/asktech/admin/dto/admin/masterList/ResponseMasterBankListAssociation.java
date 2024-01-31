package com.asktech.admin.dto.admin.masterList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMasterBankListAssociation {

	private String bankCode;
	private String merchantId;
	private String pgId;
	private String message;
	private String errorMessage;
}
