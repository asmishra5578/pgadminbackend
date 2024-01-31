package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**@author abhimanyu-kumar*/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RechargeRequestedResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9119424548646923674L;
	private String message;
	private int status;
	private long id;
	private String createdBy;
	private String updatedBy;
	private String merchantID;
	private String distributorID;
	private String uuid;
	private long amount;
	private String notes;// you can use clob
	private String UTR;
	private String created;
	private String updated;
	private String requestedStatus;
	private String approval;
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	private String info5;

}
