package com.asktech.admin.dto.merchant;

import org.json.simple.JSONObject;

import com.asktech.admin.model.payout.PayoutApiUserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayoutAndWalletDto {

	private String merchantId;
	private String whitelistedIP;
	private String merchantStatus;
	private String walletStatus;
	private String created;
	private String updated;
	private JSONObject merchantWallet ;
}
