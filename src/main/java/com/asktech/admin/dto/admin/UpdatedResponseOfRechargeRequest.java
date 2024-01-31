package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.asktech.admin.model.RechargeRequestDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatedResponseOfRechargeRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3760803770021031130L;
	private int status;
	private String message;
	private RechargeRequestDetails rechargeRequestDetails;

}
