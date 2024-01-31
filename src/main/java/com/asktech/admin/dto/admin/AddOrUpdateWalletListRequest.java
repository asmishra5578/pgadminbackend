package com.asktech.admin.dto.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddOrUpdateWalletListRequest {

    private String adminUuid;
    private String walletname;
	private String paymentcodepg;
	private String pgname;
	private String pgId;
	private String paymentcode;
	private String status;
	private String merchantId;
    
}
