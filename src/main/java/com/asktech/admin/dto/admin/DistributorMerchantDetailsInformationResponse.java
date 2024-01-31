package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.DistributorMerchantDetails;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter

@NoArgsConstructor
//@AllArgsConstructor
public class DistributorMerchantDetailsInformationResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3622569646769977854L;
	/**@author abhimanyu-kumar*/
	
	private int status ;
	private String message;
	private List<DistributorMerchantDetails> listOfDistributorMerchantDetails;
	public DistributorMerchantDetailsInformationResponse(int status,String message,
			List<DistributorMerchantDetails> listOfDistributorMerchantDetails) {
		super();
		this.status = status;
		this.message = message;
		this.listOfDistributorMerchantDetails = listOfDistributorMerchantDetails;
	}
	
	


}
