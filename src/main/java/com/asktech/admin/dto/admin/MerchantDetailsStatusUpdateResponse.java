package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantPGDetails;
import com.asktech.admin.model.MerchantPGServices;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class MerchantDetailsStatusUpdateResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1794498760854447332L;
	//private MerchantDetails merchantDetails;
	private int status;
	private String message;
	
	
	
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
	List<MerchantPGDetails> listOfMerchantPGDetails;
	List<MerchantPGServices> listOfMerchantPGServices;
	
	

}
