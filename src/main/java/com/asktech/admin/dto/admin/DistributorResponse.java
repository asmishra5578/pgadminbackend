package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.Set;

import com.asktech.admin.model.MerchantDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributorResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2831045042208780985L;
	private String distributorId;
	private String uuid;
	private String createdBy;
	private String updatedBy;
	private String distributorName;
	private String distributorEMail;
	private String userStatus;
	private String phoneNumber;
	private String kycStatus;
	private String appId;
	private String secretId;
	private String supportEmailId;
	private String supportPhoneNo;
	private String distributorType;
	private String companyName;
	private String saltKey;
	private String tr_mail_flag;
	private String logoUrl;
	private String password;
	private Set<MerchantDetails> merchantDetails; 

}
