package com.asktech.admin.dto.admin.masterList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestMasterWalletListAssociation {

	private String pgId;
	private String walletCode;
	private String merchantId;
}
