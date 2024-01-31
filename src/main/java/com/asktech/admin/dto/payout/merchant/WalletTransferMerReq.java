package com.asktech.admin.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletTransferMerReq {
	private String orderid;
	private String phonenumber;
	private String amount;
}
