package com.asktech.admin.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllMerchantDetails {
	private String merchantName;
    private String merchantEMail;
    private String phoneNumber;
    private String saltKey;
    private String merchantId;
    private String merchantStatus;
    private String kycStatus;
    private String uuid;
    private String merchantAppId;
    private String merchantSecretKey;
    private List<MerchantPgdetails> merchantpgdetails;
    //private List<MerchantServiceDetails> merchantServiceDetails;
   
}
