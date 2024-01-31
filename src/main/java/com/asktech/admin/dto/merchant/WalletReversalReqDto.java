package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletReversalReqDto {
	private String amount;
	private String transactionType;
	private String purpose;
	private String remarks;
	private String referenceId;
}
