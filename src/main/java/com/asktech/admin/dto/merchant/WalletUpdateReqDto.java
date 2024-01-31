package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletUpdateReqDto {

	private String merchantId;
	private String walletCallBackAPI;
	private String status;
	private String walletHoldAmount;
	private String instantReversal;
}
