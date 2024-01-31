package com.asktech.admin.dto.admin.masterList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMasterWalletListAssociation {

	private String walletCode;
	private String merchantId;
	private String pgId;
	private String message;
	private String errorMessage;
}
