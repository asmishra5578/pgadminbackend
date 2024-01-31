package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.DistributorDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;


/**@author abhimanyu-kumar*/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributorDetailInformationsResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 40333994795258265L;
	private int status;
	private String message;
	private List<DistributorDetails> listOfDistributorDetails;
	public DistributorDetailInformationsResponse() {}
	public DistributorDetailInformationsResponse(int status, String message,
			List<com.asktech.admin.model.DistributorDetails> listOfDistributorDetails2) {
		super();
		this.status = status;
		this.message = message;
		this.listOfDistributorDetails = listOfDistributorDetails2;
	}
	public void setListOfDistributorDetails(
			List<com.asktech.admin.model.DistributorDetails> listOfDistributorDetails2) {
		// TODO Auto-generated method stub
		
	}
	


}
