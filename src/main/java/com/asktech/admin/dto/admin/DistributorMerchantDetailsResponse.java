package com.asktech.admin.dto.admin;


import java.io.Serializable;

import com.asktech.admin.model.DistributorMerchantDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributorMerchantDetailsResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1898010321276746956L;
	private int status;
	private String message;
	private DistributorMerchantDetails distributorMerchantDetails;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public DistributorMerchantDetails getDistributorMerchantDetails() {
		return distributorMerchantDetails;
	}
	public void setDistributorMerchantDetails(DistributorMerchantDetails distributorMerchantDetails) {
		this.distributorMerchantDetails = distributorMerchantDetails;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
