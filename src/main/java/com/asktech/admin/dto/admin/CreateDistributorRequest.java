package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateDistributorRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5782909253206136708L;
	private String distributorName;
	private String phoneNumber;
	private String distributorEmailId;
	private String kycStatus;
	private String companyName;
	private String supportEmailId;
	private String supportPhoneNo;
	private String distributorType;
	private String logoUrl;

}
