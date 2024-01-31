package com.asktech.admin.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayUserDetails {

	private String merchantId;
	private String whitelistedip;
	private String walletCheck;
	private String mainWalletid;
	private String status;
	private String amount;
	private String name;
}
