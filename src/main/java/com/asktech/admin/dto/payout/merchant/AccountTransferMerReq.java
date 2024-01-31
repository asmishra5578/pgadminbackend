package com.asktech.admin.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransferMerReq {
	private String orderid;
	private String phonenumber;
	private String amount;
	private String bankaccount;
	private String ifsc;
	private String purpose;
	private String beneficiaryName;
	private String requestType;
}
