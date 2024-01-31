package com.asktech.admin.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletCreateReqDto {
	String mainWalletid;
	String status;
	String amount;
	String name;
}
