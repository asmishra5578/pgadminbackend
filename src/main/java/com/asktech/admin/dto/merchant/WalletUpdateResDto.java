package com.asktech.admin.dto.merchant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletUpdateResDto {

	private String walletId;
	private String walletStatus;
	private String responseStatus;
	private String name;
	private String walletCallBackAPI;
}
