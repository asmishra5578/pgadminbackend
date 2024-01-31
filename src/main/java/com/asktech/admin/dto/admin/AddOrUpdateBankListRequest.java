package com.asktech.admin.dto.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddOrUpdateBankListRequest {

	private String adminUuid;
    private String bankname;
	private String bankcode;
	private String pgBankCode;
	private String pgName;
	private String pgId;
	private String status;
	private String merchantId;
    
}
