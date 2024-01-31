package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.MerchantDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllMerchantsAssociatedWithADistributorByDistributorIDResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8822582904363019655L;
	private int status;
	private String message;
	private List<MerchantDetails> listOfMerchantDetails;
	public AllMerchantsAssociatedWithADistributorByDistributorIDResponse(int status, String message,
			List<MerchantDetails> listOfMerchantDetails) {
		super();
		this.status = status;
		this.message = message;
		this.listOfMerchantDetails = listOfMerchantDetails;
	}
	

}
