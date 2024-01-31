package com.asktech.admin.dto.admin;

import java.io.Serializable;

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
public class MerchantCreateRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -828990056726272805L;
	private String merchantName;
	private String phoneNumber;
	private String emailId;
    private String kycStatus;
	private String companyName;
	private String companyType;
	private String supportEmailId;
	private String supportPhoneNo;
	private String merchantType;
	private String logoUrl;
}
