package com.asktech.admin.customInterface.payout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchanSerectAppId {
    private String MerchantEMail;
	private String MerchantId;
	private String Merchant_name;
	private String Phone_number;
	private String User_status;
	private String Kyc_status;
	private String Appid;
	private String Secret_id;
	private String Salt_key;
	private String Uuid;
}
