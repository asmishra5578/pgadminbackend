package com.asktech.admin.dto.admin;

import javax.persistence.Column;

import com.asktech.admin.model.DistributorMerchantDetails;
import com.asktech.admin.model.MerchantPGDetails;
import com.asktech.admin.model.MerchantPGServices;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantCreateResponse {

	private String merchantId;
	private String uuid;
	private String merchantName;
	private String phoneNumber;
	private String emailId;
	private String kycStatus;
	private String appId;
	private String merchantStatus;
	private String secretId;
	private String supportEmailId;
	private String supportPhoneNo;
	private String merchantType;
	private String companyName;
	private String permenantLink;
	private String userStatus;
	private String updatedBy;
	private String createdBy;
	private String saltKey;
	private String password;
	
	
	private String payoutFlag;
	private String otpStatus;
	
	/**@author abhimanyu start*/
	private String message;
	//private DistributorMerchantDetails distributorMerchantDetails;
	//private MerchantPGDetails defaultMerchantPGDetails;
	//private MerchantPGServices defaultMerchantPGServices;
	/**@author abhimanyu end*/
	
	
}
