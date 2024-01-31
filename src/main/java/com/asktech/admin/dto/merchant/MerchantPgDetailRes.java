package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class MerchantPgDetailRes {
	private long id;
	private String merchantID;
	private String merchantPGId;
	private String merchantPGName;
	private String merchantPGAppId;
	private String merchantPGSecret;
	private String merchantPGSaltKey;
	private String merchantPGAdd1;
	private String merchantPGAdd2;
	private String merchantPGAdd3;
	private String created;
	private String updated;
	private String status; 
	
}
