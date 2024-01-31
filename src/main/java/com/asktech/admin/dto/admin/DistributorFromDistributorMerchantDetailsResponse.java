package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.asktech.admin.model.DistributorDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**@author abhimanyu-kumar*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributorFromDistributorMerchantDetailsResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877576256205465764L;
	private int status;
	private String message;
	
	private DistributorDetails distributorDetails;


}
