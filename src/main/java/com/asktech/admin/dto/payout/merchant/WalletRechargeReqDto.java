package com.asktech.admin.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletRechargeReqDto {
	private String amount;
	private String transactionType;
	private String orderId;
	private String purpose;
	private String remarks;
	private String referenceId;
	
}
