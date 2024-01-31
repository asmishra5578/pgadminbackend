package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.asktech.admin.model.AddMerchantByDistributorRequest;
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
public class AddMerchantByDistributorResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -429687249366957934L;
	private int status;
	private String message;
	private AddMerchantByDistributorRequest addMerchantByDistributorRequest;

}
